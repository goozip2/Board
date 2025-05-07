package com.example.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmailAndPwd(String email, String pw);
    List<User> findByEmail(String email);
}