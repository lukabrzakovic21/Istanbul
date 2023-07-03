package com.master.istanbul.converter;

import com.master.istanbul.common.dto.RegistrationRequestDTO;
import com.master.istanbul.common.model.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class RegistrationRequestConverter {

    public RegistrationRequest dtoToModel(RegistrationRequestDTO dto) {

        return RegistrationRequest.builder()
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
                .build();
    }

    public RegistrationRequestDTO modelToDto(RegistrationRequest model) {

        return RegistrationRequestDTO.builder()
                .publicId(model.getPublicId())
                .city(model.getCity())
                .country(model.getCountry())
                .email(model.getEmail())
                .firstname(model.getFirstname())
                .lastname(model.getLastname())
                .password(model.getPassword())
                .phone(model.getPhone())
                .status(model.getStatus())
                .build();
    }

}
