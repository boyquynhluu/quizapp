package com.myapp.quiz.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.quiz.dto.DiemResponse;
import com.myapp.quiz.service.DiemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/diem")
@Slf4j(topic = "Diem CONTROLLER")
@RequiredArgsConstructor
public class DiemController {

    private final DiemService diemService;

    @GetMapping
    public ResponseEntity<List<DiemResponse>> getDiems() {
        log.info("START GET DIEM CONTROLLER");

        List<DiemResponse> diems = diemService.getDiems();

        return new ResponseEntity<>(diems, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAll() {
        diemService.deleteAll();
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
