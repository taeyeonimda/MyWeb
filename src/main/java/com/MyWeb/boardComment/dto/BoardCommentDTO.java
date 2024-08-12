package com.MyWeb.boardComment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor@NoArgsConstructor
public class BoardCommentDTO {
    private Long id; //boardComment 댓글번호

    private Long userNo;
    private Long boardNo;

    private String comContent;
    private Timestamp comDate;
    private int comParentNo; //부모댓글번호
    private int comRef;   // 댓글을쓸때마다 게시글번호의 최대 Ref를 구해서
    private int refOrder; // 대댓글 순서정할때 필요
    private int depth;    // 화면에서 margin용으로 필요한것
    private int comChild; // 대댓글을 가지고있는 숫자

}
