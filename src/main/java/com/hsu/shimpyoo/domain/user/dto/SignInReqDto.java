package com.hsu.shimpyoo.domain.user.dto;

import lombok.Getter;

@Getter
public class SignInReqDto {
    private String userId;
    private String password;
}