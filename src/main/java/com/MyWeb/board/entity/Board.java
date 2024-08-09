package com.MyWeb.board.entity;

import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardLike.entity.BoardLike;
import com.MyWeb.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", referencedColumnName = "id")
    private User user;

    @Column(name = "board_title")
    private String boardTitle;

    @Column(name = "board_content")
    private String boardContent;

    @Column(name = "board_count")
    private int boardCount; // should be an integer

    @Column(name = "user_state")
    private String userState;

    @Column(name = "board_date")
    private LocalDate boardDate;

    @Column(name = "board_filepath")
    private String boardFilePath;
    @Column(name = "board_filename")
    private String boardFileName;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BoardComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BoardLike> likes = new ArrayList<>();


    @PrePersist
    protected void onCreate(){
        this.boardDate = LocalDate.now();
        this.boardCount = 0;
    };
    public String getUserNickName(){
        return user != null ? user.getNickName() : null;
    }
}
