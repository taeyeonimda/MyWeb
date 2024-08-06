package com.MyWeb.user.repository;

import com.MyWeb.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserEmail(String userEmail);

    Optional<Object> findByNickName(String nickName);

    @Query("SELECT u.profilePhoto FROM User u WHERE u.id = :id")
    String findProfilePhotoById(@Param("id") Long id);
}
