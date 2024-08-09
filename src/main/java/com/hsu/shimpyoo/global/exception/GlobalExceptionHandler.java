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
}
