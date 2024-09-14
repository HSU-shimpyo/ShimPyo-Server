package com.hsu.shimpyoo.domain.medicine.web.dto;

import com.hsu.shimpyoo.domain.medicine.enums.MealTiming;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class MedicineTimeSettingDto {
    private LocalTime breakfast;
    private LocalTime lunch;
    private LocalTime dinner;
    private MealTiming mealTiming;
    private Integer intakeTiming;
}
