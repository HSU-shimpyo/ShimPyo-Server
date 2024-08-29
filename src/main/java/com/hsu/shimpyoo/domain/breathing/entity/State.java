package com.hsu.shimpyoo.domain.breathing.entity;

public enum State {
    GOOD("양호"),
    WARNING("경고"),
    DANGER("위험");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
