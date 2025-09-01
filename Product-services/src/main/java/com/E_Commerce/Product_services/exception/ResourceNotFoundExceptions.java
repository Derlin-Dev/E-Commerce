package com.E_Commerce.Product_services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundExceptions extends Exception{

    public ResourceNotFoundExceptions(String message){
        super();
    }

}
