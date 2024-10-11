package com.MyWeb.boardComment.controller;

import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardComment.service.BoardCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class BoardCommentController {
    private final BoardCommentService boardCommentService;

    public BoardCommentController(BoardCommentService boardCommentService) {
        this.boardCommentService = boardCommentService;
    }

    @GetMapping("/comment/{commentId}/deleteComment")
    @ResponseBody
    public String deleteComment(@PathVariable("commentId")Long commentId ){
        String result = "";
        log.info("commentId => {}",commentId);
        Optional<BoardComment> bcList = boardCommentService.findById(commentId);
        BoardComment boardComment = null;

        int updateResult = 0;
        if(bcList.isPresent()) {

            boardComment = bcList.get();
            int childCount = boardComment.getComChild();
            boolean isDelete = boardComment.isComDelete();
            Long parentId = (long) boardComment.getComParentNo();

            if(isDelete){
                return "as";
            }else if(childCount == 0){
                boardCommentService.deleteBoardComment(parentId,boardComment);
                result = "s";
            }else {
                String content = "[삭제된 내용입니다]";

                updateResult = boardCommentService.changeDeleteContent(content,commentId);
                result = updateResult == 1 ? "ss" : "e";
            }
        }else{
            /**
             * bcList가 존재하지않는 경우 사실가능성없긴할듯
             */
            result = "e";
        }
        log.info("result => {}",result);
        return result;
    }

    @PostMapping("/comment/{commentId}/update")
    @ResponseBody
    public String updateComment(@PathVariable("commentId") Long commentId,
                              @RequestBody Map<String, Object> payload) {
        String content = (String) payload.get("content");
        log.info("commentId => {}", commentId);
        log.info("Content => {}", content);
        String result = "";
        Optional<BoardComment> bcList = boardCommentService.findById(commentId);
        if(bcList.isPresent()){
            int updateResult = boardCommentService.changeContent(content,commentId);
            result = updateResult == 1 ? "s" : "f";
        }else{
            result ="f";
        }
        return result;
    }
}
