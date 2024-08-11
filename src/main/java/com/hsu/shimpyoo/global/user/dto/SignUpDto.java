package com.hsu.shimpyoo.global.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hsu.shimpyoo.global.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDto {
    @NotNull(message = "아이디를 적어주세요.")
    private String userId;
    @NotNull(message = "비밀번호를 적어주세요.")
    private String password;
    @NotNull(message = "생년월일을 적어주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private Date birth;
}