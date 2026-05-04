package com.shk.personalProject.passwordManager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice // 모든 RestController에서 발생하는 예외를 여기서 다 잡음.
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException : @Valid 검증 실패 (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(MethodArgumentNotValidException e){
        Map<String, String> response = new HashMap<>();
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .findFirst().orElse("입력값이 올바르지 않습니다.");

        // 400 Bad Request 상태 코드와 함께 JSON 메시지를 보낸다.
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청", "message", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.badRequest().body(Map.of("error","잘못된 요청", "message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class) // 그 외 예상치 못한 모든 에러(500)를 처리
    public ResponseEntity<Map<String, String>> handleAllException(Exception e){
        Map<String, String> response = new HashMap<>();

        // 서버 콘솔에는 에러 정체(로그)를 아주 자세히 남김.
        log.error("알 수 없는 에러 발생. ", e);

        response.put("error", "서버 내부 오류");
        response.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
