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

    private String keyword;

    @Column(name = "member_uuid")
    private String memberUUID;

    public static NewsManagement of(String keyword, String memberUUID) {
        return NewsManagement.builder()
                .keyword(keyword)
                .memberUUID(memberUUID)
                .build();
    }
}
