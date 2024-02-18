package com.togather.member.model;

public enum Role {
    GUEST, HOST, ADMIN;

    public static Role from(String role) {
        return Role.valueOf(role);
    }
}
