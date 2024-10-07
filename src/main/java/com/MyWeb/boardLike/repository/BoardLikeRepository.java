package com.MyWeb.boardLike.repository;

import com.MyWeb.boardLike.entity.BoardLike;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
//    @Query("SELECT bl FROM BoardLike bl" +
//            " WHERE bl.user.id =:userId and bl.board.id =:boardId " +
//            " ORDER BY bl.id " +
//            " LIMIT 1  ")
//    List<BoardLike> findUserAndBoard(@Param("userId") Long userId, @Param("boardId")Long boardId);

    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);


    @Query("SELECT SUM(bl.likeStatus) as boardSum FROM BoardLike bl" +
            " WHERE bl.board.id =:board_no ")
    Optional<Integer> getSumLike(@Param("board_no") Long board_no);

    @Query("SELECT bl.likeStatus FROM BoardLike bl" +
            " WHERE bl.board.id =:board_no " +
            " AND bl.user.id =:user_no")
    Optional<Integer> getOneBoardLike(@Param("user_no")Long userId,
                                      @Param("board_no")Long boardId);
}
