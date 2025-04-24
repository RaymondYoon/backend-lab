package com.example.backproject1.domain.chatbot.controller;

import com.example.backproject1.domain.chatbot.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final GPTService gptService;

    @PostMapping(value = "/ask", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> askGPT(@RequestBody String question) {
        String reply = gptService.askToGPT(question);
        return ResponseEntity.ok(reply);
    }
}
