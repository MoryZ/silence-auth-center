package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.RoleMenu;

@Mapper
public interface RoleMenuDao extends BaseMapper<RoleMenu> {


    @Select("select * from sys_role_menu where role_id = #{roleId}")
    List<RoleMenu> findByRoleId(BigInteger roleId);

    @Select({
            "<script>",
            "SELECT * FROM sys_role_menu t where role_id in",
            "<foreach collection='roleIds' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<RoleMenu> findByRoleIdIn(List<BigInteger> roleIds);


    int insertBatchSomeColumn(List<RoleMenu> roleMenus);

    @Insert({
        "<script>",
        "INSERT INTO sys_role_menu (role_id, menu_id, created_date, created_by, is_deleted) VALUES",
        "<foreach collection='roleMenus' item='item' separator=','>",
        "(#{item.roleId}, #{item.menuId}, #{item.createdDate}, #{item.createdBy}, 0)",
        "</foreach>",
        "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertBatch(List<RoleMenu> roleMenus);

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    void deleteByRoleId(BigInteger roleId);

    @Delete("delete from sys_role_menu where menu_id = #{id}")
    void deleteByMenuId(BigInteger id);

}