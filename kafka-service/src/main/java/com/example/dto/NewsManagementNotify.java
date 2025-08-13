package com.example.dto;

public record NewsManagementNotify(
        String keyword,
        String memberUUID,
        String nickname,
        String fcmToken
) {
}
