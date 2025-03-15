package com.example.backproject1.domain.payment.controller;

import com.example.backproject1.domain.jwt.JwtTokenProvider;
import com.example.backproject1.domain.payment.dto.PaymentRequestDTO;
import com.example.backproject1.domain.payment.dto.PaymentResponseDTO;
import com.example.backproject1.domain.payment.entity.Payment;
import com.example.backproject1.domain.payment.service.KakaoPayService;
import com.example.backproject1.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final KakaoPayService kakaoPayService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/request/{postId}")
    public ResponseEntity<PaymentResponseDTO> requestPayment(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {

        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);

        PaymentResponseDTO responseDTO = kakaoPayService.requestPayment(postId, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/approve/{postId}")
    public ResponseEntity<Void> approvePayment(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {

        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);

        kakaoPayService.approvePayment(postId, userId);
        return ResponseEntity.ok().build();
    }
}

