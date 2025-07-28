package com.myapp.quiz.security.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myapp.quiz.exceptionhandler.CustomException;
import com.myapp.quiz.serviceimpl.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j(topic = "JWT FILTER")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        // ✅ Bỏ qua các path không cần kiểm tra token
        if (uri.equals("/api/auth/login")||
                uri.equals("/api/auth/logout") ||
                uri.equals("/api/auth/refresh") ||
                uri.equals("/api/auth/register") ||
                uri.contains("/swagger-ui") ||
                uri.contains("/v3/api-docs") ||
                uri.contains("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        try {
            if (StringUtils.hasText(token)) {
                jwtTokenProvider.validateToken(token); // ném lỗi nếu có

                String username = jwtTokenProvider.getUsername(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired in filter: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token expired");
            return;
        } catch (CustomException ex) {
            log.error("Invalid token: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Get token by request header
     * 
     * @param request
     * @return
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info(">>> Raw Authorization header: '{}'", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            log.info(">>> Extracted JWT token: '{}'", token);
            return token;
        }

        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
