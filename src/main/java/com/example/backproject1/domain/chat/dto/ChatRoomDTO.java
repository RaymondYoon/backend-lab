package com.example.backproject1.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long roomId;
    private String otherNickname;
    private String lastMessage;
    private int unreadCount;

}
