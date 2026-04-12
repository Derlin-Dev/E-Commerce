package com.E_Commerce.User_services.controller;

import com.E_Commerce.User_services.model.dto.*;
import com.E_Commerce.User_services.model.entity.TypeToken;
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
    public ResponseEntity<?> registerNewUser(@RequestBody UserRequest request){
        userServices.createNewUser(request);
        return ResponseEntity.status(HttpStatus.OK).body("Usuario registrado, verifica un correo.");
    }

    @GetMapping("/verified-user")
    public ResponseEntity<?> verifiedValidationEmailToken(
            @RequestParam("token") String token,
            @RequestParam("email") String email){
        try{
            userServices.isVerifiedToken(email, token, TypeToken.VERIFY_EMAIL);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario verificado correctamente...");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al validar el usuario");
        }
    }

    @GetMapping("/request-change")
    public ResponseEntity<?> requestChangePassword(@RequestBody EmailChangeRequest emailChangeRequest){
        userServices.requestResetPassword(emailChangeRequest.getEmail());
        return ResponseEntity.ok().body("Correo enviado");
    }

    @GetMapping("/verified-resettoken")
    public ResponseEntity<?> verifiedResetPasswordToken(
            @RequestParam("token") String toke,
            @RequestParam("email") String email){

        userServices.isVerifiedToken(email, toke, TypeToken.RESET_PASSWORD);
        return ResponseEntity.ok().body("Token verificado");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changeNewPassword(@RequestBody ResetNewPasswordRequest newPasswordRequest){
        userServices.resetPassword(newPasswordRequest.getEmail(), newPasswordRequest.getToken(), newPasswordRequest.getNewPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("Contrasena cambiada correctamente");
    }

}