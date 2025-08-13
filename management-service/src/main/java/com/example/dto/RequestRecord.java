package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RequestRecord {

    public record StockRequestRecord(
            Integer page,
            Integer pageSize,
            String exchangeCountry,
            String searchText
    ) {
        public StockRequestRecord {
            if (page == null) {
                page = 0;
            }

            if (pageSize == null) {
                pageSize = 10;
            }

            if (searchText == null || searchText.isEmpty()) {
                searchText = "";
            }

            if (exchangeCountry == null || exchangeCountry.isEmpty()) {
                exchangeCountry = "KOREA";
            }
        }
    }

    public record StockManagementReqeustRecord(
            @NotBlank(message = "종목 코드를 입력해주세요.")
            String ticker
    ) {

    }

    public record StockPriceManagementReqeustRecord(
            @NotBlank(message = "종목 코드를 입력해주세요.")
            String ticker,
            @NotNull(message = "가격을 입력해주세요.")
            @Positive(message = "가격은 0 이하가 될 수 없습니다.")
            Double targetPrice
    ) {

    }

    public record NewsManagementReqeustRecord(
            @NotBlank(message = "키워드를 입력해주세요.")
            String keyword
    ) {

    }
}
