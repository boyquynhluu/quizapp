package com.myapp.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.myapp.quiz.entity.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {

    @Query(value = "SELECT * FROM tbl_verification_token WHERE token = ?1", nativeQuery = true)
    VerificationToken findByToken(String token);
}
