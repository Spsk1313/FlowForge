package com.spsk1313.flowforge.workflow.service;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.Workflow;
import com.spsk1313.flowforge.workflow.exception.WorkflowNotFoundException;
import com.spsk1313.flowforge.workflow.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    public WorkflowResponse createWorkflow(CreateWorkflowRequest req) {
        Workflow workflow = new Workflow(req.name(), req.description());
        Workflow saved = workflowRepository.save(workflow);
        return toResponse(saved);
    }

    public WorkflowResponse getWorkflowById(Long id) {
        Workflow workflow = workflowRepository.findById(id).orElseThrow(() -> new WorkflowNotFoundException(id));
        return toResponse(workflow);
    }

    private WorkflowResponse toResponse(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getDescription(),
                workflow.getStatus(),
                workflow.getCreatedAt());
    }
}
