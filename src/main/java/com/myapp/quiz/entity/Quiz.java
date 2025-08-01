package com.myapp.quiz.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.myapp.quiz.utils.ListIntegerToJsonConverter;
import com.myapp.quiz.utils.ListStringToJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "tbl_quiz")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "question")
    String question;

    @Column(name = "type")
    String type;

    @Convert(converter = ListStringToJsonConverter.class)
    @Column(name = "options_json", columnDefinition = "TEXT")
    List<String> options;

    @Convert(converter = ListIntegerToJsonConverter.class)
    @Column(name = "answers_json", columnDefinition = "TEXT")
    List<Integer> answers;

    @Column(name = "image_name")
    String imageName;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
