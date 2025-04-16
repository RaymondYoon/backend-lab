package com.example.backproject1.domain.chat.controller;

import com.example.backproject1.domain.chat.dto.ChatMessageDTO;
import com.example.backproject1.domain.chat.entity.ChatMessage;
import com.example.backproject1.domain.chat.entity.ChatRoom;
import com.example.backproject1.domain.chat.repository.ChatMessageRepository;
import com.example.backproject1.domain.chat.repository.ChatRoomRepository;
import com.example.backproject1.domain.chat.service.ChatMessageAsyncService;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageAsyncService chatMessageAsyncService;
    private final UserRepository userRepository;

    @MessageMapping("/chat/message")
    @SendTo("/topic/chat")
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDTO, org.springframework.messaging.Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String email = (String) accessor.getSessionAttributes().get("userEmail");

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        ChatRoom room = chatRoomRepository.findById(messageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        chatMessageAsyncService.saveMessageAsync(ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .message(messageDTO.getMessage())
                .build());


        messageDTO.setSenderNickname(sender.getNickname());
        return messageDTO;
    }
}
