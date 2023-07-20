package com.master.istanbul.service;

import com.master.istanbul.common.event.RegistrationRequestStatusChanged;
import com.master.istanbul.common.util.PasswordEncoder;
import com.master.istanbul.common.util.RegistrationRequestStatus;
import com.master.istanbul.converter.RegistrationRequestConverter;
import com.master.istanbul.converter.UserConverter;
import com.master.istanbul.exception.registration_request.RegistrationRequestBadRequest;
import com.master.istanbul.exception.registration_request.RegistrationRequestNotFoundException;
import com.master.istanbul.repository.RegistrationRequestRepository;
import com.master.istanbul.common.dto.RegistrationRequestDTO;
import com.master.istanbul.validator.RegistrationRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistrationService {

    private final RegistrationRequestRepository registrationRequestRepository;
    private final RegistrationRequestConverter registrationRequestConverter;
    private final RegistrationRequestValidator registrationRequestValidator;
    private final RabbitMqService rabbitMqService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserConverter userConverter;
    private final static Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    public RegistrationService(RegistrationRequestRepository registrationRequestRepository,
                               RegistrationRequestConverter registrationRequestConverter,
                               RegistrationRequestValidator registrationRequestValidator,
                               RabbitMqService rabbitMqService, PasswordEncoder passwordEncoder, UserService userService, UserConverter userConverter) {
        this.registrationRequestRepository = registrationRequestRepository;
        this.registrationRequestConverter = registrationRequestConverter;
        this.registrationRequestValidator = registrationRequestValidator;
        this.rabbitMqService = rabbitMqService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userConverter = userConverter;
    }


    public RegistrationRequestDTO createRegistrationRequest(RegistrationRequestDTO registrationRequestDTO) {
        registrationRequestValidator.checkFields(registrationRequestDTO);
        logger.info("Validation of request {} passed successfully.", registrationRequestDTO);
        registrationRequestDTO.setStatus(RegistrationRequestStatus.CREATED);
        registrationRequestDTO.setPassword(passwordEncoder.encodePassword(registrationRequestDTO.getPassword()));

        var registrationRequest =  registrationRequestRepository.save
                (registrationRequestConverter.dtoToModel(registrationRequestDTO));
        logger.info("Successfully saved registration requests to the database.");
        return registrationRequestConverter.modelToDto(registrationRequest);
    }

    public RegistrationRequestDTO getByPublicId(UUID publicId) {
        var registrationRequest = registrationRequestRepository.findByPublicId(publicId);
        if(registrationRequest.isEmpty()) {
            logger.warn("Registration request with provided id: {} does not exist.", publicId);
            throw new RegistrationRequestNotFoundException(String.format("Registration request with provided id:%s does not exist.", publicId));
        }
        return registrationRequestConverter.modelToDto(registrationRequest.get());
    }

    public RegistrationRequestDTO updateByPublicId(UUID publicId, String status) {
        if(!RegistrationRequestStatus.check(status)) {
            logger.warn("Wrong status code provided:{}.", status);
            throw new RegistrationRequestBadRequest("Wrong status code provided.");
        }
        var registrationRequest = registrationRequestRepository.findByPublicId(publicId);
        if(registrationRequest.isEmpty()) {
            logger.warn("Registration request with provided id: {} does not exist.", publicId);
            throw new RegistrationRequestNotFoundException(String.format("Registration request with provided id:%d does not exist.", publicId));
        }
        var requestModel = registrationRequest.get();
        if(RegistrationRequestStatus.CREATED.name().equalsIgnoreCase(status)
                || !RegistrationRequestStatus.CREATED.name().equalsIgnoreCase(requestModel.getStatus().name())) {
            logger.warn("Wrong value of status code.");
            throw new RegistrationRequestBadRequest("Status code change doesn't have any sense.");
        }

        requestModel.setStatus(RegistrationRequestStatus.fromString(status));
        var savedModel = registrationRequestRepository.save(requestModel);
        logger.info("Successfully updated registration request with id{}.", publicId);
        var eventBody = RegistrationRequestStatusChanged.builder()
                .firstname(savedModel.getFirstname())
                .lastname(savedModel.getLastname())
                .email(savedModel.getEmail())
                .status(status)
                .build();
        var userFromRegRequest = userConverter.registrationRequestToUser(savedModel);
        if(RegistrationRequestStatus.ACCEPTED.name().equalsIgnoreCase(status)) {
            userService.createUser(userFromRegRequest);
            logger.info("User with email: {} successfully created", userFromRegRequest.getEmail());
        }
        rabbitMqService.registrationRequestStatusChanged(eventBody);
        return registrationRequestConverter.modelToDto(savedModel);
    }

    public List<RegistrationRequestDTO> getAllRegistrationRequests() {

        var allRegistrationRequests = registrationRequestRepository.findAll();
        var gamesAsList = StreamSupport.stream(allRegistrationRequests.spliterator(), false)
                .map(registrationRequestConverter::modelToDto)
                .collect(Collectors.toList());
        return gamesAsList;
        }

    }

