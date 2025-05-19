package com.old.silence.auth.center.dto;


import com.old.silence.data.commons.annotation.RelationalQueryProperty;
import com.old.silence.data.commons.converter.Part;

public class RoleQuery {
    /**
     * 角色名称
     */
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String name;

    /**
     * 角色编码
     */
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String code;

    /**
     * 状态：0-禁用，1-启用
     */
    @RelationalQueryProperty(type = Part.Type.SIMPLE_PROPERTY)
    private Boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}