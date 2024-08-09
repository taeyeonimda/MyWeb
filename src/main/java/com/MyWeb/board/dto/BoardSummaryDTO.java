package com.MyWeb.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor@NoArgsConstructor
public class BoardSummaryDTO {
    private Long boardId;
    private Long writer;
    private String nickName;
    private String boardTitle;
    private int boardCount;
    private LocalDate boardDate;
    private Long commentCount;
    private Long likeCount;


}
