package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


@Controller
// 스프링이 BoardController 라는 클래스가 'Controller' 라는 것을 인식

public class BoardController {

    @Autowired
    private BoardService boardService;

    // 글 작성
    @GetMapping("/board/write") // 어떤 url 로 접근 할 것인지 (localhost:8090/board/write)
    public String boardWriteForm() {

        return "boardwrite"; // 연동시킬 html 파일 지정 (boardWrite)
    }


    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, @RequestParam(name="file", required = false)
    MultipartFile file) throws Exception {

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");

        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    // 게시글 리스트
    @GetMapping("/board/list")
    // @PageableDefault : 페이징 처리 기능 어노테이션 (page, size 설정, sort: 페이지 설정 기준 (id or title 의 오름 or 내림차순)
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(name = "searchKeyWord", defaultValue = "") String searchKeyWord){

        Page<Board> list = null;

        if (searchKeyWord == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyWord, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1); // Math.max : 매개변수로 받은 페이지 중 높은 갚으로 무조건 반환시켜줌 -가 될 수 없음 (무조건 1보다 커야됨)
        int endPage = Math.min(nowPage + 5, list.getTotalPages()); // Math.min : 매개변수로 받은 페이지가 최대 페이지 수를 넘길 수 없도록 설정

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    // 특정 게시글 불러오기 (get방식)
    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam("id")int id) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardview";
    }

//    글 삭제
//    @GetMapping("/board/delete")
//    public String boardDelete(@RequestParam("id")int id) {
//
//        boardService.boardDelete(id);
//
//        return "redirect:/board/list"; // 게시물 삭제 후 이동할 페이지를 리턴타입으로 지정
//    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

    // 게시글 수정 기능
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, @RequestParam(name="file", required = false)
    MultipartFile file) throws Exception {

        // 기존에 있는 데이터 객체를 생성하여 그 객체에 새로운 수정 데이터를 덮어 씌우는 방식이다.
        Board boardTemp = boardService.boardView(id); // 기존 글 객체를 boardTemp 로 생성
        boardTemp.setTitle(board.getTitle()); // 제목 수정 (title)
        boardTemp.setContent(board.getContent()); // 내용 수정 (content)

        boardService.write(boardTemp, file);

        // 게시글 수정 시 message 를 띄운다
        // message 띄운 후 attributeValue 를 통해 /board/list 경로로 이동 해준다.
        model.addAttribute("message", "글 수정이 완료 되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "redirect:/board/list";
    }
}
