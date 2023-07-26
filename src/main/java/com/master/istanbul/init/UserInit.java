package com.master.istanbul.init;

import com.master.istanbul.common.dto.UserDTO;
import com.master.istanbul.common.util.PasswordEncoder;
import com.master.istanbul.common.util.UserRole;
import com.master.istanbul.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserInit {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    public UserInit(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void insertUsers() {

        var userAdmin = UserDTO.builder()
                .firstname("Luka")
                .lastname("Brzakovic")
                .email("lukabrzakovic21+admin@gmail.com")
                .city("Uzice")
                .country("Srbija")
                .password(passwordEncoder.encodePassword("pipula33@V"))
                .phone("0606867000")
                .role(UserRole.ADMIN)
                .build();

        var userVendor = UserDTO.builder()
                .firstname("Luka")
                .lastname("Brzakovic")
                .email("lukabrzakovic21+vendor@gmail.com")
                .city("Uzice")
                .country("Srbija")
                .password(passwordEncoder.encodePassword("pipula33@V"))
                .phone("0606867000")
                .role(UserRole.VENDOR)
                .build();

        var userCustomer = UserDTO.builder()
                .firstname("Luka")
                .lastname("Brzakovic")
                .email("lukabrzakovic21+customer@gmail.com")
                .city("Uzice")
                .country("Srbija")
                .password(passwordEncoder.encodePassword("pipula33@V"))
                .phone("0606867000")
                .role(UserRole.CUSTOMER)
                .build();

        userService.createUser(userAdmin);
        userService.createUser(userVendor);
        userService.createUser(userCustomer);
    }

}
