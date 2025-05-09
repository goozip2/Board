package com.example.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class FileAtch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String oName;

    String cName;

    @OneToOne
    Board board;
}
