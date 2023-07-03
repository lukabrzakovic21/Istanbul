package com.master.istanbul.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class PasswordEncoder {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordEncoder() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
    }
    public String encodePassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    public boolean verifyPassword(String plainPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(plainPassword, encodedPassword);
    }

}
