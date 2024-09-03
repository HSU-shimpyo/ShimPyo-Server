package com.hsu.shimpyoo.global.exception;

import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;


import java.util.stream.Collectors;

@ControllerAdvice //전역적으로 예외처리를 할 것
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        // 로그로 BindingResult의 모든 오류를 출력
        e.getBindingResult().getAllErrors().forEach(error -> {
            System.out.println("Object Name: " + error.getObjectName());
            System.out.println("Default Message: " + error.getDefaultMessage());
        });

        // 각 필드의 검증 오류 메시지를 수집하여 하나의 문자열로 합침
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        System.out.println("Generated Error Message: " + errorMessage);

        // 클라이언트에 반환할 응답 생성
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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

    // RequestPart 유효성 검증 응답
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<CustomAPIResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestPartException e){
        String errorMessage = "Required part : '" + e.getRequestPartName() + "'는 필수 값입니다.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomAPIResponse.createFailWithout(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }
}
