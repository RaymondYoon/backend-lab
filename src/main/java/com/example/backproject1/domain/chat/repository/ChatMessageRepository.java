package com.example.backproject1.domain.chat.repository;

import com.example.backproject1.domain.chat.entity.ChatMessage;
import com.example.backproject1.domain.chat.entity.ChatRoom;
import com.example.backproject1.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    Optional<ChatMessage> findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    List<ChatMessage> findByChatRoomAndSenderNotAndIsReadFalse(ChatRoom room, User currentUser);

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User currentUser);

    void deleteAllByChatRoom(ChatRoom chatRoom);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender <> :user AND m.isRead = false")
    void markMessagesAsReadBulk(@Param("room") ChatRoom room, @Param("user") User user);

}
