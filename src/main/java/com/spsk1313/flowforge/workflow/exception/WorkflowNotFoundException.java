package com.spsk1313.flowforge.workflow.exception;

public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(Long id) {

        super("Workflow with id %d not found".formatted(id));
    }
}
