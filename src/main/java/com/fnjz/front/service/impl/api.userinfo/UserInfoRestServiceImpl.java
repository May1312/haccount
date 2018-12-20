package com.fnjz.front.service.impl.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.incometype.IncomeTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestDTO;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.registerchannel.RegisterChannelRestServiceI;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service("userInfoRestService")
@Transactional
public class UserInfoRestServiceImpl extends CommonServiceImpl implements UserInfoRestServiceI {

    @Autowired
    private UserInfoRestDao userInfoRestDao;
    @Autowired
    private UserLoginRestDao userLoginRestDao;
    @Autowired
    private AccountBookRestDao accountBookRestDao;
    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;
    @Autowired
    private CreateTokenUtils createTokenUtils;
    @Autowired
    private UserInviteRestDao userInviteRestDao;
    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;
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
    @Autowired
    private RegisterChannelRestServiceI registerChannelRestServiceI;

    @Override
    public int insert(UserInfoRestEntity userInfoRestEntity, String type) {
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //获取主键,insert-->user login 表
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //设置手机号
        if (StringUtil.isNotEmpty(userInfoRestEntity.getMobile())) {
            userLogin.setMobile(userInfoRestEntity.getMobile());
        }
        //设置密码
        if (StringUtil.isNotEmpty(userInfoRestEntity.getPassword())) {
            userLogin.setPassword(userInfoRestEntity.getPassword());
        }
        if (StringUtil.isNotEmpty(userInfoRestEntity.getWechatAuth())) {
            userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        }
        //设置用户详情表id
        userLogin.setUserInfoId(insertId);
        userLoginRestDao.insert(userLogin);
        //创建账本----->绑定用户id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        //设置账本类型
        //获取日常账本类型id
        int typeId = userPrivateLabelRestDao.getDefaultAccountBookTypeId();
        ab.setAccountBookTypeId(typeId);
        ab.setAbName("默认账本");
        int insertId2 = accountBookRestDao.insert(ab);
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        //设置是否为默认账本
        uabre.setDefaultFlag(1);
        int insert3 = userAccountBookRestDao.insert(uabre);
        //移动端注册  分配默认账本标签
        if (StringUtils.equals("android", type) || StringUtils.equals("ios", type)) {
            insertDefaultLabel(insertId, insertId2);
        }
        return insert3;
    }

    /**
     * 分配默认账本标签
     *
     * @param userInfoId
     * @param abId
     */
    private void insertDefaultLabel(int userInfoId, int abId) {
        List<IncomeTypeLabelIdRestDTO> incomeTypeRestDTOS = userPrivateLabelRestDao.listMarkLabelByDefaultForIncome();
        if (incomeTypeRestDTOS != null) {
            if (incomeTypeRestDTOS.size() > 0) {
                incomeTypeRestDTOS.forEach(v -> {
                    UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
                    //设置三级类目id
                    userPrivateLabelRestEntity.setTypeId(v.getId());
                    //设置三级类目名称
                    userPrivateLabelRestEntity.setTypeName(v.getIncomeName());
                    //设置二级类目id
                    userPrivateLabelRestEntity.setTypePid(v.getParentId());
                    //设置优先级
                    userPrivateLabelRestEntity.setPriority(v.getPriority());
                    //绑定用户
                    userPrivateLabelRestEntity.setUserInfoId(userInfoId);
                    //绑定账本id
                    userPrivateLabelRestEntity.setAccountBookId(abId);
                    //图标
                    userPrivateLabelRestEntity.setIcon(v.getIcon());
                    //设置属性  1:支出 2:收入
                    userPrivateLabelRestEntity.setProperty(2);
                    //设置属性  1:系统分配  2:用户自建
                    userPrivateLabelRestEntity.setType(1);
                    //设置属性  1:有效  0:失效
                    userPrivateLabelRestEntity.setStatus(1);
                    //
                    userPrivateLabelRestEntity.setAbTypeLabelId(v.getLabelId());
                    //insert
                    userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
                });

            }
        }

        List<SpendTypeLabelIdRestDTO> spendTypeRestDTOS = userPrivateLabelRestDao.listMarkLabelByDefaultForSpend();
        if (spendTypeRestDTOS != null) {
            if (spendTypeRestDTOS.size() > 0) {
                spendTypeRestDTOS.forEach(v -> {
                    UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
                    //设置三级类目id
                    userPrivateLabelRestEntity.setTypeId(v.getId());
                    //设置三级类目名称
                    userPrivateLabelRestEntity.setTypeName(v.getSpendName());
                    //设置二级类目id
                    userPrivateLabelRestEntity.setTypePid(v.getParentId());
                    //绑定用户
                    userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                    //绑定账本id
                    userPrivateLabelRestEntity.setAccountBookId(abId);
                    //图标
                    userPrivateLabelRestEntity.setIcon(v.getIcon());
                    //优先级
                    userPrivateLabelRestEntity.setPriority(v.getPriority());
                    //设置属性  1:支出 2:收入
                    userPrivateLabelRestEntity.setProperty(1);
                    //设置属性  1:系统分配  2:用户自建
                    userPrivateLabelRestEntity.setType(1);
                    //设置属性  1:有效  0:失效
                    userPrivateLabelRestEntity.setStatus(1);
                    //
                    userPrivateLabelRestEntity.setAbTypeLabelId(v.getLabelId());
                    //insert
                    userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
                });
            }
        }
    }

