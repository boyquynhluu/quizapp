package com.myapp.quiz.dto;

import com.myapp.quiz.constants.Constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {

    String accessToken;
    String tokenType = Constants.BEARER_TOKEN;
}