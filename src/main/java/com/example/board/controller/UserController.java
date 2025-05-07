package com.example.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.board.entity.User;
import com.example.board.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired UserRepository userRepository;
	@Autowired BCryptPasswordEncoder encoder;

	@GetMapping("/signin")
	public String signin() {
		return "signin";
	}
	
	@PostMapping("/signin")
	public String signinPost(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
		List<User> userList = userRepository.findByEmail(user.getEmail());
		if(userList.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "존재하지 않는 이메일입니다.");
			return "redirect:/signin"; 
		}
		User loginUser = userList.get(0);
		String rawPw = user.getPwd();
		String encodedPw = loginUser.getPwd();
		boolean ok = encoder.matches(rawPw, encodedPw);
		if(ok) {
			session.setAttribute("user_info", loginUser);
		} else {
			redirectAttributes.addFlashAttribute("error", "비밀번호를 확인해주세요.");
			return "redirect:/signin";
		}
		return "redirect:/";
	}

	@GetMapping("/signout")
	public String signout(HttpSession session) {
		session.setAttribute("user_info", null);
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/signup") 
	public String signup() {
		return "signup";
	}

	@PostMapping("/signup")
	public String signupPost(@ModelAttribute User user) {
		String encodedPw = encoder.encode(user.getPwd());
		user.setPwd(encodedPw);
		userRepository.save(user);
		return "redirect:/signin";
	}
}