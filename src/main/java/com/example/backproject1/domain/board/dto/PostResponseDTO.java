package com.example.backproject1.domain.board.dto;

import com.example.backproject1.domain.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean paidByUser;

    public PostResponseDTO(Post post, boolean paidByUser) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getUser().getNickname();
        this.userId = post.getUser().getId();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.paidByUser = paidByUser;
    }
}
