package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmericaEtfDTO {

    @JsonProperty("Code")
    private String symbol;

    @JsonProperty("Name")
    private String englishName;
}
