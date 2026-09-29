package com.spsk1313.flowforge.workflow.repository;

import com.spsk1313.flowforge.workflow.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}
