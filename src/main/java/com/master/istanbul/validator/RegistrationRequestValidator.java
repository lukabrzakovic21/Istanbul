package com.master.istanbul.validator;

import com.master.istanbul.common.dto.RegistrationRequestDTO;
import com.master.istanbul.exception.custom.EmailAlreadyInUse;
import com.master.istanbul.exception.registration_request.RegistrationRequestBadRequest;
import com.master.istanbul.repository.RegistrationRequestRepository;
import com.master.istanbul.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.regex.Pattern.matches;

@Component
public class RegistrationRequestValidator {

    private final UserRepository userRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    public RegistrationRequestValidator(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository) {
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
    }


    public void checkFields(RegistrationRequestDTO requestDTO) {

        if(Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getCity()) || Objects.isNull(requestDTO.getCountry())
        || Objects.isNull(requestDTO.getEmail()) || Objects.isNull(requestDTO.getFirstname()) || Objects.isNull(requestDTO.getLastname())
        || Objects.isNull(requestDTO.getPassword()) || Objects.isNull(requestDTO.getPhone()) || Objects.isNull(requestDTO.getRole())) {
            throw new RegistrationRequestBadRequest("All fields are mandatory");
        }
        if(requestDTO.getEmail().isBlank() || requestDTO.getCountry().isBlank() || requestDTO.getFirstname().isBlank() || requestDTO.getLastname().isBlank()
        || requestDTO.getCity().isBlank() || requestDTO.getPhone().isBlank() || requestDTO.getPassword().isBlank() || requestDTO.getRole().name().isBlank()) {
            throw new RegistrationRequestBadRequest("All fields are mandatory");
        }

        var passwordCorrect = matches(PASSWORD_REGEX, requestDTO.getPassword());
        if(!passwordCorrect) {
            throw new RegistrationRequestBadRequest("Password is not in the right format");
        }

        var emailCorrect = matches(EMAIL_REGEX, requestDTO.getEmail());
        if(!emailCorrect) {
            throw new RegistrationRequestBadRequest("Email is not in the right format");
        }
        checkEmailExistence(requestDTO.getEmail());

    }

    private void checkEmailExistence(String email) {
        var emailInUseRR = registrationRequestRepository.findByEmail(email);
        var emailInUseUser = userRepository.findByEmail(email);
        if(emailInUseRR.isPresent() || emailInUseUser.isPresent()) {
            throw new EmailAlreadyInUse("Email " + email +
                    " is already in use.");
        }
    }
}
