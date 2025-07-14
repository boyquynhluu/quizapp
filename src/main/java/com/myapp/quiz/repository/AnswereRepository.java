package com.myapp.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myapp.quiz.entity.Answere;

@Repository
public interface AnswereRepository extends JpaRepository<Answere, Integer> {

    @Query(value = "SELECT answered FROM tbl_answere WHERE tbl_answere.quiz_id = :quizId", nativeQuery = true)
    List<String> getAllAnswere(@Param("quizId") Integer id);
}
