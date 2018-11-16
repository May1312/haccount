package com.fnjz.front.controller.api.push;

import java.io.Serializable;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/11/9 18:16
 * @Description:
 */
public class MessageNotificationEntity implements Serializable {

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private String messageType;

    private String messageContent;

    private Integer status;


}
