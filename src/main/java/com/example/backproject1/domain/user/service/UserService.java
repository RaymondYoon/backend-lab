package com.example.backproject1.domain.user.service;

import com.example.backproject1.domain.user.dto.UserRequestDTO;
import com.example.backproject1.domain.user.dto.UserResponseDTO;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserResponseDTO registerUser(UserRequestDTO requestDTO){
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        User newUser = requestDTO.toEntity(encodedPassword);

        User savedUser = userRepository.save(newUser);

        return new UserResponseDTO(savedUser);
    }

    public UserResponseDTO authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new UserResponseDTO(user);
    }

    public Long getUserIdByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }
}
