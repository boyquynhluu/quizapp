package com.myapp.quiz.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myapp.quiz.constants.Constants;
import com.myapp.quiz.entity.Diem;
import com.myapp.quiz.entity.RefreshToken;
import com.myapp.quiz.entity.User;
import com.myapp.quiz.repository.RefreshTokenRepository;
import com.myapp.quiz.repository.UserRepository;
import com.myapp.quiz.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "USER SERVICE")
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Value(Constants.API_JWT_REFRESH_EXPIRATION_MILLISECONDS)
    private long jwtRefreshExpirationDate;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registerUser(Double diemThi) {
        log.info("START UPDATE ĐIỂM");

        try {
            // ✅ Lấy từ SecurityContext
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            User user;
            if (username.contains("@")) {
                user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
            } else {
                user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not exist by Username or Email"));
            }

            Diem diem = new Diem();
            diem.setLanThi(Optional.ofNullable(user.getDiems()).map(List::size).orElse(0) + 1);
            diem.setDiemThi(diemThi);
            diem.setUser(user);

            if (user.getDiems() == null) {
                user.setDiems(new ArrayList<>());
            }
            user.getDiems().add(diem);

            userRepository.save(user);
        } catch (Exception e) {
            log.error("Has error when update diem: {} ", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void registerRefreshToken(String usernameOrPassword, String token) {
        try {
            User user;
            if (usernameOrPassword.contains("@")) {
                user = userRepository.findByEmail(usernameOrPassword).orElseThrow(
                        () -> new UsernameNotFoundException("User not found with email: " + usernameOrPassword));
            } else {
                user = userRepository.findByUsername(usernameOrPassword)
                        .orElseThrow(() -> new UsernameNotFoundException("User not exist by Username or Email"));
            }

            if (user.getRefreshToken() != null) {
                user.setRefreshToken(null);
                userRepository.save(user);
                entityManager.flush(); // ⬅️ ép Hibernate flush cập nhật gỡ liên kết
            }
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(token);
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshToken.setExpirationAt(formatLocalDateTime());
            refreshToken.setUser(user);

            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Has error when register refresh token: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteByRefreshToken(String username) {
        User user;
        if (username.contains("@")) {
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        } else {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not exist by Username or Email"));
        }

        RefreshToken refreshToken = user.getRefreshToken();
        if (!Objects.isNull(refreshToken)) {
            user.setRefreshToken(null);
            refreshTokenRepository.delete(refreshToken);
            userRepository.save(user);
        }
    }

    /**
     * Convert LocalDateTime
     * 
     * @return yyyy-MM-dd HH:mm:ss formatted LocalDateTime
     */
    private LocalDateTime formatLocalDateTime() {
        try {
            Date currentDate = new Date();
            Date expireDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);
            return expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            log.error("Parse LocalDateTime has error: {}", e.getMessage(), e);
            throw e;
        }
    }
}