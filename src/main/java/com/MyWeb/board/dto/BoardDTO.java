package com.MyWeb.board.dto;

import com.MyWeb.board.entity.Board;
import com.MyWeb.user.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BoardDTO {
    private Long id;
    private Long userNo;
    private Long writer;

    private String boardTitle;
    private String boardContent;
    private int boardCount; // should be an integer
    private String userState;
    private LocalDate boardDate;


    public BoardDTO(Long id, Long writer, String boardTitle, String boardContent, int boardCount, LocalDate boardDate) {
        this.id = id;
        this.writer = writer;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardCount = boardCount;
        this.boardDate = boardDate;
    }

    // BoardDTO를 Board 엔티티로 변환하는 메소드
    public Board toEntity(User user) {
        Board board = new Board();
        board.setId(this.id);
        board.setBoardTitle(this.boardTitle);
        board.setBoardContent(this.boardContent);
        board.setUser(user); // User 객체를 통해 writer 설정
        board.setBoardCount(this.boardCount);
        board.setBoardDate(this.boardDate);
        // boardFilePath와 boardFileName이 필요하다면 추가적으로 설정
        return board;
    }


    //    public BoardDTO(String boardTitle, Long writer, LocalDate boardDate, String userState, int boardCount, String boardContent) {
//        this.boardTitle = boardTitle;
//        this.writer = writer;
//        this.boardDate = boardDate;
//        this.userState = userState;
//        this.boardCount = boardCount;
//        this.boardContent = boardContent;
//    }
//
//    // BoardDTO를 Board 엔티티로 변환하는 메소드
//    public Board toEntity(User user) {
//        Board board = new Board();
//        board.setBoardTitle(this.boardTitle);
//        board.setBoardContent(this.boardContent);
//        board.setUser(user); // User 객체를 통해 writer 설정
//        board.setBoardCount(this.boardCount);
//        board.setUserState(this.userState);
//        board.setBoardDate(this.boardDate);
//        // boardFilePath와 boardFileName이 필요하다면 추가적으로 설정
//        return board;
//    }
}
