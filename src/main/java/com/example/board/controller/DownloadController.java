package com.example.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.board.entity.Board;
import com.example.board.entity.FileAtch;
import com.example.board.repository.FileAtchRepository;

@Controller
public class DownloadController {

    @Autowired
    FileAtchRepository fileAtchRepository;

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam Long boardId)
            throws FileNotFoundException, UnsupportedEncodingException {
        Board board = new Board();
        board.setId(boardId);
        FileAtch fileAtch = fileAtchRepository.findByBoard(board);
        String cName = fileAtch.getCName();
        File file = new File("c:/upload/board/" + cName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header("content-disposition",
                        "attachment; filename=\"" +
                                URLEncoder.encode(file.getName(), "utf-8") + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
