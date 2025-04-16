package com.example.backproject1.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private Long roomId;
    private String senderNickname;
    private String message;
}
