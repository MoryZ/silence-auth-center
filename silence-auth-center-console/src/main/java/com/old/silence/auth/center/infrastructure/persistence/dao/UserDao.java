package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.User;

@Mapper
public interface UserDao extends BaseMapper<User> {


    @Update("update sys_user set password = #{password} where id = #{id}")
    int updatePasswordById(String password, BigInteger id);

}