package com.example.board.filter;

import java.io.IOException;

import com.example.board.entity.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String url = req.getRequestURI();
        System.out.println(url);

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user_info");
        if(user == null && url.equals("/board/write")) {
            log.info("/board/write 필터 수행!!!!!!!!!!!");
            res.sendRedirect("/signin");
            return;
        }

        if(user != null && url.equals("/signin")) {
            log.info("/signin 필터 수행!!!!!!!!!!!");
            res.sendRedirect("/board/list");
            return;
        }
        chain.doFilter(request, response);
    }
    
}
