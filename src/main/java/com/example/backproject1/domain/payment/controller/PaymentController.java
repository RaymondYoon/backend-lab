package com.example.backproject1.domain.payment.controller;

import com.example.backproject1.domain.jwt.JwtTokenProvider;
import com.example.backproject1.domain.payment.dto.PaymentResponseDTO;
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

    // 결제 요청 (Ready API 호출)
    @PostMapping("/request/{postId}")
    public ResponseEntity<PaymentResponseDTO> requestPayment(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {

        String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
        Long userId = userService.getUserIdByEmail(email);

        PaymentResponseDTO responseDTO = kakaoPayService.requestPayment(postId, userId);
        return ResponseEntity.ok(responseDTO);
    }

    // 결제 승인 요청 (카카오페이 승인 API 호출)
    @PostMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam("pg_token") String pgToken,
            @RequestParam("postId") Long postId,
            @RequestParam("tid") String tid,
            @RequestHeader("Authorization") String token) {

        System.out.println("Received request to /payment/success");
        System.out.println("Received pg_token: " + pgToken);
        System.out.println("Received postId: " + postId);
        System.out.println("Received tid: " + tid);
        System.out.println("Received Authorization Token: " + token);

        try {
            String email = jwtTokenProvider.getUserEmail(token.replace("Bearer ", ""));
            Long userId = userService.getUserIdByEmail(email);

            System.out.println("User Email: " + email);
            System.out.println("User ID: " + userId);

            kakaoPayService.approvePayment(postId, userId, tid, pgToken);

            System.out.println("결제 승인 성공!");
            return ResponseEntity.ok("결제가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            System.out.println("결제 승인 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("결제 승인 중 오류 발생: " + e.getMessage());
        }
    }



    // 결제 실패
    @GetMapping("/fail")
    public ResponseEntity<String> paymentFail() {
        return ResponseEntity.badRequest().body("결제가 실패했습니다.");
    }

    // 결제 취소
    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.ok("결제가 취소되었습니다.");
    }
}
