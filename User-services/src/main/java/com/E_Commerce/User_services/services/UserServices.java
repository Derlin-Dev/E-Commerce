package com.E_Commerce.User_services.services;

import com.E_Commerce.User_services.model.dto.UserRequest;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.repository.UserRepository;
import com.E_Commerce.User_services.ulti.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository repository;
    private UserUtil userUtil;

    List<String> roles;

    public UserServices(UserRepository repository, UserUtil userUtil) {
        this.repository = repository;
        this.userUtil = userUtil;
    }

    //Obtener un usuario por su correo
    public User getUser(String correo){
        return repository.findByCorreo(correo);
    }

    //Crear nuevo usuario
    public User createNewUser(UserRequest request) throws UsernameNotFoundException {

        roles = new ArrayList<>();

        if (repository.findByCorreo(request.getCorreo()) != null){
            throw new UsernameNotFoundException("Correo ya registrado");
        }

        roles.add(request.getRol());
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        String code = userUtil.generateCodeUser("USER-");

        User user = new User(
                code,
                request.getName(),
                request.getCorreo(),
                request.getPassword(),
                roles
        );

        return repository.save(user);
    }
}
