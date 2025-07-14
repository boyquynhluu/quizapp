package com.myapp.quiz.dto;

import com.myapp.quiz.validation.EmailConstraint;
import com.myapp.quiz.validation.PasswordConstraint;
import com.myapp.quiz.validation.UsernameConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @UsernameConstraint
    String username;
    String firstName;
    String lastName;
    @EmailConstraint
    String email;
    @PasswordConstraint
    String password;
}