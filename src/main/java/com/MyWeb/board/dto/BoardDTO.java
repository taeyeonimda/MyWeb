package com.MyWeb.board.dto;

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

    private String boardFilePath;
    private String boardFileName;
}
