package com.example.backproject1.domain.payment.controller;

import com.example.backproject1.domain.payment.dto.PaymentRequestDTO;
import com.example.backproject1.domain.payment.dto.PaymentResponseDTO;
import com.example.backproject1.domain.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponseDTO> requestPayment(@RequestBody PaymentRequestDTO requestDTO) {
        return ResponseEntity.ok(kakaoPayService.requestPayment(requestDTO));
    }

    @PostMapping("/approve/{postId}/{userId}")
    public ResponseEntity<Void> approvePayment(@PathVariable Long postId, @PathVariable Long userId) {
        kakaoPayService.approvePayment(postId, userId);
        return ResponseEntity.ok().build();
    }
}
