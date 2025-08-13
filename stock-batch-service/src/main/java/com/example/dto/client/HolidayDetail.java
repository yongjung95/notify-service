package com.example.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HolidayDetail(
        @JsonProperty("bass_dt") String bassDt,
        @JsonProperty("wday_dvsn_cd") String wdayDvsnCd,
        @JsonProperty("bzdy_yn") String bzdyYn,
        @JsonProperty("tr_day_yn") String trDayYn,
        @JsonProperty("opnd_yn") String opndYn,
        @JsonProperty("sttl_day_yn") String sttlDayYn
) {
}
