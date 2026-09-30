package com.spsk1313.flowforge.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWorkflowRequest(
        @NotBlank @Size(max = 100) String name,

        @Size(max = 500) String description) {
    public UpdateWorkflowRequest {
        if (name != null) {
            name = name.strip();
        }

        if (description != null) {
            description = description.strip();
        }
    }
}
