package com.myapp.quiz.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponse {
    int id;
    String title;
    String answear1Quiz;
    String answear2Quiz;
    String answear3Quiz;
    String answear4Quiz;
    String typeQuiz;
}
