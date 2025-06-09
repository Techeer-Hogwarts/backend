package backend.techeerzip.domain.auth.jwt;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import backend.techeerzip.domain.role.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final RoleType role;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(Long userId, String email, String password, RoleType role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority(role.getRoleName().toUpperCase()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
