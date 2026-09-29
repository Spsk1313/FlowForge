package com.spsk1313.flowforge.workflow.repository;

import com.spsk1313.flowforge.workflow.entity.Workflow;
import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        properties = "spring.jpa.hibernate.ddl-auto=validate"
)
@Testcontainers
public class WorkflowRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPersistAndRetrieveWorkflow() {
        Workflow workflow = new Workflow("Order Processing", "Processes incoming orders");
        Workflow saved = workflowRepository.saveAndFlush(workflow);
        Long id = saved.getId();
        entityManager.clear();
        Workflow persisted = workflowRepository.findById(id).orElseThrow();

        assertNotNull(persisted.getId());
        assertEquals("Order Processing", persisted.getName());
        assertEquals("Processes incoming orders", persisted.getDescription());
        assertEquals(WorkflowStatus.DRAFT, persisted.getStatus());
        assertNotNull(persisted.getCreatedAt());
    }

    @Test
    void shouldRejectBlankNameAtDatabaseLevel() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                                INSERT INTO workflows (name, description)
                                VALUES (?, ?)
                                """,
                        "   ",
                        "Processes incoming orders"
                )
        );
    }
}
