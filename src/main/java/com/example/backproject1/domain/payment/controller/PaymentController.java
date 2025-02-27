package com.example.backproject1.domain.payment.controller;

import com.example.backproject1.domain.payment.dto.PaymentRequestDTO;
import com.example.backproject1.domain.payment.dto.PaymentResponseDTO;
import com.example.backproject1.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * ✅ 결제 요청 (사용자 ID와 게시글 ID 필요)
     */
    @PostMapping("/request")
    public ResponseEntity<PaymentResponseDTO> requestPayment(@RequestBody PaymentRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.requestPayment(requestDTO));
    }

    /**
     * ✅ 결제 승인 (카카오페이 결제 완료 후)
     */
    @PostMapping("/approve/{postId}/{userId}")
    public ResponseEntity<Void> approvePayment(@PathVariable Long postId, @PathVariable Long userId) {
        paymentService.approvePayment(postId, userId);
        return ResponseEntity.ok().build();
    }
}
