package com.hsu.shimpyoo.domain.medicine.enums;

public enum MealTiming {
    BEFORE_MEAL("식전"),
    AFTER_MEAL("식후");

    private final String description;

    MealTiming(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
