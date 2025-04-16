package com.example.backproject1.domain.chat.controller;

import com.example.backproject1.domain.chat.dto.ChatMessageDTO;
import com.example.backproject1.domain.chat.dto.ChatRoomDTO;
import com.example.backproject1.domain.chat.entity.ChatMessage;
import com.example.backproject1.domain.chat.entity.ChatRoom;
import com.example.backproject1.domain.chat.repository.ChatMessageRepository;
import com.example.backproject1.domain.chat.service.ChatService;
import com.example.backproject1.domain.jwt.JwtTokenProvider;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/room")
    public ResponseEntity<Long> createOrGetChatRoom(
            @RequestParam("postId") Long postId,
            @RequestHeader("Authorization") String token
    ){
        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long currentUserId = userService.getUserIdByEmail(email);

        ChatRoom room = chatService.createOrGetChatRoom(postId, currentUserId);
        return ResponseEntity.ok(room.getId());
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);
        User currentUser = userService.getUserById(userId);

        ChatRoom chatRoom = chatService.getChatRoomById(roomId);

        // 👉 여기에 추가
        chatService.markMessagesAsRead(chatRoom, currentUser);

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);

        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(msg -> {
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.setRoomId(roomId);
                    dto.setSenderNickname(msg.getSender().getNickname());
                    dto.setMessage(msg.getMessage());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(messageDTOs);
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms(@RequestHeader("Authorization") String token) {
        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);
        User currentUser = userService.getUserById(userId);

        List<ChatRoom> allRooms = chatService.getChatRoomsByUser(currentUser);

        List<ChatRoom> rooms = allRooms.stream()
                .filter(room -> {
                    if (room.getSender().getId().equals(currentUser.getId())) {
                        return !room.isDeletedBySender();
                    } else {
                        return !room.isDeletedByReceiver();
                    }
                })
                .toList();

        List<ChatRoomDTO> result = rooms.stream().map(room -> {
            String otherNickname = room.getSender().getId().equals(currentUser.getId())
                    ? room.getReceiver().getNickname()
                    : room.getSender().getNickname();

            String lastMsg = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(room)
                    .map(ChatMessage::getMessage)
                    .orElse("아직 메시지가 없습니다.");

            int unreadCount = chatMessageRepository
                    .countByChatRoomAndSenderNotAndIsReadFalse(room, currentUser);

            return ChatRoomDTO.builder()
                    .roomId(room.getId())
                    .otherNickname(otherNickname)
                    .lastMessage(lastMsg)
                    .unreadCount(unreadCount)
                    .build();
        }).toList();

        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);

        chatService.leaveChatRoom(roomId, userId);
        return ResponseEntity.noContent().build();
    }



}
