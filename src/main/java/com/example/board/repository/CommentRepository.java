package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{    
}
