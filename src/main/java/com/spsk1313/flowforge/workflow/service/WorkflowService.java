package com.spsk1313.flowforge.workflow.service;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.Workflow;
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
        return new WorkflowResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
