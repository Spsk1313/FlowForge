package com.spsk1313.flowforge.workflow.exception;

public class WorkflowNotEditableException extends RuntimeException {
    public WorkflowNotEditableException() {
        super("Active workflows cannot be edited");
    }
}
