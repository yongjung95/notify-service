package com.example.web;

import com.example.dto.CommonResultRecord;
import com.example.dto.RequestRecord;
import com.example.service.NewsDataService;
import com.example.service.NewsManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class NewsRestController {

    private final NewsManagementService newsManagementService;
    private final NewsDataService newsDataService;

    @PostMapping("/news/management")
    public ResponseEntity<?> newsManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @RequestBody @Valid RequestRecord.NewsManagementReqeustRecord newsManagementReqeustRecord) {
        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        newsManagementService.save(newsManagementReqeustRecord.keyword(), memberUUID)));
    }

    @DeleteMapping("/news/management/{keyword}")
    public ResponseEntity<?> newsManagement(
            @RequestHeader("memberUUID") String memberUUID,
            @PathVariable String keyword) {
        newsManagementService.delete(keyword, memberUUID);

        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success", null));
    }

    @GetMapping("/news/management")
    public ResponseEntity<?> newsManagement(@RequestHeader("memberUUID") String memberUUID) {
        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        newsManagementService.findNewsManagementByMemberUUID(memberUUID)));
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<?> getNews(@PathVariable Long id) {
        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        newsDataService.findNewsDataById(id)));
    }

    @GetMapping("/news")
    public Mono<ResponseEntity<CommonResultRecord<?>>> news(@RequestParam String keyword) {
        return newsDataService.findNewsDataByKeywordAndToday(keyword, LocalDate.now())
                .map(newsDataRecord ->
                        // 성공 응답일 때 CommonResultRecord를 먼저 생성
                        CommonResultRecord.successResult(HttpStatus.OK.value(), "success", newsDataRecord)
                )
                .switchIfEmpty(
                        // 데이터가 없을 때도 CommonResultRecord를 생성
                        Mono.just(CommonResultRecord.failResult(HttpStatus.NOT_FOUND.value(), "요청하신 뉴스 데이터가 없습니다."))
                )
                .onErrorResume(e -> {
                    // 에러가 났을 때도 CommonResultRecord를 생성
                    System.out.println("e.getMessage() = " + e.getMessage());
                    return Mono.just(CommonResultRecord.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
                })
                .map(resultRecord -> ResponseEntity.status(resultRecord.code()).body(resultRecord));
    }
}
