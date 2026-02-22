package com.E_Commerce.User_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetNewPasswordRequest {
    private String email;
    private String token;
    private String newPassword;
}
