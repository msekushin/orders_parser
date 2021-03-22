package com.home.model;

import java.util.Arrays;

public enum FileExt {
    CSV("csv"),
    JSON("json");

    private final String type;

    FileExt(String type) {
        this.type = type;
    }

    public static FileExt valueOfByType(String type) {
        return Arrays.stream(values())
                     .filter(t -> t.getType().equalsIgnoreCase(type))
                     .findFirst()
                     .orElse(null);
    }

    public String getType() {
        return type;
    }
}
