package com.example.dto;

import lombok.Builder;

@Builder
public record SendNotifyNewsTopic(
        String keyword,
        String memberUUID,
        String nickname,
        String fcmToken,
        Long newsDataId
) {
    public static SendNotifyNewsTopic of(String keyword, String memberUUID, String nickname, String fcmToken, Long newsDataId) {
        return SendNotifyNewsTopic.builder()
                .keyword(keyword)
                .memberUUID(memberUUID)
                .nickname(nickname)
                .fcmToken(fcmToken)
                .newsDataId(newsDataId)
                .build();
    }
}
