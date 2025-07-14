package com.myapp.quiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myapp.quiz.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT COUNT(*) FROM tbl_user WHERE tbl_user.first_name = :firstName", nativeQuery = true)
    int countUser(@Param("firstName") String firstName);

    @Query(value = "SELECT * FROM tbl_user WHERE tbl_user.first_name = :firstName", nativeQuery = true)
    User getUserByFirstName(@Param("firstName") String firstName);

    @Query(value = "SELECT tbl_user.full_name FROM tbl_user WHERE tbl_user.email = :email", nativeQuery = true)
    String getFullnameByEmail(@Param("email") String email);

    @Query(value = "SELECT tbl_user.full_name FROM tbl_user WHERE tbl_user.user_name = :username", nativeQuery = true)
    String getFullnameByUsername(@Param("username") String username);

    @Query(value = "SELECT COUNT(*) FROM tbl_user WHERE tbl_user.user_name = :username", nativeQuery = true)
    int countUsername(@Param("username") String username);

    @Query(value = "SELECT COUNT(*) FROM tbl_user WHERE tbl_user.email = :email", nativeQuery = true)
    int countEmail(@Param("email") String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);
}
