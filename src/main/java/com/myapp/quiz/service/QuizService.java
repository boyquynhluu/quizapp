package com.myapp.quiz.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.myapp.quiz.dto.QuizResponse;
import com.myapp.quiz.entity.Quiz;

public interface QuizService {

    public List<QuizResponse> getAllQuiz();

    public Quiz getById(int id);

    public Quiz saveQuiz(String question, String type, String optionsJson, String answerJson, MultipartFile image)
            throws IOException;

    public Map<Integer, QuizResponse> findQuizByIds(List<Integer> quizIds);
}
