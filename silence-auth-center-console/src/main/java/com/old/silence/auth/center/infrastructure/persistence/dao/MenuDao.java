package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.Menu;

@Mapper
public interface MenuDao extends BaseMapper<Menu> {

    @Select("select 1 from t_menu where parent_id = #{parentId} and is_deleted = #{deleted}")
    boolean existsByParentIdAndDeleted(BigInteger parentId, boolean deleted);
}