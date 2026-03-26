package com.E_Commerce.User_services.services;

import com.E_Commerce.User_services.config.JwtUtil;
import com.E_Commerce.User_services.model.dto.UserRequest;
import com.E_Commerce.User_services.model.entity.Roles;
import com.E_Commerce.User_services.model.entity.TokenTemp;
import com.E_Commerce.User_services.model.entity.TypeToken;
import com.E_Commerce.User_services.model.entity.User;
import com.E_Commerce.User_services.repository.TokenTempRepository;
import com.E_Commerce.User_services.repository.UserRepository;
import com.E_Commerce.User_services.ulti.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userrepository;
    private final SendEmailServices sendEmailServices;
    private final TokenTempRepository tokenTempRepository;
    private UserUtil userUtil;
    private final JwtUtil jwtUtil;

    List<String> roles;

    public UserServices(PasswordEncoder passwordEncoder, UserRepository repository, SendEmailServices sendEmailServices, TokenTempRepository tokenTempRepository, UserUtil userUtil, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userrepository = repository;
        this.sendEmailServices = sendEmailServices;
        this.tokenTempRepository = tokenTempRepository;
        this.userUtil = userUtil;
        this.jwtUtil = jwtUtil;
    }

    //Crear nuevo usuario
    public void createNewUser(UserRequest request) throws UsernameNotFoundException {

        roles = new ArrayList<>();
        User userVerified = userrepository.findByCorreo(request.getCorreo());
        TokenTemp tokenTempVerified = new TokenTemp();

        if (userVerified != null){
            if (userVerified.isVerified()) {
                throw new UsernameNotFoundException("Correo ya registrado y verificado");
            }
            else {
                tokenTempRepository.invalidateActiveToken(userVerified, TypeToken.VERIFY_EMAIL);

                String verifiedToken = userUtil.generateVerifiedCode();
                String tokenHash = passwordEncoder.encode(verifiedToken);

                tokenTempVerified.setUser(userVerified);
                tokenTempVerified.setTokenHash(tokenHash);
                tokenTempVerified.setTypeToken(TypeToken.VERIFY_EMAIL);

                tokenTempRepository.save(tokenTempVerified);
                sendEmailServices.sendVerifiedEmail(request.getCorreo(), verifiedToken);
            }
        }

        String role = "";
        if (request.getRol().equals("ADMIN")) role = String.valueOf(Roles.ADMIN);
        if (request.getRol().equals("USER")) role = String.valueOf(Roles.USER);

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        String code = userUtil.generateCodeUser("USER-");
        String verifiedToken = userUtil.generateVerifiedCode();
        String tokenHash = passwordEncoder.encode(verifiedToken);

        User user = new User(
                code,
                request.getName(),
                request.getCorreo(),
                request.getPassword(),
                false,
                role
        );

        TokenTemp tokenTemp = new TokenTemp(
          tokenHash,
          TypeToken.VERIFY_EMAIL,
          user
        );

        userrepository.save(user);
        tokenTempRepository.save(tokenTemp);

        sendEmailServices.sendVerifiedEmail(request.getCorreo(), verifiedToken);
    }

    //Verificar que el token esa correcto
    public boolean isVerifiedToken(String email, String token, TypeToken typeToken ){
        try {
            User user = userrepository.findByCorreo(email);

            Optional<TokenTemp> storeToken = tokenTempRepository.findByUserAndTypeTokenAndIsUsedFalse(user, typeToken);

            if (user == null || storeToken.isEmpty()) {
                throw new RuntimeException("Usuario o Token no encontrado");
            }

            if (!passwordEncoder.matches(token, storeToken.get().getTokenHash())) {
                throw new RuntimeException("Token no coincide");
            }

            if (storeToken.get().isUsed()){
                throw new RuntimeException("Token ya fue utilizado");
            }

            if (!storeToken.get().getTypeToken().equals(typeToken)) {
                throw new RuntimeException("Tipo de token no valido");
            }

            if (storeToken.get().getTypeToken() == TypeToken.VERIFY_EMAIL){
                isVerifiedUser(user, storeToken.orElse(null));
            }

            return true;

        }catch (RuntimeException e){
            throw new RuntimeException("Error al verificar usuario");
        }

    }

    //Verificamos el usuario
    public void isVerifiedUser(User user, TokenTemp storeToken){
        user.setVerified(true);
        storeToken.setUsed(true);
        userrepository.save(user);
        tokenTempRepository.save(storeToken);
    }

    //Solisitamos reseteo de contrasena
    @Transactional
    public void requestResetPassword(String email){

        User user = userrepository.findByCorreo(email);

        if (user != null){
            tokenTempRepository.invalidateActiveToken(user, TypeToken.RESET_PASSWORD);

            String token = userUtil.generateVerifiedCode();
            String tokenHash = passwordEncoder.encode(token);

            TokenTemp tokenTemp = new TokenTemp();

            tokenTemp.setUser(user);
            tokenTemp.setTypeToken(TypeToken.RESET_PASSWORD);
            tokenTemp.setTokenHash(tokenHash);

            tokenTempRepository.save(tokenTemp);

            sendEmailServices.sendResetEmail(email, token);
        }else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }

    //Reseteamos la contrasena
    public void resetPassword(String email, String token, String newPassword){

        if (isVerifiedToken(email, token, TypeToken.RESET_PASSWORD)){

            String newPasswordHash = passwordEncoder.encode(newPassword);

            User user = userrepository.findByCorreo(email);
            user.setPassword(newPasswordHash);

            Optional<TokenTemp> storeToken = tokenTempRepository.findByUserAndTypeTokenAndIsUsedFalse(
                    user, TypeToken.RESET_PASSWORD);

            TokenTemp tokenTemp = storeToken.get();
            tokenTemp.setUsed(true);

            tokenTempRepository.save(tokenTemp);
            userrepository.save(user);

        }else {
            throw new RuntimeException("Error al validar token");
        }

    }
}
