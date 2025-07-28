package com.myapp.quiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.quiz.dto.AnswereRequest;
import com.myapp.quiz.dto.AnswereResponse;
import com.myapp.quiz.dto.QuizRequest;
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

    private final QuizService quizService;
    private final ModelMapper mapper;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<QuizResponse>> getRandomQuizzes() {
        log.info("QUIZ CONTROLLER");
        List<QuizResponse> quizDtos = quizService.getRandomQuizzes();

        List<QuizResponse> quizResponses = mapper.map(quizDtos, new TypeToken<List<QuizResponse>>() {
        }.getType());

        return new ResponseEntity<>(quizResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> checkAnsweresQuiz(@RequestBody List<AnswereRequest> answereRequests) {
        log.info("Ans: {} ", answereRequests);
        // Lấy tất cả quiz một lần
        List<Integer> quizIds = answereRequests.stream().map(AnswereRequest::getQuizId).toList();

        // Get All Quiz By Ids
        Map<Integer, QuizResponse> quizMap = quizService.findQuizByIds(quizIds);

        List<AnswereResponse> answereResponses = new ArrayList<>();
        int correctCount = 0;

        for (AnswereRequest ans : answereRequests) {
            QuizResponse quiz = quizMap.get(ans.getQuizId());

            boolean isCorrect = quiz != null && checkAnswered(ans, quiz);
            if (isCorrect) {
                correctCount++;
            }
            answereResponses.add(new AnswereResponse(ans.getQuizId(), isCorrect));
        }

        double percent = ((double) correctCount / answereRequests.size()) * 100;
        // Register User
        userService.registerUser(percent);

        // Set Response
        Map<String, Object> map = Map.of(
                "answereResponses", answereResponses,
                "correctCount", correctCount,
                "percent", percent
        );

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQuiz(@RequestPart("quizzes") String quizzesJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        // Parse JSON thành list
        List<QuizRequest> quizList = objectMapper.readValue(quizzesJson, new TypeReference<>() {
        });

        for (int i = 0; i < quizList.size(); i++) {
            QuizRequest quiz = quizList.get(i);
            MultipartFile image = null;

            // Match ảnh theo index
            if (quiz.getImageIndex() != null && images != null && Integer.valueOf(quiz.getImageIndex()) < images.size()) {
                image = images.get(Integer.valueOf(quiz.getImageIndex()));
            }

            quizService.saveQuiz(quiz.getQuestion(), quiz.getType(),
                         objectMapper.writeValueAsString(quiz.getOptions()),
                         objectMapper.writeValueAsString(quiz.getAnswer()),
                                 image);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

/**
 * 
 * @param answereRequest
 * @return
 */
    private boolean checkAnswered(AnswereRequest answereRequest, QuizResponse quiz) {
        if (Objects.isNull(quiz)) {
            return false;
        }

        // Lấy đáp án đúng
        Set<String> correctAnswers = quiz.getAnswers().stream()
                .map(i -> quiz.getOptions().get(i))
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());

        // Lấy đáp án user chọn
        Set<String> userAnswers = answereRequest.getAnswers().stream()
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());

        return userAnswers.equals(correctAnswers);
    }

}
