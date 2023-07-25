package com.master.istanbul.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    public static final String REGISTRATION_REQUEST_STATUS_CHANGED = "registration.request.status.changed";
    public static final String USER_STATUS_CHANGED = "user.status.changed";


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue registrationRequestStatusChanged() {
        return new Queue(REGISTRATION_REQUEST_STATUS_CHANGED, false);
    }

    @Bean
    public Queue userStatusChanged() {
        return new Queue(USER_STATUS_CHANGED, false);
    }

}
