package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.Role;

@Mapper
public interface RoleDao extends BaseMapper<Role> {
    @Select("SELECT * FROM sys_role WHERE status = #{status}")
    List<Role> findByStatus(boolean status);

    @Select("SELECT count(*) FROM sys_role WHERE code = #{code}")
    boolean existsByCode(String code);

    @Select("SELECT * FROM sys_role WHERE id IN (SELECT role_id FROM sys_user_role WHERE user_id = #{userId})")
    Set<Role> findRoleByUserId(BigInteger userId);
}