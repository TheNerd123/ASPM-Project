
package com.teamvoid.aspm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void frontendHealthRouteWorks() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void initialPlatformResourcesAreAvailable() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/scanners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Semgrep")));

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications", greaterThanOrEqualTo(1)));
    }

    @Test
    void canCreateApplicationAndQueueScan() throws Exception {
        String application = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Customer Portal\",\"repositoryUrl\":\"https://github.com/teamvoid/customer-portal\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Customer Portal")))
                .andReturn().getResponse().getContentAsString();

        String applicationId = com.jayway.jsonpath.JsonPath.read(application, "$.id");
        String scannerId = com.jayway.jsonpath.JsonPath.read(
                mockMvc.perform(get("/api/scanners")).andReturn().getResponse().getContentAsString(), "$[0].id");

        mockMvc.perform(post("/api/scans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applicationId\":\"" + applicationId + "\",\"scannerId\":\"" + scannerId + "\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is("QUEUED")));
    }
}
