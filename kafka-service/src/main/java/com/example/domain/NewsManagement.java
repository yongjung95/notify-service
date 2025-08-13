package com.example.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsManagement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_uuid")
    private String memberUUID;

    private String keyword;

    public static NewsManagement of(String memberUUID, String keyword) {
        return NewsManagement.builder()
                .memberUUID(memberUUID)
                .keyword(keyword)
                .build();
    }
}
