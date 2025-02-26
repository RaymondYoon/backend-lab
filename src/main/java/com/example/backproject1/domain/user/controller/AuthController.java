package com.example.backproject1.domain.user.controller;

import com.example.backproject1.domain.jwt.JwtTokenProvider;
import com.example.backproject1.domain.user.dto.UserLoginRequestDTO;
import com.example.backproject1.domain.user.dto.UserLoginResponseDTO;
import com.example.backproject1.domain.user.dto.UserRequestDTO;
import com.example.backproject1.domain.user.dto.UserResponseDTO;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import com.example.backproject1.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(requestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO requestDTO){
        UserResponseDTO user = userService.authenticateUser(requestDTO.getEmail(), requestDTO.getPassword());

        String token = jwtTokenProvider.createToken(user.getEmail());

        return ResponseEntity.ok(new UserLoginResponseDTO(user.getEmail(), token));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");

        String email = jwtTokenProvider.getUserEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}
