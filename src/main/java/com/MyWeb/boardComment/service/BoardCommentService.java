package com.MyWeb.boardComment.service;

import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardComment.repository.BoardCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BoardCommentService {
    private final BoardCommentRepository boardCommentRepository;

    public BoardCommentService(BoardCommentRepository boardCommentRepository) {
        this.boardCommentRepository = boardCommentRepository;
    }

    public Optional<BoardComment> getOneboardComment(int comParentNo) {
        Long id = (long) comParentNo;
        return boardCommentRepository.findById(id);
    }

    @Transactional
    public void updateRefOrderPlus(int ref, int refOrder, Long boardNo) {
        boardCommentRepository.updateRefOrderPlus(ref,refOrder,boardNo);
    }

    public int findBySumAnswerNum(int ref, Long boardCommentNo) {
        return boardCommentRepository.findBySumAnswerNum(ref,boardCommentNo);
    }

    public int findMaxStep(int ref, Long boardCommentNo) {
        return boardCommentRepository.findMaxStep(ref,boardCommentNo);

    }

    public Optional<BoardComment> findById(Long commentId) {
        return boardCommentRepository.findById(commentId);
    }

    @Transactional
    public void deleteBoardComment(BoardComment boardComment) {
        boardCommentRepository.delete(boardComment);
    }

    @Transactional
    public int changeContent(String content, Long commentId) {
        return boardCommentRepository.changeContent(content,commentId);
    }

}
