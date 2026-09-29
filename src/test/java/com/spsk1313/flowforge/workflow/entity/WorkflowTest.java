package com.spsk1313.flowforge.workflow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
