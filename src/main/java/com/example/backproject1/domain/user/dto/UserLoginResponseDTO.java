package com.example.backproject1.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponseDTO {
    private String email;
    private String token;
}
