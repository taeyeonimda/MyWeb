package com.MyWeb.boardLike.dto;

import com.MyWeb.board.entity.Board;
import com.MyWeb.boardLike.entity.BoardLike;
import com.MyWeb.user.entity.User;
import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeDTO {
    private Long id;
    private User user;
    private Board board;
    private int likeStatus;


    public BoardLike toEntity(){
        return BoardLike.builder()
                .id(this.id)
                .user(this.user)
                .board(this.board)
                .likeStatus(this.likeStatus)
                .build();
    }
}
