package com.teamvoid.aspm.service;

import com.teamvoid.aspm.api.SemgrepScanRequest;
import com.teamvoid.aspm.api.SemgrepScanResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Runs the locally installed Semgrep CLI and normalizes its JSON output. */
@Service
public class SemgrepScanService {

    private final String semgrepCommand;
    private final String defaultConfig;
    private final Duration timeout;
    private final SemgrepResultParser resultParser;

    public SemgrepScanService(
            @Value("${app.semgrep.command:semgrep}") String semgrepCommand,
            @Value("${app.semgrep.config:auto}") String defaultConfig,
            @Value("${app.semgrep.timeout-seconds:300}") long timeoutSeconds,
            SemgrepResultParser resultParser) {
        this.semgrepCommand = semgrepCommand;
        this.defaultConfig = defaultConfig;
        this.timeout = Duration.ofSeconds(Math.max(1, timeoutSeconds));
        this.resultParser = resultParser;
    }

    public SemgrepScanResponse scan(SemgrepScanRequest request) {
        Path projectPath = validateProjectPath(request.projectPath());
        String config = request.config() == null || request.config().isBlank()
                ? defaultConfig
                : request.config().trim();
        UUID scanId = UUID.randomUUID();
        long started = System.nanoTime();

        List<String> command = List.of(
                semgrepCommand,
                "scan",
                "--config", config,
                "--json",
                projectPath.toString());

        ProcessResult processResult = run(command);
        if (processResult.exitCode() != 0) {
            String detail = processResult.stderr().isBlank()
                    ? "Semgrep exited with code " + processResult.exitCode()
                    : processResult.stderr().trim();
            throw new SemgrepScanException(detail);
        }

        try {
            SemgrepResultParser.ParsedResults parsed = resultParser.parse(processResult.stdout());
            return new SemgrepScanResponse(
                    scanId,
                    "COMPLETED",
                    projectPath.toString(),
                    parsed.findings().size(),
                    parsed.findings(),
                    parsed.errors(),
                    elapsedMillis(started));
        } catch (IOException exception) {
            throw new SemgrepScanException("Semgrep returned invalid JSON: " + exception.getMessage(), exception);
        }
    }

    private Path validateProjectPath(String rawPath) {
        try {
            Path path = Path.of(rawPath).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                throw new InvalidProjectPathException("Project path does not exist: " + rawPath);
            }
            if (!Files.isDirectory(path)) {
                throw new InvalidProjectPathException("Project path is not a directory: " + rawPath);
            }
            if (!Files.isReadable(path)) {
                throw new InvalidProjectPathException("Project path is not readable: " + rawPath);
            }
            return path;
        } catch (InvalidProjectPathException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidProjectPathException("Invalid project path: " + rawPath, exception);
        }
    }

    private ProcessResult run(List<String> command) {
        ExecutorService streamReaders = Executors.newFixedThreadPool(2);
        try {
            Process process;
            try {
                process = new ProcessBuilder(command).start();
            } catch (IOException exception) {
                throw new SemgrepScanException(
                        "Could not start Semgrep. Install it or set app.semgrep.command.", exception);
            }

            Future<String> stdout = streamReaders.submit(
                    () -> new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            Future<String> stderr = streamReaders.submit(
                    () -> new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));

            try {
                if (!process.waitFor(timeout.toSeconds(), TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    throw new SemgrepScanException("Semgrep scan timed out after " + timeout.toSeconds() + " seconds");
                }
                return new ProcessResult(process.exitValue(), stdout.get(), stderr.get());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
                throw new SemgrepScanException("Semgrep scan was interrupted", exception);
            } catch (Exception exception) {
                if (exception instanceof SemgrepScanException semgrepException) {
                    throw semgrepException;
                }
                throw new SemgrepScanException("Could not read Semgrep output", exception);
            }
        } finally {
            streamReaders.shutdownNow();
        }
    }

    private long elapsedMillis(long started) {
        return Duration.ofNanos(System.nanoTime() - started).toMillis();
    }

    private record ProcessResult(int exitCode, String stdout, String stderr) {
    }

    public static class InvalidProjectPathException extends RuntimeException {
        public InvalidProjectPathException(String message) {
            super(message);
        }

        public InvalidProjectPathException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SemgrepScanException extends RuntimeException {
        public SemgrepScanException(String message) {
            super(message);
        }

        public SemgrepScanException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
