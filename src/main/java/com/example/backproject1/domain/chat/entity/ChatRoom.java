package com.example.backproject1.domain.chat.entity;

import com.example.backproject1.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    private boolean deletedBySender = false;

    @Column(nullable = false)
    private boolean deletedByReceiver = false;

    public void markDeleted(User user) {
        if (user.getId().equals(sender.getId())) {
            this.deletedBySender = true;
        } else if (user.getId().equals(receiver.getId())) {
            this.deletedByReceiver = true;
        }
    }


    public boolean match(Long postId, User sender, User receiver) {
        return this.postId.equals(postId)
                && this.sender.getId().equals(sender.getId())
                && this.receiver.getId().equals(receiver.getId());
    }
}
