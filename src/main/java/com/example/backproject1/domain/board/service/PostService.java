package com.example.backproject1.domain.board.service;

import com.example.backproject1.domain.board.repository.PostRepository;
import com.example.backproject1.domain.board.dto.PostRequestDTO;
import com.example.backproject1.domain.board.dto.PostResponseDTO;
import com.example.backproject1.domain.board.entity.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<PostResponseDTO> getAllPost() {
        return postRepository.findAll().stream()
                .map(PostResponseDTO::new)
                .collect(Collectors.toList());
    }

    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        return new PostResponseDTO(post);
    }

    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        Post post = Post.builder()
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .build();
        Post savedPost = postRepository.save(post);
        return new PostResponseDTO(savedPost);
    }

    public PostResponseDTO updatePost(Long id, PostRequestDTO postRequestDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        post = Post.builder()
                .id(post.getId())
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        return new PostResponseDTO(postRepository.save(post));
    }

    public void deletePost(Long id){
        postRepository.deleteById(id);
    }
}
