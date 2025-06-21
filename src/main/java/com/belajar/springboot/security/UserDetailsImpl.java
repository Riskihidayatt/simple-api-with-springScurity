package com.belajar.springboot.security;

import com.belajar.springboot.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String username;
    private final String email;
    private final String fullName;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    // Ambil dari entity User
    private final boolean isEnabled;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;

    private UserDetailsImpl(UUID id, String username, String email, String fullName, String password,
                            Collection<? extends GrantedAuthority> authorities, boolean isEnabled,
                            boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.authorities = authorities;
        this.isEnabled = isEnabled;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // Pastikan field boolean di User tidak null, jika bisa null beri default
        boolean enabled = user.getIsEnabled() != null ? user.getIsEnabled() : true;
        boolean accountNonExpired = user.getIsAccountNonExpired() != null ? user.getIsAccountNonExpired() : true;
        boolean accountNonLocked = user.getIsAccountNonLocked() != null ? user.getIsAccountNonLocked() : true;
        boolean credentialsNonExpired = user.getIsCredentialsNonExpired() != null ? user.getIsCredentialsNonExpired() : true;


        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPassword(),
                authorities,
                enabled,
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
