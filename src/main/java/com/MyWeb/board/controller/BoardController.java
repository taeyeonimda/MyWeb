package com.MyWeb.board.controller;

import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import com.MyWeb.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Slf4j
@Controller
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     *  기존에 사용하던목록 아래 댓글 갯수 + 좋아요 가져오는거로 변경
     */
//    @GetMapping("/boards")
//    public String listBoards(Model model,
//                             @RequestParam(name="page",defaultValue = "1")int page,
//                             @RequestParam(name="size",defaultValue = "10")int size){
//        List<Board> boards = boardService.getBoardsWithPaging(page,size);
//        log.info("boards => {} ",boards);
//        int boardCount = boardService.getBoardCount();
//        int maxPage = (int) Math.ceil((double) boardCount / size);
//
//        // 현재 페이지 그룹 계산
//        int currentGroup = (page - 1) / 5;
//        int startPage = currentGroup * 5 + 1;
//        int endPage = Math.min(startPage + 4, maxPage);
//
//        model.addAttribute("boards", boards);
//        model.addAttribute("size", size);
//        model.addAttribute("maxPage", maxPage);
//        model.addAttribute("page", page);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        return "board/boards";
//    }


    @GetMapping("/boards")
    public String listBoards(Model model,
                             @RequestParam(name="page", defaultValue = "1") int page,
                             @RequestParam(name="size", defaultValue = "10") int size) {
        Page<BoardSummaryDTO> boards = boardService.getBoardsWithCommentAndLikeCount(page, size);
        log.info("boards => {} ", boards);

        int boardCount = boardService.getBoardCount();
        int maxPage = (int) Math.ceil((double) boardCount / size);

        // 현재 페이지 그룹 계산
        int currentGroup = (page - 1) / 5;
        int startPage = currentGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, maxPage);

        model.addAttribute("boards", boards.getContent());
        model.addAttribute("size", size);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("page", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "board/boards";
    }
}
