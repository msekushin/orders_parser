package com.home.model;

import static org.springframework.util.ObjectUtils.isEmpty;

public enum Currency {
    USD,
    EUR;

    public static Currency valueOfByType(String type) {
        return !isEmpty(type) ? valueOf(type.toUpperCase()) : null;
    }
}
