package com.myapp.quiz.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiemResponse {

    int userId;
    String username;
    String fullName;
    int lanThi;
    Double diemThi;
    LocalDateTime ngayThi;
}
