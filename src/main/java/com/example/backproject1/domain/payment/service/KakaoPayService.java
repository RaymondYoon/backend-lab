package com.example.backproject1.domain.payment.service;

import com.example.backproject1.domain.board.entity.Post;
import com.example.backproject1.domain.board.repository.PostRepository;
import com.example.backproject1.domain.payment.dto.PaymentRequestDTO;
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
    private final String KAKAO_ADMIN_KEY = "DEV5DBE71B40A9C510761820AC919EA7971D5611"; // 🔹 실제 API 키로 변경

    /**
     * ✅ 결제 요청 (구독료 = 100원 고정)
     */
    public PaymentResponseDTO requestPayment(PaymentRequestDTO requestDTO) {
        // ✅ 유저와 게시글 찾기
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Post post = postRepository.findById(requestDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // ✅ 카카오페이 요청 데이터 구성 (100원 고정)
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", requestDTO.getPostId());
        params.put("partner_user_id", requestDTO.getUserId());
        params.put("item_name", "게시글 구독");
        params.put("quantity", 1);
        params.put("total_amount", 100); // 🔥 100원으로 고정
        params.put("tax_free_amount", 0);
        params.put("approval_url", "http://localhost:8080/payment/success");
        params.put("cancel_url", "http://localhost:8080/payment/cancel");
        params.put("fail_url", "http://localhost:8080/payment/fail");

        // ✅ 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        // ✅ 카카오페이 API 호출
        ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_API_URL, HttpMethod.POST, requestEntity, Map.class);

        // ✅ 응답 데이터 추출
        Map<String, Object> responseBody = response.getBody();
        assert responseBody != null;
        String tid = (String) responseBody.get("tid");
        String nextRedirectPcUrl = (String) responseBody.get("next_redirect_pc_url");

        return new PaymentResponseDTO(tid, nextRedirectPcUrl);
    }

    /**
     * ✅ 결제 승인 처리
     */
    @Transactional
    public void approvePayment(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        // ✅ 게시글 결제 완료 처리
        post.markAsPaid();
        postRepository.save(post);

        // ✅ 결제 내역 저장 (100원으로 고정)
        Payment payment = Payment.builder()
                .user(user)
                .post(post)
                .amount(100) // 🔥 100원으로 고정
                .paymentStatus("SUCCESS")
                .build();

        paymentRepository.save(payment);
    }
}
