package com.old.silence.auth.center.infrastructure.persistence;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.domain.repository.NoticeRepository;
import com.old.silence.auth.center.enums.NoticeStatus;
import com.old.silence.auth.center.infrastructure.persistence.dao.NoticeDao;


/**
 * @author moryzang
 */
@Repository
public class NoticeMyBatisRepository implements NoticeRepository {

    private final NoticeDao noticeDao;

    public NoticeMyBatisRepository(NoticeDao noticeDao) {
        this.noticeDao = noticeDao;
    }

    @Override
    public Page<Notice> query(Page<Notice> page, QueryWrapper<Notice> queryWrapper) {
        return noticeDao.selectPage(page, queryWrapper);
    }

    @Override
    public int create(Notice notice) {
        return noticeDao.insert(notice);
    }

    @Override
    public int update(Notice notice) {
        return noticeDao.updateById(notice);
    }

    @Override
    public int deleteById(BigInteger id) {
        return noticeDao.deleteById(id);
    }


    @Override
    public List<Notice> getNoticesByStatus(NoticeStatus status) {
        var queryWrapper = new QueryWrapper<Notice>();
        queryWrapper.eq("status", status);
        return noticeDao.selectList(queryWrapper);
    }

    @Override
    public int markAsRead(BigInteger noticeId) {
        return noticeDao.updateStatus(NoticeStatus.READ ,noticeId);
    }

    @Override
    public void markAllAsRead() {
        noticeDao.updateAllStatus(NoticeStatus.READ, null);
    }

    @Override
    public void clearAllNotices() {
        noticeDao.deleteAll( null);
    }

}
