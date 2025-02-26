package com.example.backproject1.domain.board.dto;

import com.example.backproject1.domain.board.entity.Post;
import com.example.backproject1.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {
    private String title;
    private String content;
    private Long userId;

    public Post toEntity(User user){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}
