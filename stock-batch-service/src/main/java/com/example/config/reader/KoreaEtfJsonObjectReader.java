package com.example.config.reader;

import com.example.dto.KoreaEtfDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class KoreaEtfJsonObjectReader implements JsonObjectReader<KoreaEtfDTO> {

    private ObjectMapper objectMapper;
    private Iterator<KoreaEtfDTO> dataIterator;

    public KoreaEtfJsonObjectReader() {
        this.objectMapper = new ObjectMapper();
        // JSON에 DTO에 정의되지 않은 필드가 있어도 에러를 발생시키지 않도록 설정
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void open(Resource resource) throws Exception {
        MyJsonDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), MyJsonDataWrapper.class);

        if (wrapper != null && wrapper.getOutBlock1() != null) {
            this.dataIterator = wrapper.getOutBlock1().iterator();
        } else {
            this.dataIterator = Collections.emptyIterator(); // 데이터가 없으면 빈 Iterator 할당
        }
    }

    @Override
    public KoreaEtfDTO read() throws Exception {
        if (dataIterator != null && dataIterator.hasNext()) {
            return dataIterator.next();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        // 리소스 정리 (필요시)
        this.dataIterator = null;
    }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드는 무시하도록 설정
    public static class MyJsonDataWrapper {
        @JsonProperty("OutBlock_1")
        private List<KoreaEtfDTO> outBlock1;

    }
}
