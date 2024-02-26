package com.togather.partyroom.register;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DayListValidator implements ConstraintValidator<DayList, List<String>> {

    private static final List<String> dayStringList = new ArrayList<>();

    static {
        dayStringList.addAll(Arrays.asList(DayOfWeek.values()).stream().map(Enum::name).collect(Collectors.toList()));
    }
    @Override
    public boolean isValid(List<String> days, ConstraintValidatorContext constraintValidatorContext) {
        for (String day : days) {
            if (!dayStringList.contains(day)) {
                return false;
            }
        }
        return true;
    }
}
