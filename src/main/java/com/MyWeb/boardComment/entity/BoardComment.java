package com.MyWeb.boardComment.entity;

import com.MyWeb.board.entity.Board;
import com.MyWeb.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Instant;
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class BoardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //boardComment 댓글번호

    @Column(name ="com_content")
    private String comContent;

    @Column(name = "com_date", nullable = false)
    private Timestamp comDate;

    @Column(name ="com_parent_no")
    private int comParentNo; //부모댓글번호
    @Column(name ="com_ref")
    private int comRef;   // 댓글을쓸때마다 게시글번호의 최대 Ref를 구해서
    @Column(name ="reforder")
    private int refOrder; // 대댓글 순서정할때 필요
    @Column(name ="depth")
    private int depth;    // 화면에서 margin용으로 필요한것
    @Column(name ="com_child")
    private int comChild; // 대댓글을 가지고있는 숫자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", referencedColumnName = "board_no")
    @ToString.Exclude
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @PrePersist
    protected void onCreate() {
        this.comDate = Timestamp.from(Instant.now());
    }

}
