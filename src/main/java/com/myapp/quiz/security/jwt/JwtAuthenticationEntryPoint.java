package com.myapp.quiz.security.jwt;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j(topic = "JwtAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARSET_UTF_8 = "UTF-8";
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.info("Start JwtAuthenticationEntryPoint:");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> error = Map.of(
                "error", "Unauthorized",
                "path", request.getRequestURI(),
                "timestamp", System.currentTimeMillis()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARSET_UTF_8);
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
