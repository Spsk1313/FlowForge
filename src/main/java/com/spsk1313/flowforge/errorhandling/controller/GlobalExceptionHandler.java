package com.spsk1313.flowforge.errorhandling.controller;

import com.spsk1313.flowforge.workflow.exception.WorkflowNotEditableException;
import com.spsk1313.flowforge.workflow.exception.WorkflowNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<Void> handleWorkflowNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(WorkflowNotEditableException.class)
    public ResponseEntity<Void> handleWorkflowNotEditable() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
