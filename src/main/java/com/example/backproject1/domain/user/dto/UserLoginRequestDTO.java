package com.example.backproject1.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequestDTO {
    private String email;
    private String password;
}
