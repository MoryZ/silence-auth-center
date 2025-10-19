package com.old.silence.auth.center.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.enums.NoticeStatus;
import com.old.silence.data.commons.converter.QueryWrapperConverter;
import com.old.silence.auth.center.api.assembler.NoticeMapper;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.domain.repository.NoticeRepository;
import com.old.silence.auth.center.dto.NoticeCommand;
import com.old.silence.auth.center.dto.NoticeQuery;

import java.math.BigInteger;
import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class NoticeResource {
    
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    public NoticeResource(NoticeRepository noticeRepository, NoticeMapper noticeMapper) {
        this.noticeRepository = noticeRepository;
        this.noticeMapper = noticeMapper;
    }

    @GetMapping(value = "/notices", params = {"pageNo", "pageSize"})
    @PreAuthorize("@perm.hasAuthority('system:notice:page')")
    public Page<Notice> getNotices(Page<Notice> page, NoticeQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, Notice.class);
        return noticeRepository.query(page, queryWrapper);
    }

    @GetMapping(value = "/notices", params = {"!pageNo", "!pageSize", "status"})
    @PreAuthorize("@perm.hasAuthority('system:notice:list')")
    public List<Notice> getNotices(@RequestParam NoticeStatus status) {
        return noticeRepository.getNoticesByStatus(status);
    }

    @PostMapping("/notices")
    @PreAuthorize("@perm.hasAuthority('system:notice:add')")
    public void create(@RequestBody NoticeCommand noticeCommand) {
        var notice = noticeMapper.convert(noticeCommand);
        noticeRepository.create(notice);
    }

    @PutMapping("/notices/{id}")
    @PreAuthorize("@perm.hasAuthority('system:notice:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody NoticeCommand noticeCommand) {
        var notice = noticeMapper.convert(noticeCommand);
        notice.setId(id); //NO SONAR
        noticeRepository.update(notice);
    }

    @PutMapping("/notices/{id}/read")
    @PreAuthorize("@perm.hasAuthority('system:notice:read')")
    public void read(@PathVariable BigInteger id) {
        noticeRepository.markAsRead(id);
    }

    @PutMapping("/notices/readAll")
    @PreAuthorize("@perm.hasAuthority('system:notice:readAll')")
    public void markAllAsRead() {
        noticeRepository.markAllAsRead();
    }

    @DeleteMapping("/notices/clear")
    @PreAuthorize("@perm.hasAuthority('system:notice:clear')")
    public void clearAllNotices() {
        noticeRepository.clearAllNotices();
    }


    @DeleteMapping("/notices/{id}")
    @PreAuthorize("@perm.hasAuthority('system:notice:delete')")
    public void delete(@PathVariable BigInteger id) {
        noticeRepository.deleteById(id);
    }

} 