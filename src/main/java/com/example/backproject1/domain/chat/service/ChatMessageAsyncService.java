package com.example.backproject1.domain.chat.service;

import com.example.backproject1.domain.chat.entity.ChatMessage;
import com.example.backproject1.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageAsyncService {

    private final ChatMessageRepository chatMessageRepository;

    @Async
    public void saveMessageAsync(ChatMessage message) {
        chatMessageRepository.save(message);
    }
}
