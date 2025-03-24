package com.example.backproject1.domain.board.service;

import com.example.backproject1.domain.board.repository.PostRepository;
import com.example.backproject1.domain.board.dto.PostRequestDTO;
import com.example.backproject1.domain.board.dto.PostResponseDTO;
import com.example.backproject1.domain.board.entity.Post;
import com.example.backproject1.domain.payment.repository.PaymentRepository;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public List<PostResponseDTO> getAllPost() {
        return postRepository.findAllWithUser().stream()
                .map(PostResponseDTO::new)
                .collect(Collectors.toList());
    }

    public PostResponseDTO getPostById(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (Objects.equals(post.getUser().getId(), userId)){
            return new PostResponseDTO(post);
        }

        boolean hasPaid = paymentRepository.existsByPostIdAndUserIdAndPaymentStatus(id, userId, "SUCCESS");
       if(hasPaid){
           return new PostResponseDTO(post);
       }

        throw new IllegalArgumentException("결제 후 열람할 수 있는 게시글입니다.");
    }

    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        User user = userRepository.findById(postRequestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Post post = Post.builder()
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .user(user)
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
                .user(post.getUser())
                .createdAt(post.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return new PostResponseDTO(postRepository.save(post));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }


}
