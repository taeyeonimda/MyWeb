package com.MyWeb.board.service;

import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import com.MyWeb.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getBoardsWithPaging(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1,size);
        return boardRepository.findAll(pageRequest).getContent();
    }

    public int getBoardCount() {
        return (int)boardRepository.count();
    }

    public Page<BoardSummaryDTO> getBoardsWithCommentAndLikeCount(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return boardRepository.findAllWithCommentAndLikeCount(pageRequest);
    }
}
