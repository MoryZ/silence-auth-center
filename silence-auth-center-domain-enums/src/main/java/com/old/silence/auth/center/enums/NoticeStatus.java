package com.old.silence.auth.center.enums;

import com.old.silence.core.enums.DescribedEnumValue;

/**
 * @author moryzang
 */
public enum NoticeStatus implements DescribedEnumValue<Byte> {

    UN_READ(0, "未读"),
    READ(1, "已读"),

    ;

    private final Byte value;
    private final String description;

    NoticeStatus(int value, String description) {
        this.value = (byte) value;
        this.description = description;
    }

    public Byte getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
