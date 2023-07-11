package com.master.istanbul.service;

import com.master.istanbul.common.dto.AuthUserDTO;
import com.master.istanbul.common.dto.BasicUserInfoDTO;
import com.master.istanbul.common.dto.UserDTO;
import com.master.istanbul.common.dto.UserUpdateDTO;
import com.master.istanbul.common.event.UserStatusChanged;
import com.master.istanbul.common.model.User;
import com.master.istanbul.common.util.LoginPair;
import com.master.istanbul.common.util.PasswordChange;
import com.master.istanbul.common.util.PasswordEncoder;
import com.master.istanbul.common.util.UserRole;
import com.master.istanbul.common.util.UserStatus;
import com.master.istanbul.converter.UserConverter;
import com.master.istanbul.exception.custom.UnauthorizedException;
import com.master.istanbul.exception.user.UserBadRequest;
import com.master.istanbul.exception.user.UserNotFoundException;
import com.master.istanbul.repository.UserRepository;
import com.master.istanbul.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserValidator userValidator;
    private final RabbitMqService rabbitMqService;
    private final PasswordEncoder passwordEncoder;

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, UserConverter userConverter,
                       UserValidator userValidator, RabbitMqService rabbitMqService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.userValidator = userValidator;
        this.rabbitMqService = rabbitMqService;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDTO createUser(UserDTO userDTO) {
        userValidator.checkFields(userDTO);
        logger.info("Validation of request {} passed successfully.", userDTO);
        userDTO.setStatus(UserStatus.ACTIVE);
        var userRequest =  userRepository.save
                (userConverter.dtoToModel(userDTO));
        logger.info("User successfully saved into db.");
        return userConverter.modelToDto(userRequest);
    }

    public UserDTO getByPublicId(UUID publicId, boolean includeDeactivated, String sessionUserId, String role) {
        if(!"ADMIN".equalsIgnoreCase(role) && !sessionUserId.equalsIgnoreCase(publicId.toString())) {
            throw new UnauthorizedException("User cannot access information about other users.");
        }
        Optional<User> user;
        if(includeDeactivated) {
            logger.info("Finding also deactivated users with id: {}.", publicId);
            user = userRepository.findByPublicId(publicId);
        }
        else {
            logger.info("Finding only active users with id: {}.", publicId);
            user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.ACTIVE);
        }
        if(user.isEmpty()) {
            logger.warn("User with provided id: {} does not exist.", publicId);
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist.", publicId));
        }

        return userConverter.modelToDto(user.get());
    }

    public UserDTO updateUser(UUID publicId, UserUpdateDTO userDTO, String sessionUserId) {
        if(!sessionUserId.equalsIgnoreCase(publicId.toString())) {
            throw new UnauthorizedException("User cannot access information about other users.");
        }
        var user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.ACTIVE);
        if(user.isEmpty()) {
            logger.warn("User with provided id: {} does not exist.", publicId);
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist.", publicId));
        }

        var userModel = user.get();
        userConverter.updateSomeUserFields(userDTO, userModel);
        var savedModel = userRepository.save(userModel);
        logger.info("Successfully updated user with id: {}", publicId);
        return userConverter.modelToDto(savedModel);
    }

    public UserDTO activateUser(UUID publicId) {
        var user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.DEACTIVATED);
        if(user.isEmpty()) {
            logger.warn("User with provided id: {} does not exist or it is already active.", publicId);
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist or it is already active.", publicId));
        }

        var userModel = user.get();
        userModel.setStatus(UserStatus.ACTIVE);
        var savedModel = userRepository.save(userModel);
        var userStatusChangedEvent = UserStatusChanged.builder()
                        .firstname(savedModel.getFirstname())
                        .lastname(savedModel.getLastname())
                        .email(savedModel.getEmail())
                        .status(UserStatus.ACTIVE.name())
                        .build();
        rabbitMqService.userStatusChanged(userStatusChangedEvent);
        logger.info("Successfully activate user with id: {}", publicId);
        return userConverter.modelToDto(savedModel);
    }

    public String deactivateUser(UUID publicId) {
        var user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.ACTIVE);
        if(user.isEmpty()) {
            logger.warn("User with provided id: {} does not exist.", publicId);
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist.", publicId));
        }

        var userModel = user.get();
        userModel.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(userModel);
        var userStatusChangedEvent = UserStatusChanged.builder()
                .firstname(userModel.getFirstname())
                .lastname(userModel.getLastname())
                .email(userModel.getEmail())
                .status(UserStatus.DEACTIVATED.name())
                .build();
        rabbitMqService.userStatusChanged(userStatusChangedEvent);
        logger.info("Successfully deactivated user with id:{}", publicId);
        return "User successfully deactivated.";
    }

    public UserDTO changeUserRole(UUID publicId, String role) {
        if(!UserRole.check(role)) {
            logger.warn("Wrong user role provided: {}.", role);
            throw new UserBadRequest("Wrong role provided.");
        }
        var user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.ACTIVE);
        if(user.isEmpty()) {
            logger.warn("User with provided id: {} does not exist.", publicId);
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist.", publicId));
        }

        var userModel = user.get();
        userModel.setRole(UserRole.fromString(role));
        var savedModel = userRepository.save(userModel);
        logger.info("Successfully changed user role with user id: {}.", publicId);
        return userConverter.modelToDto(savedModel);
    }

    public UserDTO changeUserPassword(UUID publicId, PasswordChange password, String sessionUserId) {
        if(!sessionUserId.equalsIgnoreCase(publicId.toString())) {
            throw new UnauthorizedException("User cannot access information about other users.");
        }
        userValidator.checkPasswordFormat(password.getOldPassword());
        var user = userRepository.findByPublicIdAndStatusEquals(publicId, UserStatus.ACTIVE);
        if(user.isEmpty()) {
            throw new UserNotFoundException(String.format("User with provided id:%d does not exist.", publicId));
        }

        var userModel = user.get();
        var oldPasswordMatch = passwordEncoder.verifyPassword(password.getOldPassword(), userModel.getPassword());
        if(!oldPasswordMatch) {
            logger.warn("Old password for user: {} is incorrect", publicId);
            throw new UserBadRequest("Old password is incorrect.");
        }
        var encodedNewPassword = passwordEncoder.encodePassword(password.getNewPassword());
        userModel.setPassword(encodedNewPassword);
        var savedModel = userRepository.save(userModel);
        logger.info("Successfully saved new user password with user id: {}.", publicId);
        return userConverter.modelToDto(savedModel);
    }


    public List<UserDTO> getAllUsers(boolean includeDeactivated) {
        Iterable<User> allUsers;
        if(includeDeactivated) {
            logger.info("Searching for all users, include deactivated users.");
            allUsers = userRepository.findAll();
        }
        else {
            logger.info("Searching for only active users.");
            allUsers = userRepository.findAllByStatusEquals(UserStatus.ACTIVE);
        }
        var usersAsList = StreamSupport.stream(allUsers.spliterator(), false)
                .map(userConverter::modelToDto)
                .collect(Collectors.toList());
        return usersAsList;
    }

    public LoginPair authenticateUser(AuthUserDTO userDTO) {
        System.out.println("Istanbul generate token");
        var user = userRepository.findByEmail(userDTO.getEmail());
        if(user.isEmpty()) {
            return new LoginPair(false, "", "") ;
        }
        var returnedUser = user.get();
        if(UserStatus.DEACTIVATED.equals(returnedUser.getStatus())) {
            return new LoginPair(false, "", "");
        }
        var encodedPassword = returnedUser.getPassword();
        var passwordMatch = passwordEncoder.verifyPassword(userDTO.getPassword(), encodedPassword);

        return new LoginPair(passwordMatch, returnedUser.getRole().name(), returnedUser.getPublicId().toString());
    }

    public List<BasicUserInfoDTO> getAllUsersForPublicIds(List<UUID> ids) {

        var allUsers = userRepository.findAllByPublicId(ids);

        return allUsers
                .stream()
                .map(user -> new BasicUserInfoDTO(user.getPublicId().toString(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
