package com.old.silence.auth.center.dto;



import com.old.silence.auth.center.enums.NoticeStatus;

import java.math.BigInteger;

public class NoticeCommand {



    /**
     * 发送者ID
     */
    private BigInteger senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知状态（0-未读，1-已读）
     */
    private NoticeStatus status;

    public BigInteger getSenderId() {
        return senderId;
    }

    public void setSenderId(BigInteger senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(NoticeStatus status) {
        this.status = status;
    }

} 