package com.E_Commerce.User_services.ulti;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class UserUtil {

    private static final String CHARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom ramdon = new SecureRandom();

    public String generateCodeUser(String n){

        StringBuilder code = new StringBuilder(n);
        for (int i = 0; i < CODE_LENGTH; i++){
            int index = ramdon.nextInt(CHARACTERES.length());
            code.append(CHARACTERES.charAt(index));
        }

        return code.toString();
    }

    //Generar token de verificacion y reseteo de contrasena
    public String generateVerifiedCode(){

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        return token;
    }

}
