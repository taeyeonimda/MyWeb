package com.MyWeb.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "user_pwd")
    private String userPwd;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickName;

    @Column(name = "is_sns", nullable = false)
    private char isSns;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "address")
    private String address;
    /**
     * 0.일반가입1.카카오2.네이버3.구글
     */
    @Column(name = "user_grade", nullable = false)
    private char userGrade;

    @ColumnDefault("'ROLE_USER'") // default
    @Column(name="user_role")
    private String userRole;

    @Column(name="provider")
    private String provider;

    @Column(name="providerId")
    private String providerId;

    @Column(name = "user_state")
    private String userState;

    @PrePersist
    public void userRole(){
        this.userRole = this.userRole == null ? "ROLE_USER" : this.userRole;
    }
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Board> boards = new ArrayList<>();
}



