package com.example.board.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String pwd;
	private String name;

	@OneToMany (mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
	List<Board> boards;
}