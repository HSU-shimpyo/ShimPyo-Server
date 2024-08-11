package com.hsu.shimpyoo.global.user.controller;

import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import com.hsu.shimpyoo.global.user.dto.SignInReqDto;
import com.hsu.shimpyoo.global.user.dto.SignInResDto;
import com.hsu.shimpyoo.global.user.dto.SignUpDto;
import com.hsu.shimpyoo.global.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<CustomAPIResponse<Map<String, String>>> signUp(@RequestBody @Valid SignUpDto dto) {
        ResponseEntity<CustomAPIResponse<Map<String, String>>> result = userService.signUp(dto);
        return result;
    }


    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInReqDto dto) {
        SignInResDto result = userService.signIn(dto);
        return ResponseEntity.ok(result);
    }
}
