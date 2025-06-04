package backend.techeerzip.domain.role.entity;

public enum RoleType {
    ADMIN ("ROLE_ADMIN"),
    MENTOR ("ROLE_MENTOR"),
    USER ("ROLE_USER"),
    COMPANY ("ROLE_COMPANY"),
    BOOTCAMP ("ROLE_BOOTCAMP");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
