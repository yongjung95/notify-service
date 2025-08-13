package com.example.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StocksManagement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stocksId;

    @Column(name = "member_uuid")
    private String memberUUID;

    public static StocksManagement of(Long stocksId, String memberUUID) {
        return StocksManagement.builder()
                .stocksId(stocksId)
                .memberUUID(memberUUID)
                .build();
    }
}
