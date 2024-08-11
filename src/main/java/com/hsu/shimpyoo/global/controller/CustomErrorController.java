package com.hsu.shimpyoo.global.controller;

import jakarta.servlet.RequestDispatcher;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //스프링 빈으로 등록
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<CustomAPIResponse<?>> handleError(HttpServletRequest request) {
        // RequestDispatcher? : 클라이언트로부터 요청을 받고 이를 다른 리소스(서블릿, html, jsp)로 넘겨주는 역할을 하는 인터페이스
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == 400) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new CustomAPIResponse<>(HttpStatus.BAD_REQUEST.value(), false, null, "잘못된 요청입니다,"));
            }
            else if(statusCode == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new CustomAPIResponse<>(HttpStatus.FORBIDDEN.value(), false, null, "접근이 금지되었습니다."));
            }
            else if(statusCode == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CustomAPIResponse<>(HttpStatus.NOT_FOUND.value(), false, null, "요청 경로를 찾을 수 없습니다."));
            }
            else if(statusCode == 405) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(new CustomAPIResponse<>(HttpStatus.METHOD_NOT_ALLOWED.value(), false, null, "허용되지 않는 메소드입니다."));
            }
            else if(statusCode == 500) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new CustomAPIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null, "서버 에러가 발생하였습니다."));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomAPIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null, "알 수 없는 에러"));
    }
}