    //微信注册用户
    @Override
    public int wechatinsert(JSONObject jsonObject, Map<String, String> map, String type) {

        UserInfoRestEntity userInfoRestEntity = new UserInfoRestEntity();
        //设置昵称
        if (StringUtils.isNotEmpty(jsonObject.getString("nickname"))) {
            String nickName = jsonObject.getString("nickname");
            nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
            userInfoRestEntity.setNickName(nickName);
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("nickName"))) {
            String nickName = jsonObject.getString("nickName");
            nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
            userInfoRestEntity.setNickName(nickName);
        }
        //设置性别
        if (StringUtils.isNotEmpty(jsonObject.getString("sex"))) {
            userInfoRestEntity.setSex(jsonObject.getString("sex"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("gender"))) {
            userInfoRestEntity.setSex(jsonObject.getString("gender"));
        }
        /*//设置省
        if(StringUtils.isNotEmpty(jsonObject.getString("province"))){
            userInfoRestEntity.setProvinceName(jsonObject.getString("province"));
        }
        //设置市
        if(StringUtils.isNotEmpty(jsonObject.getString("city"))){
            userInfoRestEntity.setCityName(jsonObject.getString("city"));
        }*/
        //设置头像
        if (StringUtils.isNotEmpty(jsonObject.getString("headimgurl"))) {
            userInfoRestEntity.setAvatarUrl(jsonObject.getString("headimgurl"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("avatarUrl"))) {
            userInfoRestEntity.setAvatarUrl(jsonObject.getString("avatarUrl"));
        }

        if (StringUtils.isNotEmpty(jsonObject.getString("unionid"))) {
            userInfoRestEntity.setWechatAuth(jsonObject.getString("unionid"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("unionId"))) {
            userInfoRestEntity.setWechatAuth(jsonObject.getString("unionId"));
        }
        //设置设备号
        if (StringUtils.isNotEmpty(map.get("mobileDevice"))) {
            userInfoRestEntity.setMobileDevice(map.get("mobileDevice"));
        }
        //设置终端厂商
        if (StringUtils.isNotEmpty(map.get("mobileManufacturer"))) {
            userInfoRestEntity.setMobileManufacturer(map.get("mobileManufacturer"));
        }
        //设置系统标识
        if (StringUtils.isNotEmpty(type)) {
            userInfoRestEntity.setMobileSystem(type);
        }
        //设置安卓应用商店渠道标识
        if (StringUtils.isNotEmpty(map.get("androidChannel"))) {
            userInfoRestEntity.setAndroidChannel(map.get("androidChannel"));
        }

        //判断是否是从邀请多人记账页面过来，如果是带手机号  设置手机号
        if (StringUtils.isNotEmpty(jsonObject.getString("mobile"))) {
            userInfoRestEntity.setMobile(map.get("mobile"));
        }

        //insert user info表
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //获取主键,insert-->user login 表
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //转存属性值
        userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        userLogin.setUserInfoId(insertId);
        //判断是否是从邀请多人记账页面过来，如果是带手机号  设置手机号
        if (StringUtils.isNotEmpty(jsonObject.getString("mobile"))) {
            userLogin.setMobile(map.get("mobile"));
        }
        userLoginRestDao.insert(userLogin);
        //创建账本----->绑定用户id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setAbName(userLogin.getMobile());
        }
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        ab.setAbName("默认账本");
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        //设置账本类型
        //获取日常账本类型id
        int typeId = userPrivateLabelRestDao.getDefaultAccountBookTypeId();
        ab.setAccountBookTypeId(typeId);
        int insertId2 = accountBookRestDao.insert(ab);
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        int insert3 = userAccountBookRestDao.insert(uabre);
        //移动端注册  分配默认账本标签
        if (StringUtils.equals("android", type) || StringUtils.equals("ios", type)) {
            insertDefaultLabel(insertId, insertId2);
        }
        //处理当日任务  发送小程序服务通知
        taskExecutor.execute(() -> {
            //判断是否为受邀用户
            if (StringUtils.isNotEmpty(map.get("inviteCode"))) {
                int userInfoId = ShareCodeUtil.sharecode2id(map.get("inviteCode"));
                userInviteRestDao.insert(userInfoId, insertId);
                //引入当日任务
                createTokenUtils.integralTask(userInfoId + "", ShareCodeUtil.id2sharecode(userInfoId), CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Inviting_friends);
                //小程序----->服务通知
                String openId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId + "");
                if (StringUtils.isNotEmpty(openId)) {
                    //获取formId
                    Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                    if (keys.size() > 0) {
                        Object[] arrays = keys.toArray();
                        Arrays.sort(arrays,Collections.reverseOrder());
                        String formId =(String) redisTemplateUtils.popListRight(arrays[0]+"");
                        WXAppletMessageBean bean = new WXAppletMessageBean();
                        //设置好友昵称
                        bean.getKeyword1().put("value", userInfoRestEntity.getNickName() == null ? "蜂鸟用户" : userInfoRestEntity.getNickName());
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
            }
            String wxappletChannel = map.get("wxappletChannel");
            statisticsForRegister(wxappletChannel,insertId);
        });
        return insert3;
    }

    /**
     * 统计从其他渠道进来的用户数据
     */
    private void statisticsForRegister(String channel,int userInfoId){
        //统计从游戏渠道注册成功的人数
        if (StringUtils.isNotEmpty(channel)) {
            redisTemplateUtils.incrementNewRegister(channel, "sumNewRegister");
        }
        registerChannelRestServiceI.insert(channel,userInfoId,1);
    }
    /**
     * 根据userInfoId更新密码
     *
     * @return
     */
    @Override
    public int updatePWD(int userInfoId, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    /**
     * 根据手机号更改密码
     *
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public int updatePWDByMobile(String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
        if (i > 0) {
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
            return j;
        }
        return i;
    }

    /**
     * 更新手势密码开关状态
     *
     * @param userInfoId
     * @param gesturePwType
     * @return
     */
    @Override
    public int updateGestureType(String userInfoId, String gesturePwType) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    /**
     * 更新手势密码
     *
     * @param userInfoId
     * @param gesturePw
     * @return
     */
    @Override
    public int updateGesture(String userInfoId, String gesturePw) {
        if (gesturePw.length() < 1) {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `gesture_pw` = NULL , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
            if (i > 0) {
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw` = NULL , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        } else {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
            if (i > 0) {
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        }
    }

    @Override
    public int updateMobile(String userInfoId, String mobile) {
        //引入新手任务
        createTokenUtils.integralTask(userInfoId, ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.binding_phone_or_wx);
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateMobileAndPWD(String userInfoId, String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "', `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateWeChat(String userInfoId, String code, String unionid) {
        //引入新手任务
        createTokenUtils.integralTask(userInfoId, ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.binding_phone_or_wx);
        if (StringUtils.isNotEmpty(unionid)) {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `wechat_auth` = '" + unionid + "', `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if (i > 0) {
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `wechat_auth` = '" + unionid + "' , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        } else {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if (i > 0) {
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        }
    }

    @Override
    public void updateUserInfo(UserInfoRestEntity userInfoRestEntity, UserInfoRestDTO task) {
        if (userInfoRestEntity.getBirthday() != null) {
            //计算年龄+星座
            int ageByBirth = DateUtils.getAgeByBirth(userInfoRestEntity.getBirthday());
            userInfoRestEntity.setAge(ageByBirth + "");
            Calendar cal = Calendar.getInstance();
            cal.setTime(userInfoRestEntity.getBirthday());
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String constellation = getConstellation(month, day);
            userInfoRestEntity.setConstellation(constellation);
        }
        if (task != null) {
            //小程序用户
            if (StringUtils.isNotEmpty(task.getSex()) && task.getBirthday() != null && StringUtils.isNotEmpty(task.getProvinceName()) && StringUtils.isNotEmpty(task.getProfession()) && StringUtils.isNotEmpty(task.getPosition())) {
                if (!StringUtils.equals(task.getSex(), "0")) {
                    //引入新手任务
                    createTokenUtils.integralTask(userInfoRestEntity.getId() + "", ShareCodeUtil.id2sharecode(userInfoRestEntity.getId()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Perfecting_personal_data);
                }
            }
        } else {
            if (StringUtils.isNotEmpty(userInfoRestEntity.getSex()) && userInfoRestEntity.getBirthday() != null && StringUtils.isNotEmpty(userInfoRestEntity.getProvinceName()) && StringUtils.isNotEmpty(userInfoRestEntity.getProfession()) && StringUtils.isNotEmpty(userInfoRestEntity.getPosition())) {
                if (!StringUtils.equals(userInfoRestEntity.getSex(), "0")) {
                    //引入新手任务
                    createTokenUtils.integralTask(userInfoRestEntity.getId() + "", ShareCodeUtil.id2sharecode(userInfoRestEntity.getId()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Perfecting_personal_data);
                }
            }
        }
        userInfoRestDao.update(userInfoRestEntity);
    }

    /**
     * 获取星座
     */
    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }
}