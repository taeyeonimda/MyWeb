package com.MyWeb.chat.repository;

import com.MyWeb.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    ArrayList<Chat> findByChatNo(String roomNo);
}
