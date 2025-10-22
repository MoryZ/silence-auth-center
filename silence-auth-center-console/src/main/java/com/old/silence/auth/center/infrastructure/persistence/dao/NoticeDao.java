package com.old.silence.auth.center.infrastructure.persistence.dao;


import java.math.BigInteger;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.enums.NoticeStatus;


@Mapper
public interface NoticeDao extends BaseMapper<Notice> {

    @Update("update notice set status = #{noticeStatus.value} where id = #{id} ")
    int updateStatus(NoticeStatus noticeStatus, BigInteger id);

    @Update("update notice set status = #{noticeStatus.value} where status = 1 and created_by = #{createdBy} ")
    int updateAllStatus(NoticeStatus noticeStatus, String createdBy);

    @Delete("delete from notice where created_by = #{createdBy} ")
    void deleteAll(String createdBy);
}