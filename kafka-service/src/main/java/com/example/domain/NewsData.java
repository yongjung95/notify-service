package com.example.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsData extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String data;

    public static NewsData of(String keyword, String data) {
        return NewsData.builder()
                .keyword(keyword)
                .data(data)
                .build();
    }
}
