package com.master.istanbul.common.util;

public enum RegistrationRequestStatus {
    CREATED,
    ACCEPTED,
    REJECTED;

    public static RegistrationRequestStatus fromString(String registrationRequestStatus) {
        return valueOf(registrationRequestStatus.toUpperCase());
    }

    public static boolean check(String registrationRequestStatus){
        if(registrationRequestStatus == null || registrationRequestStatus.isEmpty()) {
            return false;
        }

        for (RegistrationRequestStatus type : RegistrationRequestStatus.values()) {
            if(type.name().equalsIgnoreCase(registrationRequestStatus)) {
                return true;
            }
        }
        return false;
    }
}
