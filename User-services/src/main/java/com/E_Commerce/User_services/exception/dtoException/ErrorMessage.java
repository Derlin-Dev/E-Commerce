package com.E_Commerce.User_services.exception.dtoException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private HttpStatus httpStatus;
    private String message;

}
