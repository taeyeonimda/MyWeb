package com.MyWeb.boardLike.entity;

import com.MyWeb.board.entity.Board;
import com.MyWeb.user.entity.User;
import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
@ToString(exclude = {"user", "board"})
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "board_no", referencedColumnName = "board_no")
    private Board board;

    @Description("Default = 0 , 좋아요 = 1, 싫어요 = -1")
    @Column(name = "like_status")
    private int likeStatus;


//    @PrePersist
//    protected void onCreate(){
//        this.likeStatus = 0;
//    };
}
