package com.master.istanbul.common.util;

public enum UserStatus {
    ACTIVE,
    DEACTIVATED;

    public static UserStatus fromString(String status) {
        return valueOf(status.toUpperCase());
    }

    public static boolean check(String status){
        if(status == null || status.isEmpty()) {
            return false;
        }

        for (UserStatus type : UserStatus.values()) {
            if(type.name().equalsIgnoreCase(status)) {
                return true;
            }
        }

        return false;
    }
}
