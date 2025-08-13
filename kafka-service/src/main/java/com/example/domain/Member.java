package com.example.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "MEMBER_UUID")
    private String memberUUID;

    @Column
    private String id;

    @Column
    private String passwd;

    @Column
    private String fcmToken;

    @Column
    private String nickname;

    @Column
    private Boolean isUse;

    public static Member forTest(String id, String passwd, String fcmToken, String nickname) {
        return Member.builder()
                .memberUUID(UUID.randomUUID().toString())
                .id(id)
                .passwd(passwd)
                .fcmToken(fcmToken)
                .nickname(nickname)
                .isUse(true)
                .build();
    }
}
