package com.myapp.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myapp.quiz.entity.Diem;

@Repository
public interface DiemRepository extends JpaRepository<Diem, Integer> {

    @Query(value = "SELECT * FROM tbl_diem WHERE tbl_diem.user_id = :userId AND tbl_diem.lan_thi = (SELECT MAX(lan_thi) FROM tbl_diem WHERE user_id = :userId)", nativeQuery = true)
    Diem getDiem(@Param("userId") int id);
}
