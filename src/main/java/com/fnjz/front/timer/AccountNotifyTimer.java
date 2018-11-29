package com.fnjz.front.timer;

import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletPushUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账单通知定时任务
 * Created by yhang on 2018/11/29.
 */
@Service("accountNotifyTimer")
public class AccountNotifyTimer implements Job {

    private static final Logger logger = Logger.getLogger(AccountNotifyTimer.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;

    public void accountNotify(){
            //获取formId
            /*Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH+ "*");
            if (keys.size() > 0) {
                Iterator<String> it = keys.iterator();
                String key = it.next();
                String formId = redisTemplateUtils.getForString(key);
                WXAppletMessageBean bean = new WXAppletMessageBean();
                //设置好友昵称
                bean.getKeyword1().put("value", userInfoRestEntity.getNickName() == null ? "蜂鸟用户" : userInfoRestEntity.getNickName());
                //设置邀请时间
                bean.getKeyword2().put("value", LocalDate.now().toString());
                //设置获得奖励
                FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(null, AcquisitionModeEnum.Inviting_friends.getName(), null);
                if(fengFengTicket!=null){
                    bean.getKeyword3().put("value", fengFengTicket.getBehaviorTicketValue()==null?"0":fengFengTicket.getBehaviorTicketValue() + "积分");
                }
                //设置已邀请人数
                int inviteUsers = userInviteRestDao.getCount(userInfoId + "");
                bean.getKeyword4().put("value", inviteUsers + "");
                //温馨提示
                bean.getKeyword5().put("value", "愿共同监督，知识永不枯竭。");
                wxAppletPushUtils.wxappletPush(WXAppletPushUtils.inviteFriendId, openId, formId, bean);
                //删除key
                redisTemplateUtils.deleteKey(key);
            }*/
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        accountNotify();
    }
}
