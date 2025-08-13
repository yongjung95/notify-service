package com.example.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        String issueDate,
        boolean opndYn
) {
}
