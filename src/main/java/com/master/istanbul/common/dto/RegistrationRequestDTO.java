package com.master.istanbul.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.master.istanbul.common.util.RegistrationRequestStatus;
import com.master.istanbul.common.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegistrationRequestDTO {

    private UUID publicId;
    private String email;
    @ToString.Exclude
    private String password;
    private String firstname;
    private String lastname;
    private String country;
    private String city;
    private String phone;
    private RegistrationRequestStatus status;
    private UserRole role;

}
