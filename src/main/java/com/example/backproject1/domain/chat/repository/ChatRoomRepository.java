package com.example.backproject1.domain.chat.repository;


import com.example.backproject1.domain.chat.entity.ChatRoom;
import com.example.backproject1.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPostIdAndSenderAndReceiver(Long postId, User sender, User receiver);
    List<ChatRoom> findBySenderOrReceiver(User sender, User receiver);
    @Query("SELECT r FROM ChatRoom r JOIN FETCH r.sender JOIN FETCH r.receiver WHERE r.sender = :user OR r.receiver = :user")
    List<ChatRoom> findAllWithUsersByUser(@Param("user") User user);

}
