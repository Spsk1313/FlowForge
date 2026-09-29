package com.spsk1313.flowforge.workflow.dto;

import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import java.time.Instant;

public record WorkflowResponse(Long id, String name, String description, WorkflowStatus status, Instant createdAt) {}
