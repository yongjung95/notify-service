package com.example.web;

import com.example.dto.CommonResultRecord;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request) {
        // 1. HTTP 상태 코드 가져오기
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // 2. 404 Not Found 에러 처리
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(CommonResultRecord.failResult(HttpStatus.NOT_FOUND.value(), "요청하신 리소스를 찾을 수 없습니다."));
            }
            // 3. 다른 에러 코드에 대한 처리 (예: 403 Forbidden)
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(CommonResultRecord.failResult(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."));
            }
        }

        // 4. 나머지 모든 에러에 대한 기본 응답
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResultRecord.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
    }
}