package com.example.backproject1.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponseDTO {
   private String tid;
   private String next_redirect_pc_url;
}
