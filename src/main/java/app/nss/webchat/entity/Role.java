package app.nss.webchat.entity;

public enum Role {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
