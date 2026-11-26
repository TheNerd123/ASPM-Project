package com.teamvoid.aspm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoid.aspm.api.SemgrepFinding;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Converts Semgrep's JSON output into the platform's stable finding shape. */
@Component
public class SemgrepResultParser {

    private final ObjectMapper objectMapper;

    public SemgrepResultParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ParsedResults parse(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        if (root == null || !root.isObject()) {
            throw new IOException("Semgrep returned an invalid JSON document");
        }

        List<SemgrepFinding> findings = new ArrayList<>();
        JsonNode results = root.path("results");
        if (results.isArray()) {
            for (JsonNode result : results) {
                JsonNode start = result.path("start");
                JsonNode end = result.path("end");
                JsonNode extra = result.path("extra");
                JsonNode metadata = extra.path("metadata");
                findings.add(new SemgrepFinding(
                        text(result, "check_id"),
                        text(result, "path"),
                        integer(start, "line"),
                        integer(start, "col"),
                        integer(end, "line"),
                        integer(end, "col"),
                        text(extra, "message"),
                        text(extra, "severity"),
                        text(metadata, "confidence")));
            }
        }

        List<String> errors = new ArrayList<>();
        JsonNode semgrepErrors = root.path("errors");
        if (semgrepErrors.isArray()) {
            for (JsonNode error : semgrepErrors) {
                String message = text(error, "message");
                if (message != null && !message.isBlank()) {
                    errors.add(message);
                }
            }
        }
        return new ParsedResults(List.copyOf(findings), List.copyOf(errors));
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private Integer integer(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isIntegralNumber() ? value.intValue() : null;
    }

    public record ParsedResults(List<SemgrepFinding> findings, List<String> errors) {
    }
}
