package model;

public enum Role {
    ADMIN("Admin - Full Access"),
    DETECTIVE("Detective - Investigation Access"),
    OFFICER("Officer - Limited Access"),
    ANALYST("Analyst - View Only");

    private String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
