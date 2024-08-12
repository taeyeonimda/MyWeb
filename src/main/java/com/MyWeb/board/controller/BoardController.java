package com.MyWeb.board.controller;

import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import com.MyWeb.board.service.BoardService;
import com.MyWeb.boardComment.dto.BoardCommentDTO;
import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardComment.service.BoardCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class BoardController {
    private final BoardService boardService;
    private final BoardCommentService boardCommentService;
    public BoardController(BoardService boardService, BoardCommentService boardCommentService) {
        this.boardService = boardService;
        this.boardCommentService = boardCommentService;
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

    //게시판 목록 불러오기
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

    //게시물 하나 불러오기
    @GetMapping("/getBoard")
    public String getOneBoard(@RequestParam("boardNo")Long id,
                              Model model){
        Optional<Board> board = boardService.getOneBoard(id);
        if (board.isPresent()) {
            model.addAttribute("board", board);
        }

        List<BoardCommentDTO> bcList = boardService.getAllComment(id);
        if (!bcList.isEmpty()) {
            model.addAttribute("bcList", bcList);
            System.out.println("bcList 여기니 => " + bcList);
        }
        return "board/boardDetail";
    }

    //게시물에서 댓글 작성
    @PostMapping("/boards/{boardNo}/comments")
    @ResponseBody
    public String insertComment(@PathVariable("boardNo") Long boardNo,
                                @RequestBody BoardCommentDTO boardCommentDto
    ) {
        Long userNo = boardCommentDto.getUserNo();

        String comContent = boardCommentDto.getComContent();


        // 댓글의 Ref값 최대구해서 +1씩해서 전달
        int maxRef = boardService.getMaxRef(boardNo) + 1;
//        System.out.println("컨트롤러 maxRef => "+maxRef);
        BoardComment result = boardService.insertComment(boardNo, userNo, comContent, maxRef);
//        System.out.println("컨트롤러 result => "+result);


        if (result != null) {
            return "1";
        } else {
            return "0";
        }
    }
    
    //댓글 작성 후 새롭게 전체목록 가져와서 다시보여주기
    @GetMapping("/boards/{boardNo}/comments")
    @ResponseBody
    public List<BoardCommentDTO> insertComment(@PathVariable("boardNo") Long boardNo, Model model) {
        System.out.println("댓글달고 도는지 확인");
        // 새로운 댓글 목록을 조회하여 반환
        List<BoardCommentDTO> bcList = boardService.getAllComment(boardNo);
//        System.out.println("bcList => "+bcList);
        //댓글 작성 후 기존 bcList덮어쓰기
        model.addAttribute("bcList", bcList);
        System.out.println("모델값 확인 => " + model.getAttribute("bcList"));
        return bcList;
    }


    @PostMapping("/boards/{boardNo}/reComments")
    @ResponseBody
    public String insertReComment(@PathVariable("boardNo") Long boardNo,
                                @RequestBody BoardCommentDTO boardCommentDTO) {
//        System.out.println(boardCommentDto);
//
        Long userNo = boardCommentDTO.getUserNo();
        String comContent = boardCommentDTO.getComContent();
        int comParentNo= boardCommentDTO.getComParentNo();
        int comRef= boardCommentDTO.getComRef();
        int refOrder=boardCommentDTO.getRefOrder();
        int depth=boardCommentDTO.getDepth();
        int comChild=boardCommentDTO.getComChild();
        Long boardId = boardCommentDTO.getBoardNo();
        log.info("boardNo : {} ",boardNo);
        log.info("boardCommentDTO : {} ",boardCommentDTO);

        BoardComment bc = new BoardComment();

        // 댓글 참조번호
        Optional<BoardComment> optionalBc = boardCommentService.getOneboardComment(comParentNo);

        if(optionalBc.isPresent()){
            bc = optionalBc.get();
        }
        log.info("bc  : {}",bc);
//
        int refOrderResult = refOrderAndUpdate(bc);

        System.out.println("112줄 refOrder => "+refOrderResult);
        if(refOrderResult == 0) {
            return null;
        }
        depth = depth+1;


        BoardComment result = boardService.insertReComment(
                boardId, userNo, refOrder,comChild,
                comContent, comParentNo,
                refOrderResult, depth, comRef);

        if (result != null) {
            //대댓글 성공적으로 저장 후 부모 댓글 child+1
            boardService.updateComChild(comParentNo,boardId);
            return "1";
        } else {
            return "0";
        }
    }

    private int refOrderAndUpdate(BoardComment bc){
        Long boardCommentNo = bc.getId();
        int saveStep = bc.getDepth() + 1;
        int refOrder = bc.getRefOrder();
        int answerNum = bc.getComChild();
        int ref = bc.getComRef();
        Long boardNo = bc.getBoard().getId();

        // 부모 댓글그룹의 answerNum(자식수)
        int answerNumSum = boardCommentService.findBySumAnswerNum(ref, boardCommentNo);
        /*
         * SELECT SUM(com_child) FROM board_comment WHERE com_ref = #{comRef} and
         * board_no = #{boardNo}
         */
        System.out.println("answerNumSum => "+answerNumSum);
        // 부모 댓글그룹의 최댓값 step
        int maxStep = boardCommentService.findMaxStep(ref, boardCommentNo);
        /**
         * SELECT MAX(depth) FROM board_comment WHERE com_ref = #{comRef} AND board_no =
         * #{boardNo}
         */

//        System.out.println("===refOrderAndUpdate===");
//        System.out.println("saveStep=> "+saveStep);
//        System.out.println("maxStep=> "+maxStep);
//        System.out.println("refOrder=> "+refOrder);
//        System.out.println("ref=> "+ref);
//        System.out.println("answerNum=> "+answerNum);
//        System.out.println("boardNo=> "+boardNo);


        if (saveStep < maxStep) {
            return answerNumSum + 1;
        } else if (saveStep == maxStep) {
            boardCommentService.updateRefOrderPlus(ref, refOrder + answerNum,boardNo);
            return refOrder + answerNum + 1;
        } else {
            boardCommentService.updateRefOrderPlus(ref, refOrder, boardNo);
            return refOrder + 1;
        }
    }
}
