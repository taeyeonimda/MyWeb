package com.MyWeb.chat.service;

import com.MyWeb.chat.entity.Chat;
import com.MyWeb.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ArrayList<Chat> getChatReord(String roomNo) {
        return chatRepository.findByChatNo(roomNo);
    }

    @Transactional
    public Chat saveChat(Chat cr) {
        cr.setChatDate(LocalDateTime.now());
        return chatRepository.save(cr);
    }
}
