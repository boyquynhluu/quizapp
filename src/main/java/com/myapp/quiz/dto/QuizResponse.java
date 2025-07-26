package com.myapp.quiz.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponse {
    int id;
    String question;
    String type;
    List<String> options;
    List<Integer> answers;
    String imageName;
}
