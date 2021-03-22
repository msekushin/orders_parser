package com.home.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileExtTest {

    @Test
    void valueOfTypeSuccess() {
        assertEquals(FileExt.CSV, FileExt.valueOfByType("csv"));
    }

    @Test
    void valueOfTypeEmpty() {
        assertNull(FileExt.valueOfByType(""));
    }

    @Test
    void valueOfTypeNull() {
        assertNull(FileExt.valueOfByType(null));
    }

    @Test
    void valueOfTypeNotContains() {
        assertNull(FileExt.valueOfByType("docx"));
    }
}