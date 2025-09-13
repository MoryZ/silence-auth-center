package com.old.silence.auth.center.enums;

import com.old.silence.core.enums.DescribedEnumValue;

/**
 * @author moryzang
 */
public enum MenuType implements DescribedEnumValue<Byte> {


    CONTENTS(1,"目录"),

    MENU(2,"菜单"),

    BUTTON(3,"按钮"),
    ;

    private final Byte value;
    private final String description;

    MenuType(int value, String description) {
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