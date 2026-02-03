package com.old.silence.auth.center.domain.repository;

import java.math.BigInteger;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.enums.NoticeStatus;

public interface NoticeRepository {

    /**
     * 查询通知
     *
     * @param page         分页信息
     * @param queryWrapper 查询条件
     */
    Page<Notice> query(Page<Notice> page, QueryWrapper<Notice> queryWrapper);

    /**
     * 创建通知
     *
     * @param notice 配置环境信息
     */
    int create(Notice notice);

    /**
     * 更新配置环境
     *
     * @param notice 配置环境信息
     */
    int update(Notice notice);

    /**
     * 删除配置环境
     *
     * @param id 配置环境ID
     */
    int deleteById(BigInteger id);

    /**
     * 获取通知列表 根据状态
     *
     * @param status 状态
     */
    List<Notice> getNoticesByStatus(NoticeStatus status);

    /**
     * 标记通知为已读
     *
     * @param noticeId 通知ID
     */
    int markAsRead(BigInteger noticeId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead();

    /**
     * 清空所有通知
     */
    void clearAllNotices();


}