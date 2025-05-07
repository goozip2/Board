package com.example.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.board.entity.Board;
import com.example.board.entity.User;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByTitleContainingOrContentContaining(String title, String content);

    @Query ("select b from Board b where b.user = :user and (b.title like %:keyword% or b.content like %:keyword%)")
    List<Board> searchByUserAndKeyword(@Param("user") User user, @Param("keyword") String keyword);



    Page<Board> findByTitleContaining(String title, Pageable pageable);

    @Query ("select b from Board b where b.user = :user and (b.title like %:keyword% or b.content like %:keyword%)")
    Page<Board> searchByUserAndKeywordPage(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);
}