package com.old.silence.auth.center.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.Map;

import com.old.silence.auth.center.enums.MenuType;
import com.old.silence.auth.center.enums.ModuleType;

public class MenuCommand {


    /**
     * 父菜单ID
     */
    @NotNull
    private BigInteger parentId;

    /**
     * 菜单名称
     */
    @NotBlank
    private String name;

    /**
     * 菜单类型：1-目录，2-菜单，3-按钮
     */
    @NotNull
    private MenuType type;

    /**
     * 菜单所属模块：SYSTEM-系统，CONFIG-配置中心，MQ-消息队列，JOB-任务调度
     */
    @NotNull
    private ModuleType moduleType;


    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 跳转地址
     */
    private String redirect;

    /**
     * 元数据
     */
    private Map<String, Object> meta;

    /**
     * 显示顺序
     */
    @NotNull
    private Long sort;


    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

}