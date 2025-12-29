package com.E_Commerce.User_services.controller;

import com.E_Commerce.User_services.model.dto.UserRequest;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/e-commerce/api/v1/user")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("X-User-Code") String userId) {
        return ResponseEntity.ok("Bienvenido: " + userId);
    }
}
