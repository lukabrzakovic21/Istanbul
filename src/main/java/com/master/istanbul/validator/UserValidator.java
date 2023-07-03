package com.master.istanbul.validator;

import com.master.istanbul.common.dto.UserDTO;
import com.master.istanbul.exception.custom.EmailAlreadyInUse;
import com.master.istanbul.exception.user.UserBadRequest;
import com.master.istanbul.repository.RegistrationRequestRepository;
import com.master.istanbul.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.regex.Pattern.matches;

@Component
public class UserValidator {

    private final UserRepository userRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    public UserValidator(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository) {
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
    }


    public void checkFields(UserDTO requestDTO) {

        if(Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getCity()) || Objects.isNull(requestDTO.getCountry())
                || Objects.isNull(requestDTO.getEmail()) || Objects.isNull(requestDTO.getFirstname()) || Objects.isNull(requestDTO.getLastname())
                || Objects.isNull(requestDTO.getPassword()) || Objects.isNull(requestDTO.getPhone())) {
            throw new UserBadRequest("All fields are mandatory");
        }
        if(requestDTO.getEmail().isBlank() || requestDTO.getCountry().isBlank() || requestDTO.getFirstname().isBlank() || requestDTO.getLastname().toString().isBlank()
                || requestDTO.getCity().isBlank() || requestDTO.getPhone().isBlank() || requestDTO.getPassword().isBlank()) {
            throw new UserBadRequest("All fields are mandatory");
        }
        var emailCorrect = matches(EMAIL_REGEX, requestDTO.getEmail());
        if(!emailCorrect) {
            throw new UserBadRequest("Email is not in the right format");
        }
        checkEmailExistence(requestDTO.getEmail());

    }

    private void checkEmailExistence(String email) {
        var emailInUseUser = userRepository.findByEmail(email);
        if(emailInUseUser.isPresent()) {
            throw new EmailAlreadyInUse("Email " + email +
                    "is already in use.");
        }
    }

    public void checkPasswordFormat(String password) {
        var passwordCorrect = matches(PASSWORD_REGEX, password);
        if(!passwordCorrect) {
            throw new UserBadRequest("Password is not in the right format");
        }
    }
}
