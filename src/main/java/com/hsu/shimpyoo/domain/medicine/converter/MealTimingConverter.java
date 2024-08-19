package com.hsu.shimpyoo.domain.medicine.converter;

import com.hsu.shimpyoo.domain.medicine.entity.MealTiming;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MealTimingConverter implements AttributeConverter<MealTiming, String> {

    @Override
    public String convertToDatabaseColumn(MealTiming mealTiming) {
        if (mealTiming == null) {
            return null;
        }
        return mealTiming.getDescription();
    }

    @Override
    public MealTiming convertToEntityAttribute(String description) {
        if (description == null) {
            return null;
        }

        for (MealTiming timing : MealTiming.values()) {
            if (timing.getDescription().equals(description)) {
                return timing;
            }
        }

        throw new IllegalArgumentException("잘못된 값입니다: " + description);
    }
}
