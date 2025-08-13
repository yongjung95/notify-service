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
public class Member extends BaseTimeEntity {

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

    public static Member of(String id, String passwd, String nickname) {
        return Member.builder()
                .memberUUID(UUID.randomUUID().toString())
                .id(id)
                .passwd(passwd)
                .nickname(nickname)
                .isUse(true)
                .build();
    }

    public void changeFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
