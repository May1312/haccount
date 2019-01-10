package com.fnjz.front.service.impl.api.userinvite;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserInfoAddFieldRestDao;
import com.fnjz.front.dao.UserInfoRestDao;
import com.fnjz.front.dao.UserInviteRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.UserInviteRestDTO;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.api.userinvite.UserInviteRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.RedisLockUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.newWeChat.WXAppletPushUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by yhang on 2018/10/17.
 */
@Service("userInviteRestService")
@Transactional
public class UserInviteRestServiceImpl implements UserInviteRestServiceI {

    @Autowired
    private UserInviteRestDao userInviteRestDao;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    /**
     * 获取邀请人数
     * @param userInfoId
     * @return
     */
    @Override
    public int getCountForInvitedUsers(String userInfoId) {
        return userInviteRestDao.getCountForInvitedUsers(userInfoId);
    }

    /**
     * 邀请历史记录列表
     * @param userInfoId
     * @param curPage
     * @param pageSize
     * @return
     */
    @Override
    public PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<UserInviteRestDTO> listForPage = userInviteRestDao.listForPage(userInfoId,pageRest.getStartIndex(),pageRest.getPageSize());
        //获取总条数
        Integer  count = userInviteRestDao.getCountForInvitedUsers(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }

    @Autowired
    private RedisLockUtils redisLock;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;

    @Override
    public ResultBean insert(int userInfoId, int inviteUserInfoId) {
        //校验邀请码
        int i = userInfoRestDao.checkUserExists(userInfoId);
        if(i>0){
            redisLock.lock(inviteUserInfoId+"");
            int j = userInviteRestDao.checkExists(inviteUserInfoId);
            if(j<1){
                userInviteRestDao.insert(userInfoId, inviteUserInfoId);
                taskExecutor.execute(()->{
                    //引入当日任务---->邀请好友
                    createTokenUtils.integralTask(userInfoId + "", inviteUserInfoId+"", CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Inviting_friends);
                    //引入当日任务---->邀请达5人
                    createTokenUtils.integralTask(userInfoId + "", null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.The_invitation_came_to_five);
                    String openId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId + "");
                    if (StringUtils.isNotEmpty(openId)) {
                        //获取formId
                        Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                        if (keys.size() > 0) {
                            Object[] arrays = keys.toArray();
                            Arrays.sort(arrays, Collections.reverseOrder());
                            String formId = (String) redisTemplateUtils.popListRight(arrays[0] + "");
                            WXAppletMessageBean bean = new WXAppletMessageBean();
                            Map<String, Object> map = userInfoRestDao.getNKAndAUById(inviteUserInfoId);
                            //设置好友昵称
                            bean.getKeyword1().put("value", map.get("nickname") == null ? "蜂鸟用户" : map.get("nickname")+"");
                            //设置邀请时间
                            bean.getKeyword2().put("value", LocalDate.now().toString());
                            //设置获得奖励
                            FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(null, AcquisitionModeEnum.Inviting_friends.getName(), null);
                            if (fengFengTicket != null) {
                                bean.getKeyword3().put("value", fengFengTicket.getBehaviorTicketValue() == null ? "0" : fengFengTicket.getBehaviorTicketValue() + "积分（价值0.4元）");
                            }
                            //设置已邀请人数
                            int inviteUsers = userInviteRestDao.getCountForInvitedUsers(userInfoId + "");
                            bean.getKeyword4().put("value", inviteUsers + "人");
                            //温馨提示
                            bean.getKeyword5().put("value", "邀请好友赚现金，马上去提现！");
                            wxAppletPushUtils.wxappletPush(WXAppletPushUtils.inviteFriendId, openId, formId, WXAppletPushUtils.inviteFriendPage, bean);
                        }
                    }
                });
                redisLock.unlock(inviteUserInfoId+"");
                return new ResultBean(ApiResultType.OK,null);
            }else{
                redisLock.unlock(inviteUserInfoId+"");
                return new ResultBean(ApiResultType.HAD_BIND,null);
            }
        }else{
            return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
        }
    }

    @Override
    public Object listForPagev2(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<UserInviteRestDTO> listForPage = userInviteRestDao.listForPagev2(userInfoId,pageRest.getStartIndex(),pageRest.getPageSize());
        //获取总条数
        Integer  count = userInviteRestDao.getCountForInvitedUsers(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }
}
