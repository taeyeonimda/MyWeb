package com.MyWeb.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="chat_no")
    private String chatNo;

    @Column(name="chat_member")
    private String chatMember;

    @Column(name = "chat_content")
    private String chatContent;

    @Column(name="filepath")
    private String filepath;

    @Column(name="chat_date")
    private LocalDateTime chatDate;
}
