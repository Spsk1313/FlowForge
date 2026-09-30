package com.spsk1313.flowforge.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.spsk1313.flowforge.workflow.dto.UpdateWorkflowRequest;
import com.spsk1313.flowforge.workflow.entity.Workflow;
import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import com.spsk1313.flowforge.workflow.exception.WorkflowNotEditableException;
import com.spsk1313.flowforge.workflow.repository.WorkflowRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Import(WorkflowService.class)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class WorkflowServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowRepository workflowRepository;

    @BeforeEach
    void cleanDatabase() {
        workflowRepository.deleteAllInBatch();
    }

    @Test
    void updateWorkflow_ShouldPersistChangesWithoutChangingSystemControlledFields() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Old Name", "Old Description"));

        Long id = original.getId();
        WorkflowStatus originalStatus = original.getStatus();
        Instant originalCreatedAt = original.getCreatedAt();

        UpdateWorkflowRequest request = new UpdateWorkflowRequest("New Name", "New Description");

        workflowService.updateWorkflow(id, request);

        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertThat(persisted.getName()).isEqualTo("New Name");

        assertThat(persisted.getDescription()).isEqualTo("New Description");

        assertThat(persisted.getId()).isEqualTo(id);

        assertThat(persisted.getStatus()).isEqualTo(originalStatus);

        assertThat(persisted.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    void updateWorkflow_ShouldPersistNullDescription() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Original Name", "Original Description"));

        Long id = original.getId();

        UpdateWorkflowRequest request = new UpdateWorkflowRequest("Updated Name", null);

        workflowService.updateWorkflow(id, request);

        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertThat(persisted.getName()).isEqualTo("Updated Name");

        assertThat(persisted.getDescription()).isNull();
    }

    @Test
    void updateWorkflow_ShouldRollbackAllChangesWhenSecondMutationFails() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Old Name", "Old Description"));

        Long id = original.getId();

        UpdateWorkflowRequest request = new UpdateWorkflowRequest("New Name", "a".repeat(501));

        assertThatThrownBy(() -> workflowService.updateWorkflow(id, request))
                .isInstanceOf(IllegalArgumentException.class);

        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertThat(persisted.getName()).isEqualTo("Old Name");

        assertThat(persisted.getDescription()).isEqualTo("Old Description");
    }

    @Test
    void activateWorkflow_ShouldPersistActiveStatus() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Test Workflow", "Test Description"));

        Long id = original.getId();
        Instant originalCreatedAt = original.getCreatedAt();

        assertThat(original.getStatus()).isEqualTo(WorkflowStatus.DRAFT);

        workflowService.activate(id);

        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertThat(persisted.getStatus()).isEqualTo(WorkflowStatus.ACTIVE);
        assertThat(persisted.getId()).isEqualTo(id);
        assertThat(persisted.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    void activateWorkflow_ShouldRemainActiveWhenAlreadyActive() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Test Workflow", "Test Description"));

        Long id = original.getId();

        workflowService.activate(id);

        Workflow firstRead = workflowRepository.findById(id).orElseThrow();

        assertThat(firstRead.getStatus()).isEqualTo(WorkflowStatus.ACTIVE);

        workflowService.activate(id);

        Workflow secondRead = workflowRepository.findById(id).orElseThrow();

        assertThat(secondRead.getStatus()).isEqualTo(WorkflowStatus.ACTIVE);
    }

    @Test
    void updateWorkflow_ShouldLeavePersistedStateUnchangedWhenWorkflowIsActive() {
        Workflow original = workflowRepository.saveAndFlush(new Workflow("Old Name", "Old Description"));

        Long id = original.getId();

        workflowService.activate(id);

        Workflow active = workflowRepository.findById(id).orElseThrow();

        assertThat(active.getStatus()).isEqualTo(WorkflowStatus.ACTIVE);

        UpdateWorkflowRequest request = new UpdateWorkflowRequest("New Name", "New Description");

        assertThatThrownBy(() -> workflowService.updateWorkflow(id, request))
                .isInstanceOf(WorkflowNotEditableException.class);

        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertThat(persisted.getName()).isEqualTo("Old Name");
        assertThat(persisted.getDescription()).isEqualTo("Old Description");
        assertThat(persisted.getStatus()).isEqualTo(WorkflowStatus.ACTIVE);
    }
}
