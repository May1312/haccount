package com.fnjz.front.service.impl.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.incometype.IncomeTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestDTO;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.channel.ChannelRestServiceI;
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
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
    private UserPrivateLabelRestDao userPrivateLabelRestDao;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;
    @Autowired
    private RegisterChannelRestServiceI registerChannelRestServiceI;
    @Autowired
    private ChannelRestServiceI channelRestServiceI;

    @Override
    public int insert(UserInfoRestEntity userInfoRestEntity, String type) {
        //??????????????????
        if(StringUtils.isNotEmpty(userInfoRestEntity.getAndroidChannel())){
            //??????????????????
            Integer id = channelRestServiceI.getIdByChannelNid(userInfoRestEntity.getAndroidChannel());
            if(id!=null){
                userInfoRestEntity.setChannelId(id);
            }
        }
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //????????????,insert-->user login ???
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //???????????????
        if (StringUtil.isNotEmpty(userInfoRestEntity.getMobile())) {
            userLogin.setMobile(userInfoRestEntity.getMobile());
        }
        //????????????
        if (StringUtil.isNotEmpty(userInfoRestEntity.getPassword())) {
            userLogin.setPassword(userInfoRestEntity.getPassword());
        }
        if (StringUtil.isNotEmpty(userInfoRestEntity.getWechatAuth())) {
            userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        }
        //?????????????????????id
        userLogin.setUserInfoId(insertId);
        userLoginRestDao.insert(userLogin);
        //????????????----->????????????id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        //??????????????????
        //????????????????????????id
        int typeId = userPrivateLabelRestDao.getDefaultAccountBookTypeId();
        ab.setAccountBookTypeId(typeId);
        ab.setAbName("????????????");
        int insertId2 = accountBookRestDao.insert(ab);
        //????????????---??????????????????
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        //???????????????????????????
        uabre.setDefaultFlag(1);
        int insert3 = userAccountBookRestDao.insert(uabre);
        //???????????????  ????????????????????????
        if (StringUtils.equals("android", type) || StringUtils.equals("ios", type)) {
            insertDefaultLabel(insertId, insertId2);
        }
        taskExecutor.execute(()->{
            //??????????????????---->?????????????????????
            createTokenUtils.integralTask(insertId + "", null, CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Become_hbird_user);
        });
        return insert3;
    }

    /**
     * ????????????????????????
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
                    //??????????????????id
                    userPrivateLabelRestEntity.setTypeId(v.getId());
                    //????????????????????????
                    userPrivateLabelRestEntity.setTypeName(v.getIncomeName());
                    //??????????????????id
                    userPrivateLabelRestEntity.setTypePid(v.getParentId());
                    //???????????????
                    userPrivateLabelRestEntity.setPriority(v.getPriority());
                    //????????????
                    userPrivateLabelRestEntity.setUserInfoId(userInfoId);
                    //????????????id
                    userPrivateLabelRestEntity.setAccountBookId(abId);
                    //??????
                    userPrivateLabelRestEntity.setIcon(v.getIcon());
                    //????????????  1:?????? 2:??????
                    userPrivateLabelRestEntity.setProperty(2);
                    //????????????  1:????????????  2:????????????
                    userPrivateLabelRestEntity.setType(1);
                    //????????????  1:??????  0:??????
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
                    //??????????????????id
                    userPrivateLabelRestEntity.setTypeId(v.getId());
                    //????????????????????????
                    userPrivateLabelRestEntity.setTypeName(v.getSpendName());
                    //??????????????????id
                    userPrivateLabelRestEntity.setTypePid(v.getParentId());
                    //????????????
                    userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                    //????????????id
                    userPrivateLabelRestEntity.setAccountBookId(abId);
                    //??????
                    userPrivateLabelRestEntity.setIcon(v.getIcon());
                    //?????????
                    userPrivateLabelRestEntity.setPriority(v.getPriority());
                    //????????????  1:?????? 2:??????
                    userPrivateLabelRestEntity.setProperty(1);
                    //????????????  1:????????????  2:????????????
                    userPrivateLabelRestEntity.setType(1);
                    //????????????  1:??????  0:??????
                    userPrivateLabelRestEntity.setStatus(1);
                    //
                    userPrivateLabelRestEntity.setAbTypeLabelId(v.getLabelId());
                    //insert
                    userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
                });
            }
        }
    }

    //??????????????????
    @Override
    public int wechatinsert(JSONObject jsonObject, Map<String, String> map, String type) {

        UserInfoRestEntity userInfoRestEntity = new UserInfoRestEntity();
        //????????????
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
        //????????????
        if (StringUtils.isNotEmpty(jsonObject.getString("sex"))) {
            userInfoRestEntity.setSex(jsonObject.getString("sex"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("gender"))) {
            userInfoRestEntity.setSex(jsonObject.getString("gender"));
        }
        /*//?????????
        if(StringUtils.isNotEmpty(jsonObject.getString("province"))){
            userInfoRestEntity.setProvinceName(jsonObject.getString("province"));
        }
        //?????????
        if(StringUtils.isNotEmpty(jsonObject.getString("city"))){
            userInfoRestEntity.setCityName(jsonObject.getString("city"));
        }*/
        //????????????
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
        //???????????????
        if (StringUtils.isNotEmpty(map.get("mobileDevice"))) {
            userInfoRestEntity.setMobileDevice(map.get("mobileDevice"));
        }
        //??????????????????
        if (StringUtils.isNotEmpty(map.get("mobileManufacturer"))) {
            userInfoRestEntity.setMobileManufacturer(map.get("mobileManufacturer"));
        }
        //??????????????????
        if (StringUtils.isNotEmpty(type)) {
            userInfoRestEntity.setMobileSystem(type);
        }
        //????????????????????????????????????
        if (StringUtils.isNotEmpty(map.get("androidChannel"))) {
            userInfoRestEntity.setAndroidChannel(map.get("androidChannel"));
        }

        //????????????????????????????????????????????????????????????????????????  ???????????????
        if (StringUtils.isNotEmpty(jsonObject.getString("mobile"))) {
            userInfoRestEntity.setMobile(map.get("mobile"));
        }
        //??????android????????????
        if(StringUtils.isNotEmpty(map.get("androidChannel"))){
            //??????????????????
            Integer id = channelRestServiceI.getIdByChannelNid(map.get("androidChannel"));
            if(id!=null){
                userInfoRestEntity.setChannelId(id);
            }
        }
        //???????????????????????????
        if(StringUtils.isNotEmpty(map.get("wxappletChannel"))){
            //??????????????????
            Integer id = channelRestServiceI.getIdByChannelNid(map.get("wxappletChannel"));
            if(id!=null){
                userInfoRestEntity.setChannelId(id);
            }
        }
        //insert user info???
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //????????????,insert-->user login ???
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //???????????????
        userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        userLogin.setUserInfoId(insertId);
        //????????????????????????????????????????????????????????????????????????  ???????????????
        if (StringUtils.isNotEmpty(jsonObject.getString("mobile"))) {
            userLogin.setMobile(map.get("mobile"));
        }
        userLoginRestDao.insert(userLogin);
        //????????????----->????????????id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setAbName(userLogin.getMobile());
        }
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        ab.setAbName("????????????");
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        //??????????????????
        //????????????????????????id
        int typeId = userPrivateLabelRestDao.getDefaultAccountBookTypeId();
        ab.setAccountBookTypeId(typeId);
        int insertId2 = accountBookRestDao.insert(ab);
        //????????????---??????????????????
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if (StringUtil.isNotEmpty(userLogin.getMobile())) {
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        int insert3 = userAccountBookRestDao.insert(uabre);
        //???????????????  ????????????????????????
        if (StringUtils.equals("android", type) || StringUtils.equals("ios", type)) {
            insertDefaultLabel(insertId, insertId2);
        }
        //??????????????????  ???????????????????????????
        taskExecutor.execute(() -> {
            //??????????????????---->?????????????????????
            createTokenUtils.integralTask(insertId + "", null, CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Become_hbird_user);
            //???????????????????????????
            //if (StringUtils.isNotEmpty(map.get("inviteCode"))) {
            //    int userInfoId = ShareCodeUtil.sharecode2id(map.get("inviteCode"));
                /*userInviteRestDao.insert(userInfoId, insertId);
                //??????????????????---->????????????
                createTokenUtils.integralTask(userInfoId + "", insertId+"", CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Inviting_friends);
                //??????????????????---->?????????5???
                createTokenUtils.integralTask(userInfoId + "", null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.The_invitation_came_to_five);*/
                //?????????----->????????????
                /*String openId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId + "");
                if (StringUtils.isNotEmpty(openId)) {
                    //??????formId
                    Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                    if (keys.size() > 0) {
                        Object[] arrays = keys.toArray();
                        Arrays.sort(arrays, Collections.reverseOrder());
                        String formId = (String) redisTemplateUtils.popListRight(arrays[0] + "");
                        WXAppletMessageBean bean = new WXAppletMessageBean();
                        //??????????????????
                        bean.getKeyword1().put("value", userInfoRestEntity.getNickName() == null ? "????????????" : userInfoRestEntity.getNickName());
                        //??????????????????
                        bean.getKeyword2().put("value", LocalDate.now().toString());
                        //??????????????????
                        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(null, AcquisitionModeEnum.Inviting_friends.getName(), null);
                        if (fengFengTicket != null) {
                            bean.getKeyword3().put("value", fengFengTicket.getBehaviorTicketValue() == null ? "0" : fengFengTicket.getBehaviorTicketValue() + "???????????????0.4??????");
                        }
                        //?????????????????????
                        int inviteUsers = userInviteRestDao.getCountForInvitedUsers(userInfoId + "");
                        bean.getKeyword4().put("value", inviteUsers + "???");
                        //????????????
                        bean.getKeyword5().put("value", "??????????????????????????????????????????");
                        wxAppletPushUtils.wxappletPush(WXAppletPushUtils.inviteFriendId, openId, formId, WXAppletPushUtils.inviteFriendPage, bean);
                    }
                }*/
            //}
            String wxappletChannel = map.get("wxappletChannel");
            statisticsForRegister(wxappletChannel,insertId);
        });
        return insert3;
    }

    /**
     * ??????????????????????????????????????????
     */
    private void statisticsForRegister(String channel,int userInfoId){
        //??????????????????????????????????????????
        if (StringUtils.isNotEmpty(channel)) {
            redisTemplateUtils.incrementNewRegister(channel, "sumNewRegister");
            //????????????
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
            String time = LocalDate.now().format(formatters);
            //????????????????????????
            redisTemplateUtils.incrementNewRegisterV2(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumNewRegister" + "_" + time, "sumNewRegister");
            registerChannelRestServiceI.insert(channel,userInfoId,1);
        }
    }
    /**
     * ??????userInfoId????????????
     *
     * @return
     */
    @Override
    public int updatePWD(int userInfoId, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //??????info???
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    /**
     * ???????????????????????????
     *
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public int updatePWDByMobile(String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
        if (i > 0) {
            //??????info???
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
            return j;
        }
        return i;
    }

    /**
     * ??????????????????????????????
     *
     * @param userInfoId
     * @param gesturePwType
     * @return
     */
    @Override
    public int updateGestureType(String userInfoId, String gesturePwType) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //??????info???
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    /**
     * ??????????????????
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
                //??????info???
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw` = NULL , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        } else {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
            if (i > 0) {
                //??????info???
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        }
    }

    @Override
    public int updateMobile(String userInfoId, String mobile) {
        //??????????????????
        createTokenUtils.integralTask(userInfoId, ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.binding_phone_or_wx);
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //??????info???
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateMobileAndPWD(String userInfoId, String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "', `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if (i > 0) {
            //??????info???
            int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateWeChat(String userInfoId, String code, String unionid) {
        //??????????????????
        createTokenUtils.integralTask(userInfoId, ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.binding_phone_or_wx);
        if (StringUtils.isNotEmpty(unionid)) {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `wechat_auth` = '" + unionid + "', `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if (i > 0) {
                //??????info???
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `wechat_auth` = '" + unionid + "' , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        } else {
            int i = commonDao.updateBySqlString("UPDATE `hbird_user_login` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if (i > 0) {
                //??????info???
                int j = commonDao.updateBySqlString("UPDATE `hbird_user_info` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        }
    }

    @Override
    public void updateUserInfo(UserInfoRestEntity userInfoRestEntity, UserInfoRestDTO task) {
        if (userInfoRestEntity.getBirthday() != null) {
            //????????????+??????
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
            //???????????????
            if (StringUtils.isNotEmpty(userInfoRestEntity.getSex()) && userInfoRestEntity.getBirthday() != null && StringUtils.isNotEmpty(userInfoRestEntity.getProvinceName()) && StringUtils.isNotEmpty(userInfoRestEntity.getProfession()) && StringUtils.isNotEmpty(userInfoRestEntity.getPosition())) {
                if (!StringUtils.equals(task.getSex(), "0")) {
                    //??????????????????
                    createTokenUtils.integralTask(userInfoRestEntity.getId() + "", ShareCodeUtil.id2sharecode(userInfoRestEntity.getId()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Perfecting_personal_data);
                }
            }
        } else {
            if (StringUtils.isNotEmpty(userInfoRestEntity.getSex()) && userInfoRestEntity.getBirthday() != null && StringUtils.isNotEmpty(userInfoRestEntity.getProvinceName()) && StringUtils.isNotEmpty(userInfoRestEntity.getProfession()) && StringUtils.isNotEmpty(userInfoRestEntity.getPosition())) {
                if (!StringUtils.equals(userInfoRestEntity.getSex(), "0")) {
                    //??????????????????
                    createTokenUtils.integralTask(userInfoRestEntity.getId() + "", ShareCodeUtil.id2sharecode(userInfoRestEntity.getId()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Perfecting_personal_data);
                }
            }
        }
        userInfoRestDao.update(userInfoRestEntity);
    }

    /**
     * ????????????
     */
    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????"};

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }
}