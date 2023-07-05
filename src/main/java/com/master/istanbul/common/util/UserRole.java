package com.master.istanbul.common.util;

public enum UserRole {
    ADMIN,
    CUSTOMER,
    VENDOR;

    public static UserRole fromString(String role) {
        return valueOf(role.toUpperCase());
    }

    public static boolean check(String role){
        if(role == null || role.isEmpty()) {
            return false;
        }

        for (UserRole type : UserRole.values()) {
            if(type.name().equalsIgnoreCase(role)) {
                return true;
            }
        }

        return false;
    }
}
