package com.hsu.shimpyoo.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDto {
    @NotNull(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotNull(message = "생년월일을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private Date birth;

    @NotNull(message = "최대호기량을 입력해주세요.")
    @Min(value = 0, message = "최대호기량은 0 이상 800 이하의 값이어야 합니다.")
    @Max(value = 800, message = "최대호기량은 0 이상 800 이하의 값이어야 합니다.")
    private Long pef;
}