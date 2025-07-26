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

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest, HttpServletRequest request,
            HttpServletResponse response) {
        log.info("START LOGIN CONTROLLER");
        String accessToken = authService.auth(authRequest, Constants.REQUEST_ACCESS_TOKEN);
        String refreshToken = authService.auth(authRequest, Constants.REQUEST_REFRESH_TOKEN);

        // Register Refresh Token
        userService.registerRefreshToken(authRequest.getUsernameOrEmail(), refreshToken);

        // Set Token in cookie
        this.setTokenInCookie(response, accessToken, refreshToken);

        AuthResponse jwtAuthResponse = new AuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("START REFRESH TOKEN");
        // Get refresh token from cookie
        String refreshToken = jwtTokenProvider.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token found");
        }
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

        // Set Token in cookie
        this.setTokenInCookie(response, newAccessToken, refreshToken);

        AuthResponse jwtAuthResponse = new AuthResponse();
        jwtAuthResponse.setAccessToken(newAccessToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("START LOGOUT TOKEN");
        // Get refresh token from cookie
        String refreshToken = jwtTokenProvider.getRefreshTokenFromCookie(request);
        // validate refreshToken
        jwtTokenProvider.validateToken(refreshToken);
        // Lấy username từ token
        String username = jwtTokenProvider.getUsername(refreshToken);
        // Delete Token
        userService.deleteByRefreshToken(username);

        // Xóa cả refresh token và access token cookie
        ResponseCookie deleteRefresh = ResponseCookie.from(Constants.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(false) // Dev false, Prod true
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie deleteAccess = ResponseCookie.from(Constants.ACCESS_TOKEN, "")
                .httpOnly(false) // access token FE có thể đọc
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());

        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("START REGISTER TOKEN");
        authService.register(userRequest);
        return new ResponseEntity<>("Đăng ký thành công", HttpStatus.OK);
    }

    /**
     * 
     * @param response
     * @param accessToken
     * @param refreshToken
     */
    private void setTokenInCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = setCookie(Constants.ACCESS_TOKEN, accessToken, false); // FE có thể đọc
        ResponseCookie refreshCookie = setCookie(Constants.REFRESH_TOKEN, refreshToken, true); // HttpOnly

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * Set token to cookie
     * 
     * @param tokenKey
     * @param tokenValue
     */
    private ResponseCookie setCookie(String name, String value, boolean httpOnly) {
        return ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(false) // local dev
//                .sameSite("Lax")
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}