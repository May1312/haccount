package com.fnjz.front.service.impl.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.dao.AccountBookRestDao;
import com.fnjz.front.dao.UserAccountBookRestDao;
import com.fnjz.front.dao.UserInfoRestDao;
import com.fnjz.front.dao.UserLoginRestDao;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

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

    @Override
    public int insert(UserInfoRestEntity userInfoRestEntity) {
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //获取主键,insert-->user login 表
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //设置手机号
        if(StringUtil.isNotEmpty(userInfoRestEntity.getMobile())){
            userLogin.setMobile(userInfoRestEntity.getMobile());
        }
        //设置密码
        if(StringUtil.isNotEmpty(userInfoRestEntity.getPassword())) {
            userLogin.setPassword(userInfoRestEntity.getPassword());
        }
        if(StringUtil.isNotEmpty(userInfoRestEntity.getWechatAuth())) {
            userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        }
        //设置用户详情表id
        userLogin.setUserInfoId(insertId);
        userLoginRestDao.insert(userLogin);
        //创建账本----->绑定用户id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        if(StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setAbName(userLogin.getMobile());
        }
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        if(StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        int insertId2 = accountBookRestDao.insert(ab);
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if(StringUtil.isNotEmpty(userLogin.getMobile())){
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        int insert3 = userAccountBookRestDao.insert(uabre);
        return insert3;
    }

    //微信注册用户
    @Override
    public int wechatinsert(JSONObject jsonObject) {
        UserInfoRestEntity userInfoRestEntity = new UserInfoRestEntity();
        //设置昵称
        if(StringUtils.isNotEmpty(jsonObject.getString("nickname"))){
            userInfoRestEntity.setNickName(jsonObject.getString("nickname"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("nickName"))){
            userInfoRestEntity.setNickName(jsonObject.getString("nickName"));
        }
        //设置性别
        if(StringUtils.isNotEmpty(jsonObject.getString("sex"))){
            userInfoRestEntity.setSex(jsonObject.getString("sex"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("gender"))){
            userInfoRestEntity.setSex(jsonObject.getString("gender"));
        }
        //设置省
        if(StringUtils.isNotEmpty(jsonObject.getString("province"))){
            userInfoRestEntity.setProvinceName(jsonObject.getString("province"));
        }
        //设置市
        if(StringUtils.isNotEmpty(jsonObject.getString("city"))){
            userInfoRestEntity.setCityName(jsonObject.getString("city"));
        }
        //设置头像
        if(StringUtils.isNotEmpty(jsonObject.getString("headimgurl"))){
            userInfoRestEntity.setAvatarUrl(jsonObject.getString("headimgurl"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("avatarUrl"))){
            userInfoRestEntity.setAvatarUrl(jsonObject.getString("avatarUrl"));
        }

        if(StringUtils.isNotEmpty(jsonObject.getString("unionid"))){
            userInfoRestEntity.setWechatAuth(jsonObject.getString("unionid"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("unionId"))){
            userInfoRestEntity.setWechatAuth(jsonObject.getString("unionId"));
        }

        //insert user info表
        int insertId = userInfoRestDao.insert(userInfoRestEntity);
        //获取主键,insert-->user login 表
        UserLoginRestEntity userLogin = new UserLoginRestEntity();
        //转存属性值
        userLogin.setWechatAuth(userInfoRestEntity.getWechatAuth());
        userLogin.setUserInfoId(insertId);
        userLoginRestDao.insert(userLogin);
        //创建账本----->绑定用户id
        AccountBookRestEntity ab = new AccountBookRestEntity();
        if(StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setAbName(userLogin.getMobile());
        }
        ab.setStatus(0);
        ab.setCreateBy(insertId);
        if(StringUtil.isNotEmpty(userLogin.getMobile())) {
            ab.setCreateName(userLogin.getMobile());
        }
        int insertId2 = accountBookRestDao.insert(ab);
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(insertId);
        uabre.setAccountBookId(insertId2);
        uabre.setCreateBy(insertId);
        if(StringUtil.isNotEmpty(userLogin.getMobile())){
            uabre.setCreateName(userLogin.getMobile());
        }
        uabre.setUserType(0);
        int insert3 = userAccountBookRestDao.insert(uabre);
        return insert3;
    }

    /**
     * 更新密码
     * @return
     */
    public int updatePWD(String mobile,String password) {
       int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = '" + mobile + "';");
       if(i>0){
           //更新info表
           int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = '" + mobile + "';");
            return j;
       }
        return i;
    }

    /**
     * 更新手势密码开关状态
     * @param userInfoId
     * @param gesturePwType
     * @return
     */
    @Override
    public int updateGestureType(String userInfoId, String gesturePwType) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if(i>0){
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `gesture_pw_type` = '" + gesturePwType + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    /**
     * 更新手势密码
     * @param userInfoId
     * @param gesturePw
     * @return
     */
    @Override
    public int updateGesture(String userInfoId, String gesturePw) {
        if(gesturePw.length()<1){
            int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `gesture_pw` = NULL , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
            if(i>0){
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `gesture_pw` = NULL , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        }else{
            int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
            if(i>0){
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `gesture_pw` = '" + gesturePw + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
                return j;
            }
            return i;
        }
    }

    @Override
    public int updateMobile(String userInfoId, String mobile) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if(i>0){
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `mobile` = '" + mobile + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateMobileAndPWD(String userInfoId, String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "', `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
        if(i>0){
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `mobile` = '" + mobile + "' ,`password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
        }
        return i;
    }

    @Override
    public int updateWeChat(String code, String unionid) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `wechat_auth` = '" + unionid + "', `update_date` = NOW() WHERE `mobile` = '" + code + "';");
        if(i>0){
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `wechat_auth` = '" + unionid + "' , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            return j;
        }
        return i;
    }
}