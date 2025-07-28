package com.myapp.quiz.service;

import com.myapp.quiz.entity.User;

public interface VerificationTokenService {

    void saveTokenRegister(User use, String token);
}
