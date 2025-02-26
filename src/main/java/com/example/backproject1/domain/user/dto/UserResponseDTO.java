package com.example.backproject1.domain.user.dto;

import com.example.backproject1.domain.user.entity.Gender;
import com.example.backproject1.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private String nickname;
    private String birthdate;
    private Gender gender;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.birthdate = user.getBirthdate();
        this.gender = user.getGender();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
    }

}
