package com.fnjz.front.service.api.message;

import com.fnjz.front.controller.api.message.MessageVo;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

public interface MessageServiceI extends CommonService{
    /**
     * 功能描述: 添加消息通知
     *
     * @param: 通知内容，触发消息人昵称，发送给用户集合
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/13 10:24
     */
    public Boolean addUserMessage(String messageContent, Integer creatId, List<Integer> noticeUserIdList,String type);
    /**
     * 功能描述: 根据用户id获取消息列表
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/13 18:19
     */
    public MessageVo getMessageList(String userId,int page,int rows);
    /**
     * 功能描述: 跟新消息状态
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/13 18:22
     */
    public Integer updateMessageStatus(String userinfoId, String messageId, String messageUpdateType);

}
