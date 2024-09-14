package com.hsu.shimpyoo.domain.medicine.web.dto;

import com.hsu.shimpyoo.domain.medicine.enums.MealTiming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class MedicineRequestDto {
    private MealTiming mealTiming;
    private Integer intakeTiming;
    private LocalTime breakfast;
    private LocalTime lunch;
    private LocalTime dinner;
}