package com.togather.member.model;

import java.util.HashMap;

public enum Role {
    GUEST, HOST, ADMIN, NONE;

    private static final HashMap<String, Role> roles = new HashMap<>();

    static {
        for (Role role : Role.values())
            roles.put(role.name(), role);
    }

    public static Role from(String role) {
        return roles.getOrDefault(role, NONE);
    }

}
