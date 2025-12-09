package com.E_Commerce.User_services.repository;

import com.E_Commerce.User_services.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByCorreo(String correo);
    //User findByUserName(String userName);
}
