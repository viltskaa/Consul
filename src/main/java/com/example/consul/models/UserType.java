package com.example.consul.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    NONE,
    ADMINISTRATOR,
    ACCOUNTING;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
