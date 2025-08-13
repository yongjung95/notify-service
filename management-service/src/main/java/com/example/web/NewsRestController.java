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

    @GetMapping("/news/{id}")
    public ResponseEntity<?> getNews(@PathVariable Long id) {
        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        newsDataService.findNewsDataById(id)));
    }

    @GetMapping("/news")
    public ResponseEntity<?> news(@RequestHeader("memberUUID") String memberUUID) {
        return ResponseEntity.ok(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "success",
                        newsManagementService.findNewsManagementByMemberUUID(memberUUID)));
    }
}
