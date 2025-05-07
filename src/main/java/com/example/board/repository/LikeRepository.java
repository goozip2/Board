package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.entity.Board;
import com.example.board.entity.Like;
import com.example.board.entity.User;

import java.util.List;


public interface LikeRepository extends JpaRepository<Like, Long>{
    List<Like> findByBoard(Board board);

    Like findByBoardAndUser(Board board, User user);
}
