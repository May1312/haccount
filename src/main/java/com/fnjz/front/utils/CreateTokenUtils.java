package com.fnjz.front.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.jwt.def.JwtConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token生成工具类
 * Created by yhang on 2018/6/1.
 */
@Component
public class CreateTokenUtils {

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private AccountBookRestDao accountBookRestDao;

    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;

    @Autowired
    private UserInviteRestDao userInviteRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    public String createToken(String code) {
        //使用sharcode作为源token
        String token = Jwts.builder().setId(code).setSubject(code).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, JwtConstants.JWT_SECRET).compact();
        return token;
    }

    /**
     * 登录/注册成功-->返回token-->设置缓存
     *
     * @return
     */
    public ResultBean loginSuccess(UserLoginRestEntity task, String shareCode) {
        Map<String, Object> map = new HashMap<>();
        String token = this.createToken(shareCode);
        map.put("X-AUTH-TOKEN", token);
        map.put("expire", RedisPrefix.USER_EXPIRE_TIME);
        //设置账本+用户缓存
        String user = JSON.toJSONString(task);
        redisTemplateUtils.cacheUserAndAccount(task.getUserInfoId(), user);

        //离线增加返回 userinfoid  accountbookid
        map.put("userInfoId", task.getUserInfoId() + "");
        //UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(task.getUserInfoId(), shareCode);
        //map.put("accountBookId", userAccountBookRestEntityCache.getAccountBookId() + "");
        return new ResultBean(ApiResultType.OK, map);
    }

    public ResultBean wxappletLoginSuccess(UserLoginRestEntity task, String code) {
        Map<String, Object> map = new HashMap<>();
        String token = this.createToken(code);
        map.put("token", token);
        map.put("expire", RedisPrefix.USER_EXPIRE_TIME);
        //设置账本+用户缓存
        String user = JSON.toJSONString(task);
        redisTemplateUtils.cacheUserAndAccount(task.getUserInfoId(), user);
        return new ResultBean(ApiResultType.OK, map);
    }

    /**
     * 小程序01034情况下  返回key
     *
     * @param sessionKey
     * @return
     */
    public ResultBean returnKeyToWXApplet(String sessionKey) {
        String sessionKeyPrefix = CommonUtils.getSessionKeyPrefix();
        redisTemplateUtils.cacheSessionKey(sessionKeyPrefix, sessionKey);
        Map<String, String> map = new HashMap();
        map.put("key", sessionKeyPrefix);
        return new ResultBean(ApiResultType.UNIONID_IS_NULL, map);
    }

    /**
     * 根据行为类别/获取方式  判断是否已领取
     *
     * @param acquisitionModeEnum
     * @param userInfoId
     * @return
     */
    public boolean checkTaskComplete(CategoryOfBehaviorEnum categoryOfBehaviorEnum, AcquisitionModeEnum acquisitionModeEnum, String userInfoId, String shareCode) {
        //邀请好友可以多次触发
        if (acquisitionModeEnum.equals(AcquisitionModeEnum.Inviting_friends)) {
            //缓存邀请好友人数
            redisTemplateUtils.incrementForHash(RedisPrefix.USER_INVITE_COUNT + shareCode, "inviteCount", 1);
            return false;
        }
        int count = userIntegralRestDao.checkTaskComplete(categoryOfBehaviorEnum.getIndex(), acquisitionModeEnum.getIndex(), userInfoId);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取行为方式对应的积分数
     *
     * @param acquisitionModeEnum
     * @return
     */
    public FengFengTicketRestEntity getFengFengTicket(AcquisitionModeEnum acquisitionModeEnum) {
        return fengFengTicketRestDao.getFengFengTicket(null, acquisitionModeEnum.getName(), null);
    }

    /**
     * 新增积分记录
     *
     * @param userInfoId
     * @param ff
     * @param acquisitionModeEnum
     */
    public void insertInIntegral(String userInfoId, FengFengTicketRestEntity ff, AcquisitionModeEnum acquisitionModeEnum, CategoryOfBehaviorEnum categoryOfBehaviorEnum) {
        userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), acquisitionModeEnum.getDescription(), acquisitionModeEnum.getIndex(), categoryOfBehaviorEnum.getIndex(),Double.parseDouble(ff.getBehaviorTicketValue()+""));
        userIntegralRestDao.updateForTotalIntegral(userInfoId, ff.getBehaviorTicketValue(),new BigDecimal(ff.getBehaviorTicketValue()+""));

    }

    /**
     * 引入新手任务
     *
     * @param categoryOfBehaviorEnum
     * @param acquisitionModeEnum
     */
    public void integralTask(String userInfoId, String shareCode, CategoryOfBehaviorEnum categoryOfBehaviorEnum, AcquisitionModeEnum acquisitionModeEnum) {
        boolean flag = this.checkTaskComplete(categoryOfBehaviorEnum, acquisitionModeEnum, userInfoId, shareCode);
        if (!flag) {
            //获取需要绑定的积分数
            FengFengTicketRestEntity fengFengTicketRestEntity = this.getFengFengTicket(acquisitionModeEnum);
            if (fengFengTicketRestEntity != null) {
                if (acquisitionModeEnum.equals(AcquisitionModeEnum.The_invitation_came_to_five)) {
                    //当日邀请达5人  判断人数
                    int countForInvitedUsers = userInviteRestDao.getCountForInvitedUsers(userInfoId);
                    if (countForInvitedUsers >= 5) {
                        this.insertInIntegral(userInfoId, fengFengTicketRestEntity, acquisitionModeEnum, categoryOfBehaviorEnum);
                        updateTaskStatus(shareCode,categoryOfBehaviorEnum,acquisitionModeEnum);
                    }
                } else if (acquisitionModeEnum.equals(AcquisitionModeEnum.The_bookkeeping_came_to_three)) {
                    //当日记账达3笔
                    int count = warterOrderRestDao.getCountForCurrentDay(userInfoId);
                    if (count >= 3) {
                        this.insertInIntegral(userInfoId, fengFengTicketRestEntity, acquisitionModeEnum, categoryOfBehaviorEnum);
                        updateTaskStatus(shareCode,categoryOfBehaviorEnum,acquisitionModeEnum);
                    }
                } else if (acquisitionModeEnum.equals(AcquisitionModeEnum.Become_hbird_user)) {
                    //成为蜂鸟记账用户
                    this.insertInIntegral(userInfoId, fengFengTicketRestEntity, acquisitionModeEnum, categoryOfBehaviorEnum);
                    //设置注册积分奖励弹框
                    redisTemplateUtils.cacheForString(RedisPrefix.USER_REGISTER_INTEGRAL + shareCode, fengFengTicketRestEntity.getBehaviorTicketValue()+"",RedisPrefix.USER_VALID_TIME);
                }else {
                    this.insertInIntegral(userInfoId, fengFengTicketRestEntity, acquisitionModeEnum, categoryOfBehaviorEnum);
                    updateTaskStatus(shareCode,categoryOfBehaviorEnum,acquisitionModeEnum);
                }
                //加入积分返利
                addIntegralByInvitedUser(userInfoId,fengFengTicketRestEntity,categoryOfBehaviorEnum);
            }
        }
    }

    /**
     * 更新账本最后同步时间
     *
     * @param abId
     */
    public void updateABtime(Integer abId) {
        accountBookRestDao.updateABtime(abId);
    }

    /**
     * 判断当前用户是否具有对传入abId权限
     *
     * @param abId
     */
    public boolean checkByABIdAndUserInfoId(Integer abId, String userInfoId) {
        Integer status = userAccountBookRestDao.checkByABIdAndUserInfoId(abId, userInfoId);
        return status == null ? false : true;
    }

    /**
     * 积分红利活动开始时间
     */
    private static String beginTime="2018-12-14";

    /**
     * 红利返点  20%
     */
    private static double percentage=0.2;

    /**
     * 受邀用户积分红利
     * @param userInfoId 用户id
     * @param ff 奖励积分数  返利20%
     */
    private void addIntegralByInvitedUser(String userInfoId, FengFengTicketRestEntity ff,CategoryOfBehaviorEnum categoryOfBehaviorEnum){
        //查看当前用户是否存在被邀请用户
        Map<String,Object> map = userInviteRestDao.getInvitedUserNickName(userInfoId,beginTime,1);
        if(map!=null){
            if(map.size()>0){
                BigDecimal bigDecimal = new BigDecimal(ff.getBehaviorTicketValue());
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(percentage));
                String desc = "["+(map.get("nickname")==null?"蜂鸟记账":map.get("nickname"))+"]";
                userIntegralRestDao.insertSignInIntegral(map.get("userinfoid")+"", ff.getId() + "", null, desc+AcquisitionModeEnum.BONUS.getDescription(), AcquisitionModeEnum.BONUS.getIndex(), categoryOfBehaviorEnum.getIndex(),multiply.doubleValue());
                userIntegralRestDao.updateForTotalIntegral(map.get("userinfoid")+"", ff.getBehaviorTicketValue(),new BigDecimal(ff.getBehaviorTicketValue()+""));
            }
        }
    }

    /**
     * 修改任务状态
     * @param shareCode
     * @param categoryOfBehaviorEnum
     */
    private void updateTaskStatus(String shareCode,CategoryOfBehaviorEnum categoryOfBehaviorEnum,AcquisitionModeEnum acquisitionModeEnum){
        String cacheTask;
        if (categoryOfBehaviorEnum.equals(CategoryOfBehaviorEnum.NewbieTask)) {
            cacheTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
            if(StringUtils.isNotEmpty(cacheTask)){
                JSONArray todayTask = JSONArray.parseArray(cacheTask);
                for(int i = 0;i<todayTask.size();i++){
                    JSONObject jsonObject = JSONObject.parseObject(todayTask.get(i)+"");
                    if(StringUtils.equals(acquisitionModeEnum.getForUser(),jsonObject.getString("name"))){
                        //重置状态--->已获取
                        jsonObject.put("status",2);
                        todayTask.add(i,jsonObject);
                        long expire = redisTemplateUtils.getExpire(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
                        redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode,todayTask.toJSONString(),expire,TimeUnit.SECONDS);
                        break;
                    }
                }
            }
        } else if (categoryOfBehaviorEnum.equals(CategoryOfBehaviorEnum.TodayTask)) {
            cacheTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
            if(StringUtils.isNotEmpty(cacheTask)){
                JSONArray todayTask = JSONArray.parseArray(cacheTask);
                for(int i = 0;i<todayTask.size();i++){
                    JSONObject jsonObject = JSONObject.parseObject(todayTask.get(i)+"");
                    if(StringUtils.equals(acquisitionModeEnum.getForUser(),jsonObject.getString("name"))){
                        //重置状态--->已获取
                        jsonObject.put("status",2);
                        todayTask.add(i,jsonObject);
                        long expire = redisTemplateUtils.getExpire(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
                        redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode,todayTask.toJSONString(),expire,TimeUnit.SECONDS);
                        break;
                    }
                }
            }
        }
    }
}
