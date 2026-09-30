package com.spsk1313.flowforge.workflow.controller;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.UpdateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@Valid @RequestBody CreateWorkflowRequest req) {
        WorkflowResponse response = workflowService.createWorkflow(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResponse> getWorkflowById(@PathVariable Long id) {
        WorkflowResponse response = workflowService.getWorkflowById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> getAllWorkflows() {
        List<WorkflowResponse> response = workflowService.getAllWorkflows();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowResponse> updateWorkflow(
            @PathVariable Long id, @Valid @RequestBody UpdateWorkflowRequest req) {
        WorkflowResponse response = workflowService.updateWorkflow(id, req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<WorkflowResponse> activate(@PathVariable Long id) {
        WorkflowResponse response = workflowService.activate(id);
        return ResponseEntity.ok(response);
    }
}
