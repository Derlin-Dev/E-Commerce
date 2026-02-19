package com.E_Commerce.Cart_services.util;

import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class ShoppingCartUtil {

    private static final String CHARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom ramdon = new SecureRandom();

    public String generateShoppingCartCode(String n){
        StringBuilder code = new StringBuilder(n);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = ramdon.nextInt(CHARACTERES.length());
            code.append(CHARACTERES.charAt(index));
        }

        return code.toString();
    }
}
