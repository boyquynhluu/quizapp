package com.myapp.quiz.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.quiz.dto.QuizResponse;
import com.myapp.quiz.entity.Quiz;
import com.myapp.quiz.repository.QuizRepository;
import com.myapp.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "QUIZ SERVICE")
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ModelMapper mapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public Quiz saveQuiz(String question, String type, String optionsJson, String answerJson, MultipartFile image)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Quiz quiz = new Quiz();
        quiz.setQuestion(question);
        quiz.setType(type);
        quiz.setOptions(objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {}));
        quiz.setAnswers(objectMapper.readValue(answerJson, new TypeReference<List<Integer>>() {}));

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Files.copy(image.getInputStream(), Paths.get(uploadDir).resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            quiz.setImageName(fileName);
        }

        return quizRepository.save(quiz);
    }

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
    public Map<Integer, QuizResponse> findQuizByIds(List<Integer> quizIds) {
        return quizRepository.findAllById(quizIds).stream()
                .sorted(Comparator.comparingInt(q -> quizIds.indexOf(q.getId())))
                .collect(Collectors.toMap(
                        Quiz::getId,
                        quiz -> mapper.map(quiz, QuizResponse.class),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Quiz getById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

}
