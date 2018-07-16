package com.fnjz.front.service.impl.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.dao.AccountBookRestDao;
import com.fnjz.front.dao.UserAccountBookRestDao;
import com.fnjz.front.dao.UserInfoRestDao;
import com.fnjz.front.dao.UserLoginRestDao;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.EmojiUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import java.util.ArrayList;
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
        //分配系统默认收入支出标签   1上线  1常用  支出
        String spend_sql = "select id,priority from hbird_spend_type where status = 1 AND mark = 1";
        List<Map> listbySql = commonDao.findListMapbySql(spend_sql);
        List<UserCommUseSpendRestEntity> list_common_spend = new ArrayList<>();
        for (int i = 0;i<listbySql.size();i++) {
            UserCommUseSpendRestEntity userCommUseSpendRestEntity = new UserCommUseSpendRestEntity();
            //设置三级类目id
            userCommUseSpendRestEntity.setSpendTypeId(listbySql.get(i).get("id")+"");
            //设置优先级
            userCommUseSpendRestEntity.setPriority(Integer.valueOf(listbySql.get(i).get("priority")+""));
            userCommUseSpendRestEntity.setUserInfoId(insertId);
            list_common_spend.add(userCommUseSpendRestEntity);
        }
        commonDao.batchSave(list_common_spend);
        //收入
        String income_sql = "select id,priority from hbird_income_type where status = 1 AND mark = 1";
        List<Map> listbySql2 = commonDao.findListMapbySql(income_sql);
        List<UserCommUseIncomeRestEntity> list_common_income = new ArrayList<>();
        for (int j = 0;j<listbySql2.size();j++) {
            UserCommUseIncomeRestEntity userCommUseIncomeRestEntity = new UserCommUseIncomeRestEntity();
            //设置三级类目id
            userCommUseIncomeRestEntity.setIncomeTypeId(listbySql2.get(j).get("id")+"");
            //设置优先级
            userCommUseIncomeRestEntity.setPriority(Integer.valueOf(listbySql2.get(j).get("priority")+""));
            userCommUseIncomeRestEntity.setUserInfoId(insertId);
            list_common_income.add(userCommUseIncomeRestEntity);
        }
        commonDao.batchSave(list_common_income);
        return insert3;
    }

    //微信注册用户
    @Override
    public int wechatinsert(JSONObject jsonObject) {
        UserInfoRestEntity userInfoRestEntity = new UserInfoRestEntity();
        //设置昵称
        if(StringUtils.isNotEmpty(jsonObject.getString("nickname"))){
            //userInfoRestEntity.setNickName(EmojiUtils.emojiToAlias(jsonObject.getString("nickname")));
            userInfoRestEntity.setNickName(jsonObject.getString("nickname"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("nickName"))){
            //userInfoRestEntity.setNickName(EmojiUtils.emojiToAlias(jsonObject.getString("nickName")));
            userInfoRestEntity.setNickName(jsonObject.getString("nickName"));
        }
        //设置性别
        if(StringUtils.isNotEmpty(jsonObject.getString("sex"))){
            userInfoRestEntity.setSex(jsonObject.getString("sex"));
        }
        if(StringUtils.isNotEmpty(jsonObject.getString("gender"))){
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
        //分配系统默认收入支出标签   1上线  1常用  支出
        String spend_sql = "select id,priority from hbird_spend_type where status = 1 AND mark = 1";
        List<Map> listbySql = commonDao.findListMapbySql(spend_sql);
        List<UserCommUseSpendRestEntity> list_common_spend = new ArrayList<>();
        for (int i = 0;i<listbySql.size();i++) {
            UserCommUseSpendRestEntity userCommUseSpendRestEntity = new UserCommUseSpendRestEntity();
            //设置三级类目id
            userCommUseSpendRestEntity.setSpendTypeId(listbySql.get(i).get("id")+"");
            //设置优先级
            userCommUseSpendRestEntity.setPriority(Integer.valueOf(listbySql.get(i).get("priority")+""));
            userCommUseSpendRestEntity.setUserInfoId(insertId);
            list_common_spend.add(userCommUseSpendRestEntity);
        }
        commonDao.batchSave(list_common_spend);
        //收入
        String income_sql = "select id,priority from hbird_income_type where status = 1 AND mark = 1";
        List<Map> listbySql2 = commonDao.findListMapbySql(income_sql);
        List<UserCommUseIncomeRestEntity> list_common_income = new ArrayList<>();
        for (int j = 0;j<listbySql2.size();j++) {
            UserCommUseIncomeRestEntity userCommUseIncomeRestEntity = new UserCommUseIncomeRestEntity();
            //设置三级类目id
            userCommUseIncomeRestEntity.setIncomeTypeId(listbySql2.get(j).get("id")+"");
            //设置优先级
            userCommUseIncomeRestEntity.setPriority(Integer.valueOf(listbySql2.get(j).get("priority")+""));
            userCommUseIncomeRestEntity.setUserInfoId(insertId);
            list_common_income.add(userCommUseIncomeRestEntity);
        }
        commonDao.batchSave(list_common_income);
        return insert3;
    }

    /**
     * 根据userInfoId更新密码
     * @return
     */
    @Override
    public int updatePWD(int userInfoId,String password) {
       int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `user_info_id` = " + userInfoId + ";");
       if(i>0){
           //更新info表
           int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `id` = " + userInfoId + ";");
            return j;
       }
        return i;
    }

    /**
     * 根据手机号更改密码
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public int updatePWDByMobile(String mobile, String password) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
        if(i>0){
            //更新info表
            int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `password` = '" + password + "' , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
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
        if(StringUtils.isNotEmpty(unionid)){
            int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `wechat_auth` = '" + unionid + "', `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if(i>0){
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `wechat_auth` = '" + unionid + "' , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        }else{
            int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
            if(i>0){
                //更新info表
                int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `wechat_auth` = NULL , `update_date` = NOW() WHERE `mobile` = '" + code + "';");
                return j;
            }
            return i;
        }
    }

    @Override
    public void updateUserInfo(UserInfoRestEntity userInfoRestEntity) {
        if(userInfoRestEntity.getBirthday()!=null){
            //计算年龄+星座
            int ageByBirth = DateUtils.getAgeByBirth(userInfoRestEntity.getBirthday());
            userInfoRestEntity.setAge(ageByBirth+"");
            Calendar cal = Calendar.getInstance();
            cal.setTime(userInfoRestEntity.getBirthday());
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String constellation = getConstellation(month,day);
            userInfoRestEntity.setConstellation(constellation);
        }
        userInfoRestDao.update(userInfoRestEntity);
    }

    /**
     * 获取星座
     */
    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }
}