package com.fnjz.front.controller.api.message;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/11/12 14:27
 * @Description:
 */

public class MessageVo {

    public Long getUnreadMessageNumber() {
        return unreadMessageNumber;
    }

    public void setUnreadMessageNumber(Long unreadMessageNumber) {
        this.unreadMessageNumber = unreadMessageNumber;
    }

    public List<Map<String, Object>> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Map<String, Object>> messageList) {
        this.messageList = messageList;
    }

    /**未读个数*/
    private Long unreadMessageNumber;

    /**消息列表*/
    private List<Map<String,Object>> messageList;


}
