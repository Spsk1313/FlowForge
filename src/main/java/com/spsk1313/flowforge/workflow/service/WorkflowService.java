package com.spsk1313.flowforge.workflow.service;

import com.spsk1313.flowforge.workflow.dto.CreateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.UpdateWorkflowRequest;
import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.Workflow;
import com.spsk1313.flowforge.workflow.exception.WorkflowNotFoundException;
import com.spsk1313.flowforge.workflow.repository.WorkflowRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<WorkflowResponse> getAllWorkflows() {
        return workflowRepository
                .findAll(Sort.by("createdAt").ascending().and(Sort.by("id").ascending()))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long id, UpdateWorkflowRequest req) {
        Workflow workflow = workflowRepository.findById(id).orElseThrow(() -> new WorkflowNotFoundException(id));

        workflow.rename(req.name());
        workflow.updateDescription(req.description());

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
