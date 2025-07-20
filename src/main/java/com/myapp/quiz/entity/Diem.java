package com.myapp.quiz.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "tbl_diem")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Diem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "lan_thi")
    int lanThi;

    @Column(name = "diem_thi")
    Double diemThi;

    @Column(name = "ngay_thi")
    LocalDateTime ngayThi;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

}