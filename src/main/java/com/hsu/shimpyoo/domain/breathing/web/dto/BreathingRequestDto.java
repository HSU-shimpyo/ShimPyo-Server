package com.hsu.shimpyoo.domain.breathing.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BreathingRequestDto {
    private Float first;
    private Float second;
    private Float third;
}
