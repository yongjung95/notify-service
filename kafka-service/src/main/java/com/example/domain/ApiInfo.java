package com.example.domain;

import jakarta.persistence.*;
import lombok.*;


@Table
@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 400)
    private String token;

    private String issueDate;

    private Boolean opndYn;


    public static ApiInfo forTest(String token, String issueDate, Boolean opndYn) {
        return ApiInfo.builder()
                .token(token)
                .issueDate(issueDate)
                .opndYn(opndYn)
                .build();
    }
}
