package com.example.config.reader;

import com.example.dto.AmericaStockDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AmericaStockJsonObjectReader implements JsonObjectReader<AmericaStockDTO> {

    private ObjectMapper objectMapper;
    private Iterator<AmericaStockDTO> dataIterator;

    public AmericaStockJsonObjectReader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void open(Resource resource) throws Exception {
        // JSON 전체를 읽어서 data.rows 배열 추출
        StockDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), StockDataWrapper.class);

        if (wrapper != null && wrapper.getData() != null && wrapper.getData().getRows() != null) {
            this.dataIterator = wrapper.getData().getRows().iterator();
        } else {
            this.dataIterator = Collections.emptyIterator();
        }
    }

    @Override
    public AmericaStockDTO read() throws Exception {
        if (dataIterator != null && dataIterator.hasNext()) {
            return dataIterator.next();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        // 필요시 리소스 정리
        this.dataIterator = null;
    }

    // Wrapper 클래스들
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockDataWrapper {
        private Data data;

        public Data getData() { return data; }
        public void setData(Data data) { this.data = data; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private List<AmericaStockDTO> rows;

        public List<AmericaStockDTO> getRows() { return rows; }
        public void setRows(List<AmericaStockDTO> rows) { this.rows = rows; }
    }
}
