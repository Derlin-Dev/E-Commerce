package com.E_Commerce.User_services.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Usuaios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_code", unique = true, nullable = false)
    private String userCode;
    private String name;
    private String correo;
    private String password;
    private boolean isVerified;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;


    public User(String userCode, String name, String correo, String password, boolean isVerified, List<String> roles) {
        this.userCode = userCode;
        this.name = name;
        this.correo = correo;
        this.password = password;
        this.isVerified = isVerified;
        this.roles = roles;
    }
}
