package com.E_Commerce.User_services.services;

import com.E_Commerce.User_services.config.JwtUtil;
import com.E_Commerce.User_services.model.dto.AuthenticationRequest;
import com.E_Commerce.User_services.model.dto.AuthenticationResponses;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServices(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponses login (AuthenticationRequest request) {

        User user = userRepository.findByCorreo(request.getCorreo());

        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Credenciales invalidas");
        }

        String token = jwtUtil.createToken(user.getUserCode(), user.getRoles());

        return new AuthenticationResponses(
                token,
                user.getUserCode(),
                user.getCorreo()
        );
    }
}
