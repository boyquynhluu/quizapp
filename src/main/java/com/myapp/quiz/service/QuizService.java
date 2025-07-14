package com.myapp.quiz.service;

import java.util.List;
import java.util.Map;

import com.myapp.quiz.dto.QuizResponse;

public interface QuizService {

    public List<QuizResponse> getAllQuiz();

    public Map<String, Object> checkAnswers(Map<Integer, Object> map);
}
