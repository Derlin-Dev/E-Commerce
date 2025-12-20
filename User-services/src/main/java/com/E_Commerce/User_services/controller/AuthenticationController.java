package com.E_Commerce.User_services.controller;

import com.E_Commerce.User_services.model.dto.AuthenticationRequest;
import com.E_Commerce.User_services.model.dto.AuthenticationResponses;
import com.E_Commerce.User_services.model.dto.UserRequest;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.services.AuthServices;
import com.E_Commerce.User_services.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/e-commerce/api/v1/auth")
public class AuthenticationController {

    private final UserServices userServices;
    private final AuthServices authServices;

    public AuthenticationController(UserServices userServices, AuthServices authServices) {
        this.userServices = userServices;
        this.authServices = authServices;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request){
        final AuthenticationResponses responses = authServices.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registreNewUser(@RequestBody UserRequest request){
        User user = userServices.createNewUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
