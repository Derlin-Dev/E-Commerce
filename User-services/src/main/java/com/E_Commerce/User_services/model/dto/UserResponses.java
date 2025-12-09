package com.E_Commerce.User_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponses {
    private String userCode;
    private String name;
    private String correo;
    private String rol;
   // private String password;
}
