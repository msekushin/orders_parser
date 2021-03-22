package com.home.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyTest {

    @Test
    void valueOfByTypeSuccess() {
        assertEquals(Currency.EUR, Currency.valueOfByType("eur"));
    }

    @Test
    void valueOfByTypeEmpty() {
        assertNull(Currency.valueOfByType(""));
    }

    @Test
    void valueOfByTypeNull() {
        assertNull(Currency.valueOfByType(null));
    }

    @Test
    void valueOfByTypeThrow() {
        assertThrows(IllegalArgumentException.class, () -> Currency.valueOfByType("eur1"));
    }
}