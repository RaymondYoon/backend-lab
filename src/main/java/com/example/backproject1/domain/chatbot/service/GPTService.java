package com.example.backproject1.domain.chatbot.service;

import com.example.backproject1.domain.chatbot.dto.GPTRequest;
import com.example.backproject1.domain.chatbot.dto.GPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.builder().build();

    public String askToGPT(String userMessage) {
        GPTRequest request = new GPTRequest(
                "gpt-4o",
                List.of(
                        new GPTRequest.Message("system", "너는 친절한 반려견 상담 챗봇이야."),
                        new GPTRequest.Message("user", userMessage)
                )
        );

        try {
            GPTResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GPTResponse.class)
                    .block();

            return response.getChoices().get(0).getMessage().getContent();
        } catch (WebClientResponseException.TooManyRequests e) {
            return "⚠️ 현재 요청이 많아요. 잠시 후 다시 시도해주세요.";
        } catch (WebClientResponseException e) {
            return "⚠️ GPT 응답 오류: " + e.getStatusCode();
        } catch (Exception e) {
            return "⚠️ GPT 호출 중 예기치 않은 오류가 발생했어요.";
        }
    }
}
