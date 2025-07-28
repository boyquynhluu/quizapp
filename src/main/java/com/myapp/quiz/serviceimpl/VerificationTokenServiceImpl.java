package com.myapp.quiz.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.myapp.quiz.entity.User;
import com.myapp.quiz.entity.VerificationToken;
import com.myapp.quiz.repository.VerificationTokenRepository;
import com.myapp.quiz.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "VerificationTokenServiceImpl")
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepo;

    @Override
    public void saveTokenRegister(User user, String token) {
        try {
            log.info("Start Register Token");
            VerificationToken vt = new VerificationToken();
            vt.setToken(token);
            vt.setUser(user);
            vt.setCreatedAt(LocalDateTime.now());
            vt.setExpiryDate(vt.getCreatedAt().plusMinutes(30));
            tokenRepo.save(vt);
        } catch (Exception e) {
            log.error("Register Token Has Error: {}", e.getMessage(), e);
            throw e;
        }
    }

}
