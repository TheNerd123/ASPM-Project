package com.teamvoid.aspm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoid.aspm.service.SemgrepResultParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SemgrepResultParserTest {

    private final SemgrepResultParser parser = new SemgrepResultParser(new ObjectMapper());

    @Test
    void parsesFindingLocationSeverityAndConfidence() throws Exception {
        String json = """
                {
                  "results": [{
                    "check_id": "java.lang.security.audit.xss",
                    "path": "src/Main.java",
                    "start": {"line": 10, "col": 3},
                    "end": {"line": 10, "col": 20},
                    "extra": {
                      "message": "Potential cross-site scripting issue",
                      "severity": "WARNING",
                      "metadata": {"confidence": "HIGH"}
                    }
                  }],
                  "errors": []
                }
                """;

        SemgrepResultParser.ParsedResults parsed = parser.parse(json);

        assertEquals(1, parsed.findings().size());
        assertEquals("java.lang.security.audit.xss", parsed.findings().get(0).checkId());
        assertEquals("src/Main.java", parsed.findings().get(0).path());
        assertEquals(10, parsed.findings().get(0).startLine());
        assertEquals("WARNING", parsed.findings().get(0).severity());
        assertEquals("HIGH", parsed.findings().get(0).confidence());
        assertEquals(0, parsed.errors().size());
    }

    @Test
    void parsesSemgrepErrors() throws Exception {
        SemgrepResultParser.ParsedResults parsed = parser.parse(
                "{\"results\":[],\"errors\":[{\"message\":\"Could not parse file\"}]}" );

        assertEquals(0, parsed.findings().size());
        assertEquals(1, parsed.errors().size());
        assertEquals("Could not parse file", parsed.errors().get(0));
    }
}
