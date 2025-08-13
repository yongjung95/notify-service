package com.example.dto;

import jakarta.validation.constraints.NotBlank;

public class RequestRecord {

    public record MembersRequestRecord(
            @NotBlank(message = "아이디를 입력해주세요.")
            String id,
            @NotBlank(message = "패스워드를 입력해주세요.")
            String passwd,
            @NotBlank(message = "닉네임을 입력해주세요.")
            String nickname
    ) {
    }

    public record LoginRequestRecord(
            @NotBlank(message = "아이디를 입력해주세요.")
            String id,
            @NotBlank(message = "패스워드를 입력해주세요.")
            String passwd
    ) {
    }

    public record MembersFcmTokenRequestRecord(
            String jwtToken,
            String fcmToken
    ){
    }
}
