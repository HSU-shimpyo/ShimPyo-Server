package com.hsu.shimpyoo.domain.medicine.dto;

import com.hsu.shimpyoo.domain.medicine.entity.MealTiming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class MedicineRequestDto {
    private Long userId;
    private MealTiming mealTiming;
    private Integer intakeTiming;
    private LocalTime breakfast;
    private LocalTime lunch;
    private LocalTime dinner;
}