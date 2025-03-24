package com.example.backproject1.domain.payment.service;

import com.example.backproject1.domain.board.entity.Post;
import com.example.backproject1.domain.board.repository.PostRepository;
import com.example.backproject1.domain.payment.dto.PaymentResponseDTO;
import com.example.backproject1.domain.payment.entity.Payment;
import com.example.backproject1.domain.payment.repository.PaymentRepository;
import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private final String KAKAO_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private final String KAKAO_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

    @Value("${kakao.pay.secret-key}")
    private String SECRET_KEY;


    public PaymentResponseDTO requestPayment(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", postId.toString());
        params.put("partner_user_id", userId.toString());
        params.put("item_name", "게시글 구독");
        params.put("quantity", 1);
        params.put("total_amount", 100);
        params.put("tax_free_amount", 0);
        params.put("approval_url", "http://localhost:3000/payment-success?postId=" + postId);
        params.put("cancel_url", "http://localhost:3000/payment-cancel");
        params.put("fail_url", "http://localhost:3000/payment-fail");

        HttpHeaders headers = getHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        try {
            log.info("[카카오페이 결제 요청] postId={}, userId={}", postId, userId);
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_READY_URL, HttpMethod.POST, requestEntity, Map.class);

            log.info("[카카오페이 응답] 상태 코드={}, 응답={}", response.getStatusCode(), response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("tid") || !responseBody.containsKey("next_redirect_pc_url")) {
                throw new RuntimeException("카카오페이 응답 형식이 올바르지 않습니다: " + responseBody);
            }

            return new PaymentResponseDTO(
                    (String) responseBody.get("tid"),
                    (String) responseBody.get("next_redirect_pc_url")
            );

        } catch (HttpClientErrorException e) {
            log.error("[카카오페이 결제 요청 실패] 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("카카오페이 결제 요청 중 오류 발생: " + e.getResponseBodyAsString());
        }
    }
    @Transactional
    public void approvePayment(Long postId, Long userId, String tid, String pgToken) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("tid", tid);
        params.put("partner_order_id", postId.toString());
        params.put("partner_user_id", userId.toString());
        params.put("pg_token", pgToken);

        HttpHeaders headers = getHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        try {
            log.info("[카카오페이 결제 승인 요청] postId={}, userId={}, tid={}, pgToken={}", postId, userId, tid, pgToken);
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_APPROVE_URL, HttpMethod.POST, requestEntity, Map.class);

            log.info("[카카오페이 승인 응답] 상태 코드={}, 응답={}", response.getStatusCode(), response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("approved_at")) {
                throw new RuntimeException("카카오페이 결제 승인 응답이 올바르지 않습니다: " + responseBody);
            }

            Payment payment = Payment.builder()
                    .user(user)
                    .post(post)
                    .amount(100)
                    .paymentStatus("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

        } catch (HttpClientErrorException e) {
            log.error("[카카오페이 결제 승인 실패] 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("카카오페이 결제 승인 중 오류 발생: " + e.getResponseBodyAsString());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


}
