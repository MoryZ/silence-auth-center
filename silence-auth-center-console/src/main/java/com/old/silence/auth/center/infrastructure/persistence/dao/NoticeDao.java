package com.old.silence.auth.center.infrastructure.persistence.dao;


import java.math.BigInteger;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.enums.NoticeStatus;


@Mapper
public interface NoticeDao extends BaseMapper<Notice> {

    @Update("update sys_notice set status = #{noticeStatus} where id = #{id} and is_deleted = 0")
    int updateStatus(NoticeStatus noticeStatus, BigInteger id);

    @Update("update sys_notice set status = #{noticeStatus} where status = 0 and created_by = #{createdBy} and is_deleted = 0 ")
    int updateAllStatus(NoticeStatus noticeStatus, @Param("createdBy") String createdBy);

    @Update("update sys_notice set is_deleted = 1 where created_by = #{createdBy} and is_deleted = 0")
    void deleteAll(@Param("createdBy") String createdBy);
}