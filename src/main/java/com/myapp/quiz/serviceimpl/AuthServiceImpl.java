package com.myapp.quiz.serviceimpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myapp.quiz.constants.Constants;
import com.myapp.quiz.dto.AuthRequest;
import com.myapp.quiz.dto.UserRequest;
import com.myapp.quiz.entity.Diem;
import com.myapp.quiz.entity.Role;
import com.myapp.quiz.entity.User;
import com.myapp.quiz.entity.VerificationToken;
import com.myapp.quiz.exceptionhandler.CustomException;
import com.myapp.quiz.repository.RoleRepository;
import com.myapp.quiz.repository.UserRepository;
import com.myapp.quiz.repository.VerificationTokenRepository;
import com.myapp.quiz.security.jwt.JwtTokenProvider;
import com.myapp.quiz.service.AuthService;
import com.myapp.quiz.service.VerificationTokenService;
import com.myapp.quiz.utils.SendMail;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "AuthService")
@Transactional(rollbackOn = Exception.class)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository tokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final SendMail sendMail;

    @Override
    public String auth(AuthRequest login, String requestType) {
        try {
            log.info("Start Authen: {}", login);

            // Get username or email
            String usernameRequest = login.getUsernameOrEmail();

            // Get fullname
            String fullName = usernameRequest.contains("@") ?
                    userRepository.getFullnameByEmail(usernameRequest) :
                    userRepository.getFullnameByUsername(usernameRequest);

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(usernameRequest, login.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate Access Token
            return requestType.equals(Constants.REQUEST_ACCESS_TOKEN) ?
                    jwtTokenProvider.generateAccessToken(authentication, fullName) :
                    jwtTokenProvider.generateRefreshToken(authentication, fullName);
        } catch (BadCredentialsException ex) {
            log.warn("Invalid login attempt for: {}", login.getUsernameOrEmail());
            throw new CustomException("Username or password is incorrect", HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException ex) {
            log.error("Authentication failed", ex);
            throw new CustomException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void register(UserRequest userRequest) {
        try {
            log.info("START REGISTER USER");
            if (userRepository.existsByUsername(userRequest.getUsername())
                    || userRepository.existsByEmail(userRequest.getEmail())) {
                throw new IllegalArgumentException("Username hoặc Email đã tồn tại");
            }

            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setFullName(userRequest.getFirstName() + Constants.SPACE_HALF_SIZE + userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            // Gán role mặc định: ROLE_USER
            Role roleUser = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRoles(List.of(roleUser));
            user.setDiems(List.of(new Diem()));
            user.setEnabled(false);
            userRepository.save(user);

            // tạo token verify
            String token = UUID.randomUUID().toString();
            verificationTokenService.saveTokenRegister(user, token);

            // gửi email
            sendMail.sendVerificationEmail(user, token);
        } catch (Exception e) {
            log.error("Has error when register user: {} ", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean checkValidToken(String token) {
        try {
            log.info("Start check valid token:");
            VerificationToken verificationToken = tokenRepository.findByToken(token);

            if (verificationToken == null) {
                log.warn("verificationToken is null!");
                return false;
            }

            if (!isWithin30Minutes(verificationToken)) {
                log.warn("verificationToken is expiry!");
                return false;
            }

            User user = verificationToken.getUser();
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            log.error("Error check valid token: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 
     * @param verificationToken
     * @return
     */
    private boolean isWithin30Minutes(VerificationToken verificationToken) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(verificationToken.getCreatedAt(), verificationToken.getExpiryDate());

        if (duration.toMinutes() <= 30) {
            // Kiểm tra xem createdAt có không phải là sau thời điểm hiện tại
            if (!verificationToken.getCreatedAt().isAfter(now)) {
                // Kiểm tra xem expiryDate có không phải là trước thời điểm hiện tại
                if (!verificationToken.getExpiryDate().isBefore(now)) {
                    return true;
                }
            }
        }
        return false;
    }

}
