package com.myapp.quiz.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.quiz.dto.QuizResponse;
import com.myapp.quiz.service.QuizService;
import com.myapp.quiz.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "QUIZ CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("api/v1/quiz")
public class QuizController {

    private final QuizService service;
    private final ModelMapper mapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<QuizResponse>> getAllQuiz() {
        log.info("QUIZ CONTROLLER");
        List<QuizResponse> quizDtos = service.getAllQuiz();

        List<QuizResponse> quizResponses = mapper.map(quizDtos, new TypeToken<List<QuizResponse>>() {
        }.getType());

        return new ResponseEntity<>(quizResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> checkAnsweresQuiz(@RequestBody Map<Integer, Object> answereRequestMap) {
        log.info("Ans: {} ", answereRequestMap);
        // Get all key
        Set<Integer> keys = answereRequestMap.keySet();

        Map<String, Object> map = service.checkAnswers(answereRequestMap);
        Integer correctCount = (Integer) map.get("correctCount");

        double percent = (correctCount * 100.0) / keys.size();
        map.put("percent", percent);

        // Register User
        userService.registerUser(percent);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
