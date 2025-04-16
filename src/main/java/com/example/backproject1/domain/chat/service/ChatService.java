package com.example.backproject1.domain.chat.service;

import com.example.backproject1.domain.board.entity.Post;
import com.example.backproject1.domain.board.repository.PostRepository;
import com.example.backproject1.domain.chat.entity.ChatMessage;
import com.example.backproject1.domain.chat.entity.ChatRoom;
import com.example.backproject1.domain.chat.repository.ChatMessageRepository;
import com.example.backproject1.domain.chat.repository.ChatRoomRepository;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createOrGetChatRoom(Long postId, Long currentUserId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        User receiver = post.getUser();

        return chatRoomRepository.findByPostIdAndSenderAndReceiver(postId, sender, receiver)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                                .postId(postId)
                                .sender(sender)
                                .receiver(receiver)
                                 .build()));
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    }

    public List<ChatRoom> getChatRoomsByUser(User user) {
        return chatRoomRepository.findAllWithUsersByUser(user);
    }


    @Transactional
    public void markMessagesAsRead(ChatRoom room, User currentUser) {
        chatMessageRepository.markMessagesAsReadBulk(room, currentUser);
    }


    @Transactional
    public void leaveChatRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        room.markDeleted(user);

        if (room.isDeletedBySender() && room.isDeletedByReceiver()) {
            chatMessageRepository.deleteAllByChatRoom(room);
            chatRoomRepository.delete(room);
        }
    }


}
