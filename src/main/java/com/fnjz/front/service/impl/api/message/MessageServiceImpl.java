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
import java.util.List;
import java.util.Map;

@Service("messageService")
@Transactional
public class MessageServiceImpl extends CommonServiceImpl implements MessageServiceI {

    @Autowired
    private UserInfoRestServiceI userInfoRestService;

    /**
     * 测试环境
     */
    public static final String fnjzAppKey = "09ec5f2b4d173f59183d949b";
    public static final String fnjzMasterSecret = "72fb5f9d56c10b8448d713f7";

    /**
     * 正式环境
     */
    public static final String sdzjAppKey = "c5de250e0420d5dc327bc691";
    public static final String sdzjMasterSecret = "190f25c0272c577d5d3c6e1b";

    @Override
    public Boolean addUserMessage(String messageContent, Integer creatId, List<Integer> noticeUserIdList) {

        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        for (Integer userId : noticeUserIdList) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUserInfoId(userId);
            messageEntity.setContent(messageContent);
            messageEntity.setCreateBy(creatId);
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
        String messageListSql = "select m.id,m.create_date,m.create_by,m.user_info_id,m.content,m.status,IFNULL(u.nick_name,REPLACE(u.mobile, SUBSTR(mobile,4,4), '****')) name " +
                " from hbird_message  m ,hbird_user_info u WHERE m.user_info_id= " + userId +
                " AND  m.create_by = u.id " +
                " order by m.status desc,m.create_date desc";

        List<Map<String, Object>> messageList = this.findForJdbc(messageListSql, page, rows);

        //创建者姓名修改，替换为最新
        for (Map<String, Object> stringObjectMap : messageList) {
            String content = String.valueOf(stringObjectMap.get("content"));
            if (content.contains("{") && content.contains("}")) {
                StringBuilder contentb = new StringBuilder(content);
                int start = content.indexOf("{");
                int end = content.indexOf("}");
                contentb.replace(start + 1, end, String.valueOf(stringObjectMap.get("name")));
                stringObjectMap.put("name", contentb.toString());
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
            updateSql = "update hbird_message set status = 1 where user_info_id = " + userinfoId;
        } else if (messageUpdateType.equals("ONE")) {
            Assert.notNull(messageId, "messageId must not be null");
            updateSql = "update hbird_message set status = 1 where id  = " + userinfoId;
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

        /*String sql = "select IFNULL(u.nick_name,REPLACE(u.mobile, SUBSTR(mobile,4,4), '****')) name  from hbird_user_info u  where id = "+creatId;

        List<Map<String, Object>> forJdbc = userInfoRestService.findForJdbc(sql);*/

        String noticeUserIds = "";
        for (Integer noticeUserId : noticeUserIdList) {
            noticeUserIds += String.valueOf(ShareCodeUtil.id2sharecode(noticeUserId)) + ",";
        }
        if (messageContent.length() > 20) {
            messageContent = messageContent.substring(0, 20);
        }
        //蜂鸟记账主体下推送
        String jumpPage = "fftz";
        JgPushExampls.sendPushObject_android_and_ios(fnjzAppKey, fnjzMasterSecret,noticeUserIds.substring(0, noticeUserIds.length() - 1),messageContent,jumpPage);
        //速贷之家主体下推送
        //JgPushExampls.sendPushObject_android_and_ios(messageContent,noticeUserIds.substring(0,noticeUserIds.length() - 1),fnjzAppKey,fnjzMasterSecret,jumpPage);
    }
}