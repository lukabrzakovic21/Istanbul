package com.master.istanbul.controller;

import com.master.istanbul.common.dto.RegistrationRequestDTO;
import com.master.istanbul.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = "/register")
public class RegistrationController {

    private final RegistrationService registrationService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

   @PostMapping("/create")
    public ResponseEntity<RegistrationRequestDTO> createRegistrationRequest(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        logger.info("Create registration request started with request: {}", registrationRequestDTO);
       return ok(registrationService.createRegistrationRequest(registrationRequestDTO));
   }

   @GetMapping
    public ResponseEntity<List<RegistrationRequestDTO>> getAll() {
       logger.info("Get all registration requests started.");
       return ok(registrationService.getAllRegistrationRequests());
   }

   @GetMapping("/{publicId}")
    public ResponseEntity<RegistrationRequestDTO> getByPublicId(@PathVariable String publicId) {
       logger.info("Get registration request with id: {}", publicId);
        return ok(registrationService.getByPublicId(UUID.fromString(publicId)));
   }

    @PatchMapping("/{publicId}")
    public ResponseEntity<RegistrationRequestDTO> updateRequestByPublicId(@PathVariable String publicId, @RequestBody String status) {
        logger.info("Update registration request with id: {}. New status: {}.", publicId, status);
        return ok(registrationService.updateByPublicId(UUID.fromString(publicId), status));
    }
 }
