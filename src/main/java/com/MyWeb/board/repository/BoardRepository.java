package com.MyWeb.board.repository;

import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board,Long> {

    @Query("SELECT new com.MyWeb.board.dto.BoardSummaryDTO(b.id, b.user.id, b.user.nickName, b.boardTitle, " +
        "b.boardCount, b.boardDate, " +
        "COUNT(DISTINCT bc.id), COUNT(DISTINCT bl.id)) " +
        "FROM Board b " +
        "LEFT JOIN b.comments bc " +
        "LEFT JOIN b.likes bl " +
        "GROUP BY b.id, b.user.id, b.user.nickName, b.boardTitle, b.boardCount, b.boardDate")
    Page<BoardSummaryDTO> findAllWithCommentAndLikeCount(PageRequest pageable);


}
