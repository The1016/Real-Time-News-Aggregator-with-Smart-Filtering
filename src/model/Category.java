package model;

public enum Category {
    BUSINESS("Business"),
    TECHNOLOGY("Technology"),
    SPORTS("Sports"),
    HEALTH("Health");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}