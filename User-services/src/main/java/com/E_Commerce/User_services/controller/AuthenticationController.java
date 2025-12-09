package com.E_Commerce.User_services.controller;

import com.E_Commerce.User_services.model.dto.AuthenticationRequest;
import com.E_Commerce.User_services.model.dto.AuthenticationResponses;
import com.E_Commerce.User_services.model.dto.UserRequest;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.services.JwtServices;
import com.E_Commerce.User_services.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/e-commerce/api/v1/auth")
public class AuthenticationController {

    private final UserServices userServices;
    private final JwtServices jwtServices;

    public AuthenticationController(UserServices userServices, JwtServices jwtServices) {
        this.userServices = userServices;
        this.jwtServices = jwtServices;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request){
        final AuthenticationResponses responses = jwtServices.JwtCreateToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registreNewUser(@RequestBody UserRequest request){
        User user = userServices.createNewUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
