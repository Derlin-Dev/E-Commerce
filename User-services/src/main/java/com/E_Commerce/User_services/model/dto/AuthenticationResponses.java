package com.E_Commerce.User_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponses {

  private String token;
  private String code;
  private String correo;

}
