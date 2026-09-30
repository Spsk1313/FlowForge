package com.spsk1313.flowforge.workflow.entity;

import com.spsk1313.flowforge.workflow.exception.WorkflowNotEditableException;
import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "workflows")
public class Workflow {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = false, length = 20)
    private WorkflowStatus status;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected Workflow() {}

    public Workflow(String name, String description) {
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.status = WorkflowStatus.DRAFT;
    }

    private static String validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Workflow name must not be null");
        }

        String normalizedName = name.strip();

        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Workflow name must not be blank");
        }

        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Workflow name must not exceed " + MAX_NAME_LENGTH + " characters");
        }

        return normalizedName;
    }

    private static String validateDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalizedDescription = description.strip();

        if (normalizedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Workflow description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }

        return normalizedDescription;
    }

    public void rename(String name) {
        ensureEditable();
        this.name = validateName(name);
    }

    public void updateDescription(String description) {
        ensureEditable();
        this.description = validateDescription(description);
    }

    public void activate() {
        this.status = WorkflowStatus.ACTIVE;
    }

    private void ensureEditable() {
        if (status != WorkflowStatus.DRAFT) {
            throw new WorkflowNotEditableException();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
