package com.spsk1313.flowforge.workflow.controller;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import com.spsk1313.flowforge.workflow.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvcTester mvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkflowService workflowService;

    @Test
    void createWorkflow_ShouldReturnWorkflowAnd201() throws Exception {
        CreateWorkflowRequest request =
                new CreateWorkflowRequest(
                        "testName",
                        "testDescription"
                );

        Instant createdAt = Instant.parse("2026-09-29T20:00:00Z");

        WorkflowResponse response =
                new WorkflowResponse(
                        1L,
                        "testName",
                        "testDescription",
                        WorkflowStatus.DRAFT,
                        createdAt
                );

        given(workflowService.createWorkflow(request))
                .willReturn(response);

        assertThat(
                mvcTester.post()
                        .uri("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                {
                  "id": 1,
                  "name": "testName",
                  "description": "testDescription",
                  "status": "DRAFT",
                  "createdAt": "2026-09-29T20:00:00Z"
                }
                """);
    }

    @Test
    void createWorkflow_ShouldReturn400_WhenNameIsBlank() throws Exception {
        CreateWorkflowRequest request =
                new CreateWorkflowRequest(
                        "   ",
                        "testDescription"
                );

        assertThat(
                mvcTester.post()
                        .uri("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .hasStatus(HttpStatus.BAD_REQUEST);

        then(workflowService)
                .should(never())
                .createWorkflow(any(CreateWorkflowRequest.class));

    }
}