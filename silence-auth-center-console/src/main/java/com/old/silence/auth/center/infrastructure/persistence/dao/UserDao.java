package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.data.mybatis.projection.ProjectionRepository;

public interface UserDao extends ProjectionRepository<User, BigInteger> {

    @Select("SELECT id,username,nickname,password  FROM sys_user WHERE username = #{username} AND status = #{status}")
    User findByUsernameAndStatus(String username, Boolean status);


    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updatePasswordById(String password, BigInteger id);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatusById(Boolean status, BigInteger id);
}