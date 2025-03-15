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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String KAKAO_API_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private final String SECRET_KEY = "DEV5DBE71B40A9C510761820AC919EA7971D5611"; // 환경 변수로 설정할 것

    public PaymentResponseDTO requestPayment(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", postId);
        params.put("partner_user_id", userId);
        params.put("item_name", "게시글 구독");
        params.put("quantity", 1);
        params.put("total_amount", 100); // 100원으로 고정
        params.put("tax_free_amount", 0);
        params.put("approval_url", "http://localhost:8080/payment/success");
        params.put("cancel_url", "http://localhost:8080/payment/cancel");
        params.put("fail_url", "http://localhost:8080/payment/fail");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_API_URL, HttpMethod.POST, requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("카카오페이 결제 요청 실패");
        }

        Map<String, Object> responseBody = response.getBody();
        String tid = (String) responseBody.get("tid");
        String nextRedirectPcUrl = (String) responseBody.get("next_redirect_pc_url");

        return new PaymentResponseDTO(tid, nextRedirectPcUrl);
    }

    @Transactional
    public void approvePayment(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        post.markAsPaid();
        postRepository.save(post);

        Payment payment = Payment.builder()
                .user(user)
                .post(post)
                .amount(100)
                .paymentStatus("SUCCESS")
                .build();

        paymentRepository.save(payment);
    }
}
