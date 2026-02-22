package com.E_Commerce.User_services.repository;

import com.E_Commerce.User_services.model.entity.TokenTemp;
import com.E_Commerce.User_services.model.entity.TypeToken;
import com.E_Commerce.User_services.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenTempRepository extends JpaRepository<TokenTemp, Long> {

    //TokenTemp findByToken(String token);

    Optional<TokenTemp> findByUserAndTypeTokenAndIsUsedFalse(User user, TypeToken typeToken);

    @Modifying
    @Query("""
            UPDATE token_temp t
            SET t.isUsed = true
            WHERE t.user = :user
            AND t.typeToken = :typeToken
            AND t.isUsed = false
            """)
    void invalidateActiveToken(User user, TypeToken typeToken);

}
