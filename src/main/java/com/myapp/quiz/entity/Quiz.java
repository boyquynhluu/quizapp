package com.myapp.quiz.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "answear1_quiz")
    String answear1Quiz;

    @Column(name = "answear2_quiz")
    String answear2Quiz;

    @Column(name = "answear3_quiz")
    String answear3Quiz;

    @Column(name = "answear4_quiz")
    String answear4Quiz;

    @Column(name = "type_quiz")
    String typeQuiz;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
    List<Answere> answeres;

}
