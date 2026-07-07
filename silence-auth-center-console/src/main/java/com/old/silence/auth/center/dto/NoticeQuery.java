package com.old.silence.auth.center.dto;


import com.old.silence.auth.center.enums.NoticeStatus;
import com.old.silence.data.commons.annotation.RelationalQueryProperty;
import com.old.silence.data.commons.converter.Part;

/**
 * @author moryzang
 */
public class NoticeQuery {
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String title;

    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String content;

    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String senderName;

    @RelationalQueryProperty(type = Part.Type.SIMPLE_PROPERTY)
    private NoticeStatus status;

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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(NoticeStatus status) {
        this.status = status;
    }
}
