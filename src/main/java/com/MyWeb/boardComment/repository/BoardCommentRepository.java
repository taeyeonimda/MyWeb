package com.MyWeb.boardComment.repository;

import com.MyWeb.boardComment.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment,Long> {

    @Query("SELECT bc "+
           "FROM BoardComment bc "+
           "WHERE board.id =:boardNo ORDER BY bc.comRef, bc.refOrder " )
    List<BoardComment> findByBoard_Id(@Param("boardNo")Long boardNo);

    @Query("SELECT COALESCE(MAX(c.comRef), 0) " +
            "FROM BoardComment c WHERE c.board.id = :boardNo")
    int findMaxRef(@Param("boardNo")Long boardNo);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BoardComment c" +
            " SET c.refOrder = c.refOrder + 1" +
            " WHERE c.comRef = :ref AND c.refOrder > :refOrder" +
            " AND c.board.id = :boardNo")
    void updateRefOrderPlus(@Param("ref") int ref, @Param("refOrder") int refOrder, @Param("boardNo") Long boardNo);

    @Query("SELECT SUM(c.comChild) FROM BoardComment c " +
            "WHERE c.comRef = :ref AND c.id = :boardCommentNo " )
    int findBySumAnswerNum(@Param("ref")int ref, @Param("boardCommentNo")Long boardCommentNo);

    @Query("SELECT MAX(c.depth) FROM BoardComment c WHERE c.comRef = :ref AND c.id = :boardCommentNo")
    int findMaxStep(@Param("ref") int ref, @Param("boardCommentNo") Long boardCommentNo);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BoardComment c" +
            " SET c.comChild = c.comChild + 1" +
            " WHERE c.id = :boardComId " +
            " AND c.board.id = :boardId ")
    void updateComChild(@Param("boardComId")Long boardCommentId, @Param("boardId")Long boardId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BoardComment c" +
            " SET c.comContent =:comContent ,  " +
            " c.comDelete = true " +
            " WHERE c.id = :commentId ")
    int changeContent(@Param("comContent") String content, @Param("commentId")Long commentId);
}
