package com.spsk1313.flowforge.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import com.spsk1313.flowforge.workflow.exception.WorkflowNotFoundException;
import com.spsk1313.flowforge.workflow.service.WorkflowService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

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
        CreateWorkflowRequest request = new CreateWorkflowRequest("testName", "testDescription");

        Instant createdAt = Instant.parse("2026-09-29T20:00:00Z");

        WorkflowResponse response =
                new WorkflowResponse(1L, "testName", "testDescription", WorkflowStatus.DRAFT, createdAt);

        given(workflowService.createWorkflow(request)).willReturn(response);

        assertThat(mvcTester
                        .post()
                        .uri("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
        CreateWorkflowRequest request = new CreateWorkflowRequest("   ", "testDescription");

        assertThat(mvcTester
                        .post()
                        .uri("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .hasStatus(HttpStatus.BAD_REQUEST);

        then(workflowService).should(never()).createWorkflow(any(CreateWorkflowRequest.class));
    }

    @Test
    void createWorkflow_ShouldAcceptRawNameThatNormalizesTo100Characters() {
        String normalizedName = "a".repeat(100);
        String rawName = "   " + normalizedName + "   ";

        String requestJson = """
            {
              "name": "%s",
              "description": "testDescription"
            }
            """.formatted(rawName);

        WorkflowResponse response = new WorkflowResponse(
                1L, normalizedName, "testDescription", WorkflowStatus.DRAFT, Instant.parse("2026-09-29T20:00:00Z"));

        CreateWorkflowRequest expectedRequest = new CreateWorkflowRequest(normalizedName, "testDescription");

        given(workflowService.createWorkflow(expectedRequest)).willReturn(response);

        assertThat(mvcTester
                        .post()
                        .uri("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .hasStatus(HttpStatus.CREATED);

        then(workflowService).should().createWorkflow(expectedRequest);
    }

    @Test
    void getWorkflowById_ShouldReturnWorkflowWhenValidId() {
        Long workflowId = 1L;
        Instant createdAt = Instant.parse("2026-09-29T20:00:00Z");

        WorkflowResponse response =
                new WorkflowResponse(workflowId, "testName", "testDescription", WorkflowStatus.DRAFT, createdAt);

        given(workflowService.getWorkflowById(workflowId)).willReturn(response);

        assertThat(mvcTester.get().uri("/api/workflows/{id}", workflowId).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
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

        then(workflowService).should().getWorkflowById(workflowId);
    }

    @Test
    void getWorkflowById_ShouldReturn404WhenWorkflowDoesNotExist() {
        Long workflowId = 999L;

        given(workflowService.getWorkflowById(workflowId)).willThrow(new WorkflowNotFoundException(workflowId));

        assertThat(mvcTester.get().uri("/api/workflows/{id}", workflowId).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.NOT_FOUND);

        then(workflowService).should().getWorkflowById(workflowId);
    }

    @Test
    void getAllWorkflows_ShouldReturnWorkflowsAnd200() {
        Instant firstCreatedAt = Instant.parse("2026-09-29T20:00:00Z");
        Instant secondCreatedAt = Instant.parse("2026-09-29T21:00:00Z");

        WorkflowResponse first =
                new WorkflowResponse(1L, "First Workflow", "First Description", WorkflowStatus.DRAFT, firstCreatedAt);

        WorkflowResponse second = new WorkflowResponse(
                2L, "Second Workflow", "Second Description", WorkflowStatus.DRAFT, secondCreatedAt);

        given(workflowService.getAllWorkflows()).willReturn(List.of(first, second));

        assertThat(mvcTester.get().uri("/api/workflows").accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                    [
                      {
                        "id": 1,
                        "name": "First Workflow",
                        "description": "First Description",
                        "status": "DRAFT",
                        "createdAt": "2026-09-29T20:00:00Z"
                      },
                      {
                        "id": 2,
                        "name": "Second Workflow",
                        "description": "Second Description",
                        "status": "DRAFT",
                        "createdAt": "2026-09-29T21:00:00Z"
                      }
                    ]
                    """);

        then(workflowService).should().getAllWorkflows();
    }

    @Test
    void getAllWorkflows_ShouldReturnEmptyListAnd200() {
        given(workflowService.getAllWorkflows()).willReturn(List.of());

        assertThat(mvcTester.get().uri("/api/workflows").accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("[]");

        then(workflowService).should().getAllWorkflows();
    }
}
