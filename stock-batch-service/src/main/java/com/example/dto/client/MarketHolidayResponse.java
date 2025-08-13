package com.example.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MarketHolidayResponse(
        @JsonProperty("ctx_area_nk") String ctxAreaNk,
        @JsonProperty("ctx_area_fk") String ctxAreaFk,
        @JsonProperty("output") List<HolidayDetail> output
) {
}
