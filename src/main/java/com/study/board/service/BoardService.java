package com.study.board.service;


import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception {

        // 1. file 저장 경로 설정
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        // 2. UUID : 파일 이름 지정할 식별자 생성 (랜덤으로 이름 생성)
        UUID uuid = UUID.randomUUID();

        // 3. 랜덤 이름 양식 설정
        String fileName = uuid + "_" + file.getOriginalFilename();

        // 파일 저장 시 projectPath 경로에 "" 지정된 이름으로 저장
        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        // 실제 DB에 파일 저장 (파일 이름 설정)
        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);

        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(Integer id) {

        return boardRepository.findById(id).get();
    }

    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    public Page<Board> boardSearchList(String searchKeyWord, Pageable pageable) {

        return boardRepository.findByTitleContaining(searchKeyWord, pageable);
    }
}
