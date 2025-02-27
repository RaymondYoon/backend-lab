package com.example.backproject1.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private Long userId;
    private Long postId;
    private int amount;
}
