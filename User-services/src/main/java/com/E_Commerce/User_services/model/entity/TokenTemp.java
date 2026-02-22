package com.E_Commerce.User_services.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "token_temp")
public class TokenTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tokenHash;
    @Enumerated(EnumType.STRING)
    private TypeToken typeToken;
    private boolean isUsed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TokenTemp(String tokenHash, TypeToken typeToken, User user) {
        this.tokenHash = tokenHash;
        this.typeToken = typeToken;
        this.user = user;
    }
}
