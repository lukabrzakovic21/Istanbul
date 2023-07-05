package com.master.istanbul.controller;

import com.master.istanbul.common.dto.AuthUserDTO;
import com.master.istanbul.common.dto.UserDTO;
import com.master.istanbul.common.dto.UserUpdateDTO;
import com.master.istanbul.common.util.LoginPair;
import com.master.istanbul.common.util.PasswordChange;
import com.master.istanbul.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        logger.info("Create user started with request: {}", userDTO);
        return ok(userService.createUser(userDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(
            @RequestParam(name = "include_deactivated", required = false, defaultValue = "false") boolean includeDeactivated
    ) {
        logger.info("Get all users started.");
        return ok(userService.getAllUsers(includeDeactivated));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<UserDTO> getByPublicId(@PathVariable String publicId,
                                                 @RequestParam(name = "include_deactivated", required = false, defaultValue = "false") boolean includeDeactivated) {
        logger.info("Get user with id: {}", publicId);
        return ok(userService.getByPublicId(UUID.fromString(publicId), includeDeactivated));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String publicId, @RequestBody UserUpdateDTO userDTO) {
        logger.info("Update user password with id: {}. Request body: {}.", publicId, userDTO);
        return ok(userService.updateUser(UUID.fromString(publicId), userDTO));
    }

    @PatchMapping("/activate/{publicId}")
    public ResponseEntity<UserDTO> activateUser(@PathVariable String publicId) {
        logger.info("Activating user with id: {}.", publicId);
        return ok(userService.activateUser(UUID.fromString(publicId)));
    }

    @DeleteMapping("/deactivate/{publicId}")
    public ResponseEntity<HttpStatusCode> deactivateUser(@PathVariable String publicId) {
        logger.info("Deactivating user with id: {}", publicId);
        userService.deactivateUser(UUID.fromString(publicId));
        return ok(HttpStatusCode.valueOf(204));
    }

    @PatchMapping("/role/{publicId}")
    public ResponseEntity<UserDTO> changeUserRole(@PathVariable String publicId, @RequestBody String role) {
        logger.info("Update user role with id: {}. New role: {}.", publicId, role);
        return ok(userService.changeUserRole(UUID.fromString(publicId), role));
    }

    @PatchMapping("/password/{publicId}")
    public ResponseEntity<UserDTO> changeUserPassword(@PathVariable String publicId, @RequestBody PasswordChange passwordBody) {
        logger.info("Update user password with id: {}", publicId);
        return ok(userService.changeUserPassword(UUID.fromString(publicId), passwordBody));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginPair> authenticateUser(@RequestBody AuthUserDTO userDTO) {
        logger.info("Login flow started with request: {}", userDTO);
        return ok(userService.authenticateUser(userDTO));
    }

}
