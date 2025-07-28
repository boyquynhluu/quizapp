package com.myapp.quiz.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "tbl_verification_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "token", unique = true)
    String token;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(
        name = "user_id", 
        referencedColumnName = "id", 
        unique = true,
        foreignKey = @ForeignKey(name = "fk_verification_token_user")
    )
    User user;
}
