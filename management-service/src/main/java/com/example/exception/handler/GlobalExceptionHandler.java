package com.example.exception.handler;

import com.example.dto.CommonResultRecord;
import com.example.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResultRecord.failResult(HttpStatus.BAD_REQUEST.value(), fieldError.getDefaultMessage()));
        }

        List<ObjectError> globalErrors = e.getBindingResult().getGlobalErrors();
        for (ObjectError globalError : globalErrors) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResultRecord.failResult(HttpStatus.BAD_REQUEST.value(), globalError.getDefaultMessage()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResultRecord.failResult(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e, HttpServletResponse response) {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(CommonResultRecord.failResult(e.getErrorCode().getHttpStatus().value(), e.getMessage()));
    }

    // 선언되지 않은 모든 예외 처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleException(Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(CommonResultRecord.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
//    }
}
