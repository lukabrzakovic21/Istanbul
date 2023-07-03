package com.master.istanbul.converter;

import com.master.istanbul.common.dto.RegistrationRequestDTO;
import com.master.istanbul.common.dto.UserDTO;
import com.master.istanbul.common.dto.UserUpdateDTO;
import com.master.istanbul.common.model.RegistrationRequest;
import com.master.istanbul.common.model.User;
import com.master.istanbul.common.util.UserRole;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
public class UserConverter {

    public User dtoToModel(UserDTO dto) {

        return User.builder()
                .publicId(UUID.randomUUID())
                .city(dto.getCity())
                .country(dto.getCountry())
                .createdAt(Timestamp.from(Instant.now()))
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .status(dto.getStatus())
                .role(dto.getRole())
                .build();
    }

    public UserDTO modelToDto(User model) {

        return UserDTO.builder()
                .publicId(model.getPublicId())
                .city(model.getCity())
                .country(model.getCountry())
                .email(model.getEmail())
                .firstname(model.getFirstname())
                .lastname(model.getLastname())
                .password(model.getPassword())
                .phone(model.getPhone())
                .status(model.getStatus())
                .role(model.getRole())
                .build();
    }

    public UserDTO registrationRequestToUser(RegistrationRequest model) {

        return UserDTO.builder()
                .city(model.getCity())
                .country(model.getCountry())
                .email(model.getEmail())
                .firstname(model.getFirstname())
                .lastname(model.getLastname())
                .password(model.getPassword())
                .phone(model.getPhone())
                .role(model.getRole())
                .build();
    }

    public void updateSomeUserFields(UserUpdateDTO dto, User model) {
        if(Objects.nonNull(dto.getCity())) {
            model.setCity(dto.getCity());
        }
        if(Objects.nonNull(dto.getCountry())) {
            model.setCountry(dto.getCountry());
        }
        if(Objects.nonNull(dto.getFirstname())) {
            model.setFirstname(dto.getFirstname());
        }
        if(Objects.nonNull(dto.getLastname())) {
            model.setLastname(dto.getLastname());
        }
        if(Objects.nonNull(dto.getPhone())) {
            model.setPhone(dto.getPhone());
        }
    }
}
