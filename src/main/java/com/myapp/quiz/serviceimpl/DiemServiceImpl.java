package com.myapp.quiz.serviceimpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.quiz.dto.DiemResponse;
import com.myapp.quiz.entity.User;
import com.myapp.quiz.repository.UserRepository;
import com.myapp.quiz.service.DiemService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Service
@Slf4j(topic = "DIEM SERVICE")
@RequiredArgsConstructor
public class DiemServiceImpl implements DiemService {

    private static final String GET_ALL_DIEM_BY_NATIVE_QUERY = """
            SELECT 
                user_id, 
                user_name, 
                full_name, 
                lan_thi, 
                diem_thi, 
                ngay_thi
            FROM (
                SELECT 
                    tbl_diem.user_id, 
                    tbl_user.user_name, 
                    tbl_user.full_name, 
                    tbl_diem.lan_thi, 
                    tbl_diem.diem_thi, 
                    tbl_diem.ngay_thi, 
                    ROW_NUMBER() OVER (
                        PARTITION BY tbl_diem.user_id 
                        ORDER BY tbl_diem.diem_thi DESC
                    ) AS rn
                FROM tbl_diem
                INNER JOIN tbl_user ON tbl_diem.user_id = tbl_user.id
                WHERE tbl_diem.user_id IS NOT NULL
            ) ranked
            WHERE rn = 1
            """;

    private static final String GET_DIEM_BY_ID = """
                SELECT new com.myapp.quiz.dto.DiemResponse(
                d.user.id,
                u.username,
                u.fullName,
                d.lanThi,
                d.diemThi,
                d.ngayThi
            )
            FROM Diem d
            JOIN d.user u
            WHERE d.user.id = :userId
            ORDER BY d.diemThi DESC
            """;

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DiemResponse> getDiems() {
        List<Object[]> objs = entityManager.createNativeQuery(GET_ALL_DIEM_BY_NATIVE_QUERY).getResultList();
        log.info("START GET ALL DIEM - total: {}", objs.size());
        List<DiemResponse> diems = objs.stream().map(obj -> {
            Timestamp timestamp = (Timestamp) obj[5];
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            return new DiemResponse(
                (int) obj[0],
                (String) obj[1],
                (String) obj[2],
                (int) obj[3],
                (Double) obj[4],
                dateTime
            );
        }).toList();

        log.info("END GET ALL DIEM - total: {}", diems.size());
        return diems;
    }

    @Override
    public List<DiemResponse> getDiemsById(int userId) {
        List<DiemResponse> results = entityManager.createQuery(
                GET_DIEM_BY_ID,
                DiemResponse.class
            )
            .setParameter("userId", userId)
            .getResultList();

        log.info("END GET ALL DIEM BY ID - Total: {}", results.size());
        return results;
    }

    @Override
    public void deleteAll() {
        try {
            List<User> users = userRepository.findAll();
            for(User user : users) {
                user.getDiems().clear();
                userRepository.save(user);
            }
        } catch (Exception e) {
            log.info("Error when delete diem: {}", e.getMessage(), e);
            throw e;
        }
    }

}
