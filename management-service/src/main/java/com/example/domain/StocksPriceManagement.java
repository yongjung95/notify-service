package com.example.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StocksPriceManagement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stockId;

    private Double targetPrice;

    @Column(name = "member_uuid")
    private String memberUUID;

    @Enumerated(EnumType.STRING)
    private NotifyStatus notifyStatus;

    private LocalDateTime LastNotifiedDate;

    public static StocksPriceManagement of(Long stockId, Double targetPrice, String memberUUID) {
        return StocksPriceManagement.builder()
                .stockId(stockId)
                .targetPrice(targetPrice)
                .memberUUID(memberUUID)
                .notifyStatus(NotifyStatus.ACTIVE)
                .build();
    }
}
