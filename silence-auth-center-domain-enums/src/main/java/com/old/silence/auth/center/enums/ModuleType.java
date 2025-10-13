package com.old.silence.auth.center.enums;

import com.old.silence.core.enums.DescribedEnumValue;

/**
 * @author moryzang
 */
public enum ModuleType implements DescribedEnumValue<String> {

    SYSTEM("SYSTEM","系统模块"),
    CONFIG("CONFIG","配置中心CC"),
    MQ("MQ","消息队列SilenceMQ"),
    JOB("JOB","任务调度SilenceJob"),
    ;

    private final String value;
    private final String description;

    ModuleType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }
}