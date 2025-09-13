package com.old.silence.auth.center.dto;


import com.old.silence.auth.center.enums.MenuType;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class MenuDto {
    /**
     * 菜单ID
     */
    private BigInteger id;

    /**
     * 父菜单ID
     */
    private BigInteger parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单类型：1-目录，2-菜单，3-按钮
     */
    private MenuType type;


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
    private Long sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Boolean status;

    private List<MenuDto> children;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<MenuDto> getChildren() {
        return children;
    }

    public void setChildren(List<MenuDto> children) {
        this.children = children;
    }
}