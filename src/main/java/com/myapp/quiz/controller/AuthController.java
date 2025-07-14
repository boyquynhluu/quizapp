package com.myapp.quiz.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.quiz.constants.Constants;
import com.myapp.quiz.dto.AuthRequest;
import com.myapp.quiz.dto.AuthResponse;
import com.myapp.quiz.dto.RefreshTokenRequest;
import com.myapp.quiz.dto.UserRequest;
import com.myapp.quiz.security.jwt.JwtTokenProvider;
import com.myapp.quiz.service.AuthService;
import com.myapp.quiz.service.UserService;
import com.myapp.quiz.serviceimpl.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j(topic = "AUTH CONTROLLER")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("START LOGIN CONTROLLER");
        String accessToken = authService.auth(authRequest, Constants.REQUEST_ACCESS_TOKEN);
        String refreshToken = authService.auth(authRequest, Constants.REQUEST_REFRESH_TOKEN);

        // Register Refresh Token
        userService.registerRefreshToken(authRequest.getUsernameOrEmail(), refreshToken);
        // Set refresh token for cookie
        response.setHeader(HttpHeaders.SET_COOKIE, this.setRefreshTokenForCookies(Constants.REFRESH_TOKEN, refreshToken).toString());

        AuthResponse jwtAuthResponse = new AuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // validate refreshToken
        jwtTokenProvider.validateToken(refreshToken);
        // Lấy username từ token
        String username = jwtTokenProvider.getUsername(refreshToken);

        // Lấy thông tin user từ DB
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Tạo access token mới
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities()),
                userDetails.getUsername());

        AuthResponse jwtAuthResponse = new AuthResponse();
        jwtAuthResponse.setAccessToken(newAccessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Get refresh token from cookie
        String refreshToken = jwtTokenProvider.getRefreshTokenFromCookie(request);
        // validate refreshToken
        jwtTokenProvider.validateToken(refreshToken);
        // Lấy username từ token
        String username = jwtTokenProvider.getUsername(refreshToken);
        // Delete Token
        userService.deleteByRefreshToken(username);

        // Xóa cookie phía client
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN, Constants.REFRESH_TOKEN_BLANK)
                .httpOnly(true).secure(true) // production
                .path("/")
                .maxAge(0) // xóa cookie
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest userRequest) {

        authService.register(userRequest);
        return new ResponseEntity<>("Đăng ký thành công", HttpStatus.OK);
    }

    /**
     * Set token to cookie
     * 
     * @param tokenKey
     * @param tokenValue
     */
    private ResponseCookie setRefreshTokenForCookies(String tokenKey, String tokenValue) {
        return ResponseCookie.from(tokenKey, tokenValue)
                .httpOnly(true)
                .secure(false)              // ❗ Dev local bắt buộc dùng false
                .sameSite("Lax")            // ❗ Lax phù hợp cho local HTTP
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}