package com.rpgate.model;

public enum Category {
    LEAVE("Leave"), TD_POSTING("TD / Posting"), VEHICLE("Vehicle In / Out"), OUTPASS("Outpass"), NIGHT_PASS("Night Pass");
    private final String value;
    Category(String value) { this.value = value; }
    public String value() { return value; }
    public static Category fromValue(String value) { for (Category category : values()) if (category.value.equals(value)) return category; throw new IllegalArgumentException("Unknown category: " + value); }
}
