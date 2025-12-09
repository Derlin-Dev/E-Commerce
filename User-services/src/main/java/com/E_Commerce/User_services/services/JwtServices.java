package com.E_Commerce.User_services.services;

import com.E_Commerce.User_services.config.JwtUtil;
import com.E_Commerce.User_services.model.dto.AuthenticationRequest;
import com.E_Commerce.User_services.model.dto.AuthenticationResponses;
import com.E_Commerce.User_services.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JwtServices {

    @Autowired
    private AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    private final AuthenticationServices authenticationServices;
    private final UserServices services;

    public JwtServices(JwtUtil jwtUtil, AuthenticationServices authenticationServices, UserServices services) {
        this.jwtUtil = jwtUtil;
        this.authenticationServices = authenticationServices;
        this.services = services;
    }

    public AuthenticationResponses JwtCreateToken(AuthenticationRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getCorreo(), request.getPassword()
        ));

        final UserDetails userDetails = authenticationServices.loadUserByUsername(request.getCorreo());
        User user = services.getUser(request.getCorreo());
        String token = jwtUtil.generateToken(userDetails, user.getUserCode());
        return new AuthenticationResponses(token, user.getUserCode(), user.getCorreo());
    }

//    public String JwtCreateToken(AuthenticationRequest request){
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                request.getCorreo(), request.getPassword()
//        ));
//
//        final UserDetails userDetails = authenticationServices.loadUserByUsername(request.getCorreo());
//        User user = services.getUser(request.getCorreo());
//        return jwtUtil.generateToken(userDetails, user.getUserCode());
//    }
}
