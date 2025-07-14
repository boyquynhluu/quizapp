package com.myapp.quiz.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.myapp.quiz.dto.AnswereResponse;
import com.myapp.quiz.dto.QuizResponse;
import com.myapp.quiz.entity.Quiz;
import com.myapp.quiz.repository.AnswereRepository;
import com.myapp.quiz.repository.QuizRepository;
import com.myapp.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "QUIZ SERVICE")
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final AnswereRepository answereRepository;
    private final ModelMapper mapper;

    @Override
    public List<QuizResponse> getAllQuiz() {
        try {
            log.info("START QUIZ SERVICE");

            List<Quiz> quizs = quizRepository.findAll();

            return mapper.map(quizs, new TypeToken<List<QuizResponse>>() {
            }.getType());
        } catch (Exception e) {
            log.error("Get All Quiz Has Error: {}", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> checkAnswers(Map<Integer, Object> answereRequest) {
        log.info("START CHECK ANSWERS");
        int correctCount = 0;
        Map<String, Object> map = new HashMap<>();
        List<AnswereResponse> resultChecks = new ArrayList<>();

        for (Map.Entry<Integer, Object> entry : answereRequest.entrySet()) {
            AnswereResponse result = new AnswereResponse();
            Integer quizId = entry.getKey();
            // Set quizId
            result.setId(quizId);
            List<String> correctAnswers = answereRepository.getAllAnswere(quizId);
            Object userAswerer = entry.getValue();

            if (userAswerer instanceof List) {
                List<?> submittedList = (List<?>) userAswerer;
                boolean isEqual = CollectionUtils.isEqualCollection(submittedList, correctAnswers);
                if (isEqual) {
                    correctCount += 1;
                    result.setChecked(Boolean.TRUE);
                } else {
                    result.setChecked(Boolean.FALSE);
                }
            } else {
                String value = (String) userAswerer;
                if (correctAnswers.contains(value)) {
                    correctCount += 1;
                    result.setChecked(Boolean.TRUE);
                } else {
                    result.setChecked(Boolean.FALSE);
                }
            }
            resultChecks.add(result);
        }
        map.put("result", resultChecks);
        map.put("correctCount", correctCount);
        return map;
    }
}
