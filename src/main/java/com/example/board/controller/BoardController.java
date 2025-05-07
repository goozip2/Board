package com.example.board.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.board.entity.Board;
import com.example.board.entity.Comment;
import com.example.board.entity.Like;
import com.example.board.entity.User;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.LikeRepository;

import jakarta.servlet.http.HttpSession;



@Controller
public class BoardController {
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	LikeRepository likeRepository;
	
	@GetMapping("/board/delete/{bid}")
	public String boardDelete(@PathVariable Long bid, HttpSession session) throws AccessDeniedException {
		User user = (User) session.getAttribute("user_info");
		Board board = boardRepository.findById(bid).orElseThrow(() -> new IllegalArgumentException("게시글 존재하지 않음"));
		if(!board.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("삭제 권한이 없습니다.");
		}
		boardRepository.deleteById(bid);
		return "redirect:/board";
	}

	@GetMapping("/board/update")
	public String boardUpdate(Model model, @RequestParam Long bid, HttpSession session) throws AccessDeniedException {
		User user = (User) session.getAttribute("user_info");
		Board board = boardRepository.findById(bid).orElseThrow(() -> new IllegalArgumentException("게시글 존재하지 않음"));
		if(!board.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		model.addAttribute("board", board);
		return "board/update";
	}
		
	@PostMapping("/board/update/{bid}")
	public String boardUpdatePost(@PathVariable Long bid, @ModelAttribute Board newBoard, HttpSession session) throws AccessDeniedException {
		User user = (User) session.getAttribute("user_info");
		Board originBoard = boardRepository.findById(bid).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		if(!originBoard.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		originBoard.setTitle(newBoard.getTitle());
		originBoard.setContent(newBoard.getContent());
		boardRepository.save(originBoard);
		return "redirect:/board";
	}

	@GetMapping("/board/view")
	public String boardView(@RequestParam Long bid, Model model, HttpSession session) {
		Board board = boardRepository.findById(bid).orElseThrow(() -> new IllegalArgumentException("Invalid board Id"));
		model.addAttribute("board", board);
		
		User user = (User) session.getAttribute("user_info");
		if(user!= null) {
			Like like = likeRepository.findByBoardAndUser(board, user);
			model.addAttribute("likeExists", like!=null);
		} else {
			model.addAttribute("likeExists", false);
		}
		return "board/view";
	}

	@GetMapping("/board")
	public String board() {
		return "redirect:/board/list";
	}

	@GetMapping("/board/list")
	public String boardList(@RequestParam(defaultValue="1") int page, @RequestParam(defaultValue = "") String search, Model model) {
		
		Sort sort = Sort.by(Direction.DESC, "id");
		Pageable pageable = PageRequest.of(page-1, 10, sort);
		Page<Board> p = boardRepository.findByTitleContaining(search, pageable);

		List<Board> list = p.getContent();
		// Long totalElements = p.getTotalElements();
		int totalPages = p.getTotalPages();
		int startPage = (page-1)/10 * 10 +1;
		int endPage = Math.min(startPage+9, totalPages);
		int offset = (page - 1) * 10;
		model.addAttribute("offset", offset);
		model.addAttribute("list", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		return "board/list";
	}

	@GetMapping("/board/list/my")
	public String getMethodName(@RequestParam(defaultValue="1") int page, @RequestParam(defaultValue = "") String search, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user_info");
		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(page - 1, 10, sort);

		Page<Board> p = boardRepository.searchByUserAndKeywordPage(user, search, pageable);

		List<Board> list = p.getContent();
		int totalPages = p.getTotalPages();
		int startPage = (page - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 9, totalPages);
		int offset = (page - 1) * 10;
		model.addAttribute("offset", offset);
		model.addAttribute("list", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("search", search); // 검색어 유지용
		return "board/list";
	}
	

	@GetMapping("/board/write")
	public String boardWrite(HttpSession session) {
		if(session.getAttribute("user_info") == null) {
			return "signin";
		}
		return "board/write";
	}
	
	@PostMapping("/board/write")
	public String boardWritePost(@ModelAttribute Board  board, HttpSession session) {
		if(session.getAttribute("user_info") == null) {
			return "signin";
		}
		User user = (User) session.getAttribute("user_info");
		board.setUser(user);
		boardRepository.save(board);
		return "redirect:/board";
	}

	@PostMapping("/board/comment/write")
	public String commentWritePost(@ModelAttribute Comment comment, HttpSession session, @RequestParam Long bid) {
		//commnet 객체 (내용밖에 없음)에 user_id와 board_id 추가해서 save
		if(session.getAttribute("user_info") == null) {
			return "signin";
		}
		Board board = boardRepository.findById(bid).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		User user = (User) session.getAttribute("user_info");
		comment.setUser(user);
		comment.setBoard(board);
		commentRepository.save(comment);
		return "redirect:/board/view?bid=" + bid;
	}

	@GetMapping("/board/comment/delete/{cid}/{bid}")
	public String getMethodName(@PathVariable Long cid, @PathVariable Long bid, HttpSession session) throws AccessDeniedException {
		User sessionUser = (User) session.getAttribute("user_info");
		Comment comment = commentRepository.findById(cid).orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

		if (!comment.getUser().getId().equals(sessionUser.getId())) {
			throw new AccessDeniedException("삭제 권한이 없습니다.");
		}
		
		commentRepository.deleteById(cid);
		return "redirect:/board/view?bid=" + bid;
	}

	@GetMapping("/board/like")
	public String boardLike(@RequestParam Long id, HttpSession session) {
		Board board = new Board();
		board.setId(id);

		User user = (User) session.getAttribute("user_info");
		Like like = likeRepository.findByBoardAndUser(board, user);
		if(like != null) {
			likeRepository.delete(like);
		} else {
			like = new Like();
			like.setBoard(board);
			like.setUser(user);
			likeRepository.save(like);
		}
		
		return "redirect:/board/view?bid=" +id;
	}
	
	
	
}