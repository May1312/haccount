package com.fnjz.front.service.impl.api.message;
import com.fnjz.front.controller.api.message.MessageVo;
import com.fnjz.front.controller.api.push.JgPushExampls;
import com.fnjz.front.entity.api.message.MessageEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.ShareCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fnjz.front.service.api.message.MessageServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("messageService")
@Transactional
public class MessageServiceImpl extends CommonServiceImpl implements MessageServiceI {

    @Autowired
    private UserInfoRestServiceI userInfoRestService;


    @Override
    public Boolean addUserMessage(String messageContent, Integer creatId, List<Integer> noticeUserIdList) {

        ArrayList<MessageEntity> messageEntities = new ArrayList<>();

        for (Integer userId : noticeUserIdList) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUserInfoId(userId);
            messageEntity.setContent(messageContent);
            messageEntity.setCreateBy(creatId);
            messageEntity.setStatus(2);
            messageEntity.setCreateDate(new Date());
            messageEntities.add(messageEntity);
        }
        this.batchSave(messageEntities);

        //异步线程推送
        new Thread() {
            public void run() {
                sendPush(messageContent, creatId, noticeUserIdList);
            }
        }.start();

        return true;
    }

    @Override
    public MessageVo getMessageList(String userId, int page, int rows) {
        //未读条数
        String unreadMessageNumberSql = "select count(id) from hbird_message where status = 2 and  user_info_id = " + userId;
        Long count = this.getCountForJdbc(unreadMessageNumberSql);
        //消息列表
        String messageListSql ="select m.id,m.create_date,m.create_by,m.user_info_id,m.content,m.status,IFNULL(u.nick_name,REPLACE(u.mobile, SUBSTR(mobile,4,4), '****')) name   " +
                "from (SELECT * FROM hbird_message WHERE user_info_id = " + userId +")  m   " +
                "LEFT JOIN " +
                "hbird_user_info u  " +
                "ON  m.create_by = u.id  " +
                "order by m.status desc,m.create_date desc";

        List<Map<String, Object>> messageList = this.findForJdbc(messageListSql, page, rows);

        //创建者姓名修改，替换为最新
        for (Map<String, Object> stringObjectMap : messageList) {
            String content = String.valueOf(stringObjectMap.get("content"));
            if (content.contains("{") && content.contains("}")) {
                StringBuilder contentb = new StringBuilder(content);
                int start = content.indexOf("{");
                int end = content.indexOf("}");
                contentb.replace(start + 1, end, String.valueOf(stringObjectMap.get("name")));
                stringObjectMap.put("content", contentb.toString());
            }
        }
        //返回结果集
        MessageVo messageVo = new MessageVo();
        messageVo.setUnreadMessageNumber(count);
        messageVo.setMessageList(messageList);
        return messageVo;
    }

    @Override
    public Integer updateMessageStatus(String userinfoId, String messageId, String messageUpdateType) {
        String updateSql = "";
        if (messageUpdateType.equals("ALL")) {
            Assert.notNull(userinfoId, "userinfoId must not be null");
            updateSql = "update hbird_message set status = 1 ,update_date = current_timestamp() where user_info_id = " + userinfoId;
        } else if (messageUpdateType.equals("ONE")) {
            Assert.notNull(messageId, "messageId must not be null");
            updateSql = "update hbird_message set status = 1,update_date = current_timestamp()  where id  = " + messageId;
        } else {
            throw new IllegalArgumentException("没有约定的更新类型");
        }
        int i = this.updateBySqlString(updateSql);
        return i;
    }

    /**
     * 功能描述:通知消息录入成功之后发送推送
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/13 11:27
     */
    public void sendPush(String messageContent, Integer creatId, List<Integer> noticeUserIdList) {

        String jumpPage = "fftz";
        if (messageContent.length() > 20) {
            messageContent = messageContent.substring(0, 20);
        }
        for (Integer noticeUserId : noticeUserIdList) {
            String sharecode = ShareCodeUtil.id2sharecode(noticeUserId);
            //分环境
            JgPushExampls.sendPushObject_android_and_ios(sharecode,messageContent,jumpPage);
        }
    }
}