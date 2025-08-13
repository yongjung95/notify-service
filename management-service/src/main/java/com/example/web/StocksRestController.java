package com.example.web;

import com.example.dto.CommonResultRecord;
import com.example.dto.RequestRecord;
import com.example.dto.StocksRecord;
import com.example.service.StocksManagementService;
import com.example.service.StocksPriceManagementService;
import com.example.service.StocksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StocksRestController {

    private final StocksService stocksService;

    private final StocksManagementService stocksManagementService;

    private final StocksPriceManagementService stocksPriceManagementService;

    @GetMapping("/stocks")
    public ResponseEntity<?> getStocks(
            @RequestHeader("memberUUID") String memberUUID,
            RequestRecord.StockRequestRecord stockRequestRecord) {
        PageRequest pageRequest = PageRequest.of(stockRequestRecord.page(), stockRequestRecord.pageSize());
        Page<StocksRecord> result = stocksService.findStocksList(pageRequest, stockRequestRecord.exchangeCountry(),
                stockRequestRecord.searchText(), memberUUID);

        return ResponseEntity
                .ok(CommonResultRecord.successResult(HttpStatus.OK.value(), "success", result));
    }

    @PostMapping("/stocks/management")
    public ResponseEntity<?> stockManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @RequestBody @Valid RequestRecord.StockManagementReqeustRecord stockManagementReqeustRecord) {

        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        stocksManagementService.save(stockManagementReqeustRecord.ticker(), memberUUID)));
    }

    @DeleteMapping("/stocks/management/{ticker}")
    public ResponseEntity<?> stockManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @PathVariable String ticker) {
        stocksManagementService.delete(ticker, memberUUID);

        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success", null));
    }

    @PostMapping("/stocks/price/management")
    public ResponseEntity<?> stockPriceManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @RequestBody @Valid RequestRecord.StockPriceManagementReqeustRecord stockPriceManagementReqeustRecord) {

        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        stocksPriceManagementService.save(stockPriceManagementReqeustRecord.ticker(),
                                stockPriceManagementReqeustRecord.targetPrice(), memberUUID)));
    }

    @DeleteMapping("/stocks/price/management/{ticker}")
    public ResponseEntity<?> stockPriceManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @PathVariable String ticker) {
        stocksPriceManagementService.delete(ticker, memberUUID);

        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success", null));
    }
}
