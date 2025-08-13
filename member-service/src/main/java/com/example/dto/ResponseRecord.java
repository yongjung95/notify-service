package com.example.dto;

import lombok.Builder;

public class ResponseRecord {

    @Builder
    public record MemberResponseRecord(
            String memberUUID,
            String id,
            String fcmToken,
            String nickname,
            boolean isUse
    ) {
        public static MemberResponseRecord fromMemberRecord(MemberRecord memberRecord) {
            return MemberResponseRecord.builder()
                    .memberUUID(memberRecord.memberUUID())
                    .id(memberRecord.id())
                    .fcmToken(memberRecord.fcmToken())
                    .nickname(memberRecord.nickname())
                    .isUse(memberRecord.isUse())
                    .build();
        }
    }

    @Builder
    public record LoginResponseRecord(
            String memberUUID,
            String token
    ) {
        public static LoginResponseRecord of(String memberUUID, String token) {
            return LoginResponseRecord.builder()
                    .memberUUID(memberUUID)
                    .token(token)
                    .build();
        }
    }
}
