package com.MyWeb.File.entity;

import com.MyWeb.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor@NoArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_name")
    private String fileName;

    @Column(name="file_path")
    private String filePath;

    @Column(name="type")
    private String type;

    @Column(name="refId")
    private Long refId;

    @JoinColumn (name="user_id")
    @ManyToOne
    private User user;
}
