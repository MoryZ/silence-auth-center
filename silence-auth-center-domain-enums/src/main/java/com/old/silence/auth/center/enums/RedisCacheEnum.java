package com.old.silence.auth.center.enums;

import com.old.silence.core.enums.DescribedEnumValue;

/**
 * @author moryzang
 */
public enum RedisCacheEnum implements DescribedEnumValue<String> {

    PWD_ERR_CNT_KEY("password:error:retry:","密码错误可重试次数"),
    CAPTCHA_CODE_KEY("captcha_codes:", "验证码");
    private final String value;
    private final String description;

    RedisCacheEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getCacheKey(String suffix){

        return this.getValue() + suffix;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
