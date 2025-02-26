package com.example.backproject1.domain.user.dto;

import com.example.backproject1.domain.user.entity.User;
import com.example.backproject1.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private String email;
    private String password;
    private String nickname;
    private String birthdate;
    private Gender gender;
    private String phoneNumber;

    public User toEntity(String encodedPassword){
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .birthdate(birthdate)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .build();
    }

}
