package com.myapp.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.myapp.quiz.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    Quiz getQuizById(int id);

    @Query(value = "SELECT * FROM tbl_quiz ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Quiz> findRandom10Quizzes();

    @Query(value = "SELECT * FROM tbl_quiz ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
    List<Quiz> findTop10ByOrderByCreatedAtDesc();
}
