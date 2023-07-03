package com.master.istanbul.service;

import com.master.istanbul.common.event.RegistrationRequestStatusChanged;
import com.master.istanbul.common.event.UserStatusChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.master.istanbul.configuration.RabbitMqConfiguration.REGISTRATION_REQUEST_STATUS_CHANGED;
import static com.master.istanbul.configuration.RabbitMqConfiguration.USER_STATUS_CHANGED;


@Service
public class RabbitMqService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqService.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void registrationRequestStatusChanged(RegistrationRequestStatusChanged registrationRequest) {
        logger.info("Sending event to queue: {}  with body {}.", REGISTRATION_REQUEST_STATUS_CHANGED, registrationRequest);
//        rabbitTemplate.convertAndSend(REGISTRATION_REQUEST_STATUS_CHANGED, registrationRequest);
        logger.info("Send event to queue: {}  with body {}.", REGISTRATION_REQUEST_STATUS_CHANGED, registrationRequest);
    }

    public void userStatusChanged(UserStatusChanged userStatus) {
        logger.info("Sending event to queue: {}  with body {}.", USER_STATUS_CHANGED, userStatus);
        rabbitTemplate.convertAndSend(USER_STATUS_CHANGED, userStatus);
        logger.info("Send event to queue: {}  with body {}.", USER_STATUS_CHANGED, userStatus);
    }
}
