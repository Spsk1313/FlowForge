package com.spsk1313.flowforge.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.spsk1313.flowforge.workflow.dto.WorkflowResponse;
import com.spsk1313.flowforge.workflow.entity.Workflow;
import com.spsk1313.flowforge.workflow.entity.WorkflowStatus;
import com.spsk1313.flowforge.workflow.repository.WorkflowRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @InjectMocks
    private WorkflowService workflowService;

    @Test
    void getAllWorkflows_ShouldRequestCreatedAtAscendingThenIdAscendingAndPreserveOrder() {
        Instant earlier = Instant.parse("2026-09-29T20:00:00Z");
        Instant later = Instant.parse("2026-09-29T21:00:00Z");

        Workflow first = mockWorkflow(1L, "First", "First Description", earlier);

        Workflow second = mockWorkflow(2L, "Second", "Second Description", earlier);

        Workflow third = mockWorkflow(3L, "Third", "Third Description", later);

        Sort expectedSort = Sort.by("createdAt").ascending().and(Sort.by("id").ascending());

        given(workflowRepository.findAll(expectedSort)).willReturn(List.of(first, second, third));

        List<WorkflowResponse> result = workflowService.getAllWorkflows();

        assertThat(result).extracting(WorkflowResponse::id).containsExactly(1L, 2L, 3L);

        assertThat(result).extracting(WorkflowResponse::createdAt).containsExactly(earlier, earlier, later);

        then(workflowRepository).should().findAll(expectedSort);
    }

    private Workflow mockWorkflow(Long id, String name, String description, Instant createdAt) {

        Workflow workflow = mock(Workflow.class);
        given(workflow.getId()).willReturn(id);
        given(workflow.getName()).willReturn(name);
        given(workflow.getDescription()).willReturn(description);
        given(workflow.getStatus()).willReturn(WorkflowStatus.DRAFT);
        given(workflow.getCreatedAt()).willReturn(createdAt);

        return workflow;
    }
}
