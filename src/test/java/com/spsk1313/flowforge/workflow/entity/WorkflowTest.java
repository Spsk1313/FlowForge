package com.spsk1313.flowforge.workflow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WorkflowTest {

    @Test
    void shouldTrimSurroundingWhitespaceFromName() {
        Workflow workflow = new Workflow("   Process Orders   ", "Processes incoming orders");

        assertEquals("Process Orders", workflow.getName());
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Workflow("   ", "Processes incoming orders"));
    }

    @Test
    void shouldAcceptNameExactly100CharactersAfterNormalization() {
        String name = "   %s   ".formatted("a".repeat(100));

        Workflow workflow = new Workflow(name, null);

        assertEquals(name.strip(), workflow.getName());
        assertEquals(100, workflow.getName().length());
    }

    @Test
    void shouldRejectNameLongerThan100CharactersAfterNormalization() {
        String name = "a".repeat(101);

        assertThrows(IllegalArgumentException.class, () -> new Workflow(name, "Processes incoming orders"));
    }

    @Test
    void shouldTrimSurroundingWhitespaceFromDescription() {
        Workflow workflow = new Workflow("Process Orders", "   Processes incoming orders   ");

        assertEquals("Processes incoming orders", workflow.getDescription());
    }

    @Test
    void rename_ShouldNormalizeAndUpdateName() {
        Workflow workflow = new Workflow("Old Name", "Description");

        workflow.rename("   New Name   ");

        assertEquals("New Name", workflow.getName());
    }

    @Test
    void rename_ShouldRejectBlankName() {
        Workflow workflow = new Workflow("Old Name", "Description");

        assertThrows(IllegalArgumentException.class, () -> workflow.rename("   "));
    }

    @Test
    void updateDescription_ShouldNormalizeAndUpdateDescription() {
        Workflow workflow = new Workflow("Workflow", "Old Description");

        workflow.updateDescription("   New Description   ");

        assertEquals("New Description", workflow.getDescription());
    }

    @Test
    void updateDescription_ShouldAllowNull() {
        Workflow workflow = new Workflow("Workflow", "Old Description");

        workflow.updateDescription(null);

        assertNull(workflow.getDescription());
    }

    @Test
    void updateDescription_ShouldRejectDescriptionLongerThan500Characters() {
        Workflow workflow = new Workflow("Workflow", "Old Description");

        String invalidDescription = "a".repeat(501);

        assertThrows(IllegalArgumentException.class, () -> workflow.updateDescription(invalidDescription));
    }
}
