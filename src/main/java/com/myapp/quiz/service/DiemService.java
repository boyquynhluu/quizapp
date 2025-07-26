package com.myapp.quiz.service;

import java.util.List;

import com.myapp.quiz.dto.DiemResponse;

public interface DiemService {

    List<DiemResponse> getDiems();

    List<DiemResponse> getDiemsById(int userId);

    void deleteAll();
}
