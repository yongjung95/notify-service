package com.example.dto;

import com.example.domain.Member;
import lombok.Builder;

@Builder
public record MemberRecord(
        String memberUUID,
        String id,
        String fcmToken,
        String nickname,
        boolean isUse
) {
    public static MemberRecord fromMember(Member member) {
        return MemberRecord.builder()
                .memberUUID(member.getMemberUUID())
                .id(member.getId())
                .fcmToken(member.getFcmToken())
                .nickname(member.getNickname())
                .isUse(member.getIsUse())
                .build();
    }
}
