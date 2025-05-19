package com.old.silence.auth.center.infrastructure.persistence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.User;

import java.math.BigInteger;

@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("select * from sys_user where username = #{username} and status = #{status} ")
    User findByUsernameAndStatus(String username, Boolean status);

    @Update("update sys_user set password = #{password} where id = #{id}")
    int updatePasswordById(String password, BigInteger id);

}