package com.E_Commerce.Order_services.util;

import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class OrderUtil {

    private static final String CHARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom ramdon = new SecureRandom();

    public String generateOrderCode(String n){
        StringBuilder code = new StringBuilder(n);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = ramdon.nextInt(CHARACTERES.length());
            code.append(CHARACTERES.charAt(index));
        }

        return code.toString();
    }

}
