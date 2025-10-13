package com.old.silence.auth.center.enums;

import com.old.silence.core.enums.DescribedEnumValue;

/**
 * @author moryzang
 */
public enum CaptchaType implements DescribedEnumValue<Byte> {


    MATH(1,"数字"),

    CHAR(2,"字符串"),
    ;

    private final Byte value;
    private final String description;

    CaptchaType(int value, String description) {
        this.value = (byte) value;
        this.description = description;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }
}