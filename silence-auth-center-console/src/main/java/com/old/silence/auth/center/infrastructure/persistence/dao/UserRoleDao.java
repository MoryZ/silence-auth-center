package com.old.silence.auth.center.infrastructure.persistence.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.UserRole;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface UserRoleDao extends BaseMapper<UserRole> {
    @Select("select * from sys_user_role where user_id = #{userId}")
    List<UserRole> findByUserId(BigInteger userId);

    int insertBatchSomeColumn(List<UserRole> userRoles);

    @Delete("delete from sys_user_role where user_id = #{userId}")
    int deleteByUserId(BigInteger userId);

    @Select({
            "<script>",
            "SELECT * FROM sys_user_role t where user_id in",
            "<foreach collection='userIds' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<UserRole> findByUserIdIn(List<BigInteger> userIds);
}