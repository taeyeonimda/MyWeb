package com.MyWeb.boardLike.service;

import com.MyWeb.boardLike.entity.BoardLike;
import com.MyWeb.boardLike.repository.BoardLikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;

    public BoardLikeService(BoardLikeRepository boardLikeRepository) {
        this.boardLikeRepository = boardLikeRepository;
    }

    public Optional<BoardLike> getCheck(Long userId, Long boardId) {
        return boardLikeRepository.findByUserIdAndBoardId(userId,boardId);
    }

    @Transactional(rollbackOn = Exception.class)
    public BoardLike save(BoardLike entityBoardLike) {
        return boardLikeRepository.save(entityBoardLike);
    }

    @Transactional(rollbackOn = Exception.class)
    public BoardLike update(BoardLike entityBoardLike) {
        return boardLikeRepository.save(entityBoardLike);
    }

    public int getSumLike(Long boardId) {
        return boardLikeRepository.getSumLike(boardId).orElse(0);
    }

    public int getOneLike(Long userId, Long boardId) {
        return boardLikeRepository.getOneBoardLike(userId,boardId).orElse(0);

    }
}
