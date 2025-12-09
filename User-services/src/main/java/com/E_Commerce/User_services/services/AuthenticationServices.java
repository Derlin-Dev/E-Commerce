package com.E_Commerce.User_services.services;

import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationServices implements UserDetailsService {

    private final UserRepository repository;

    public AuthenticationServices(UserRepository repository) {
        this.repository = repository;
    }

    //Cargar los datos del usuario al inicial seccion
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        User user = repository.findByCorreo(correo);

        if (user == null){
            throw new UsernameNotFoundException("Usuario no encontrado con nombre de usuario");
        }

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getCorreo(),
                user.getPassword(),
                authorities
        );
    }
}
