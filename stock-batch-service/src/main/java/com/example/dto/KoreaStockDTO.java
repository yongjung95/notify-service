package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoreaStockDTO {
    @JsonProperty("ISU_ABBRV")
    String corpName;
    @JsonProperty("ISU_SRT_CD")
    String stockCode;
}