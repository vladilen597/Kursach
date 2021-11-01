package com.vladien.kursovaya.kursovaya.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_CLIENT, ROLE_MENTOR, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
