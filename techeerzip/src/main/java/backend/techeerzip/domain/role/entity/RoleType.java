package backend.techeerzip.domain.role.entity;

public enum RoleType {
    ADMIN("ROLE_ADMIN"),
    MENTOR("ROLE_MENTOR"),
    TECHEER("ROLE_TECHEER"),
    COMPANY("ROLE_COMPANY"),
    BOOTCAMP("ROLE_BOOTCAMP");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
