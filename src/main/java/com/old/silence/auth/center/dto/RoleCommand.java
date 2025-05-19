package com.old.silence.auth.center.dto;


import java.math.BigInteger;

public class RoleCommand {


    /**
     * 主键ID
     */
    private BigInteger id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Boolean status;

} 