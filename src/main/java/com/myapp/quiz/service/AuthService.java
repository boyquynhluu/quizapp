package com.myapp.quiz.service;

import com.myapp.quiz.dto.AuthRequest;
import com.myapp.quiz.dto.UserRequest;

public interface AuthService {
    String auth(AuthRequest authRequest, String requestType);

    void register(UserRequest userRequest);

    boolean checkValidToken(String token);
}
