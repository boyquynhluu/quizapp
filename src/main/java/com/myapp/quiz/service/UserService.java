package com.myapp.quiz.service;

public interface UserService {

    public void registerUser(Double diemThi);

    public void registerRefreshToken(String usernameOrPassword,  String token);

    public void deleteByRefreshToken(String username);
}
