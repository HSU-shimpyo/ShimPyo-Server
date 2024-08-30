package com.hsu.shimpyoo.global.exception;

import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


import java.util.stream.Collectors;

@ControllerAdvice //전역적으로 예외처리를 할 것
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        //에러로부터 에러메시지 가져오기
        String errorMessage= e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomAPIResponse.createFailWithout(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage=e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomAPIResponse.createFailWithout(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // ResponseStatusException을 사용하여 예외를 던질 때,
    // 예외가 발생하면 위에서 정의한 예외 처리기가 해당 예외를 처리하고, 일관된 오류 응답을 반환
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleResponseStatusException(ResponseStatusException e) {
        // ResponseStatusException에서 발생한 상태 코드와 메시지를 사용
        return ResponseEntity.status(e.getStatusCode())
                .body(CustomAPIResponse.createFailWithout(e.getStatusCode().value(), e.getReason()));
    }
}
