package com.myapp.quiz.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.myapp.quiz.constants.Constants;
import com.myapp.quiz.exceptionhandler.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j(topic = "JWTTOKEN-PROVIDER")
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String CLAIM_FULL_NAME = "fullName";
    public static final String CLAIM_AUTHORITIES = "authorities";

    @Value(Constants.API_JWT_SECRET)
    private String jwtSecret;

    @Value(Constants.API_JWT_EXPIRATION_MILLISECONDS)
    private long jwtExpirationDate;

    @Value(Constants.API_JWT_REFRESH_EXPIRATION_MILLISECONDS)
    private long jwtRefreshExpirationDate;

    /**
     * Generated Access Token
     * 
     * @param authentication
     * @param fullName
     * @return
     */
    public String generateAccessToken(Authentication authentication, String fullName) {
        return generateToken(authentication, fullName, jwtExpirationDate);
    }

    /**
     * Generated Refresh Token
     * 
     * @param authentication
     * @param fullName
     * @return
     */
    public String generateRefreshToken(Authentication authentication, String fullName) {
        return generateToken(authentication, fullName, jwtRefreshExpirationDate);
    }

    /**
     * Generate Token
     * 
     * @param authentication
     * @return token
     */
    private String generateToken(Authentication authentication, String fullName, Long expireDateAccessOrRefresh) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expireDateAccessOrRefresh);

        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_FULL_NAME, fullName)
                .claim(CLAIM_AUTHORITIES, authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // get username from Jwt token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(cleanToken(token))
                .getBody();
        return claims.getSubject();
    }

    // validate Jwt token
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(cleanToken(token)); // ✅ kiểm tra chữ ký luôn
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e);
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e);
            throw new CustomException("JWT expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new CustomException("Invalid JWT", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e);
        }
    }

    // Get refresh token
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 
     * @param token
     * @return
     */
    private String cleanToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }
}
