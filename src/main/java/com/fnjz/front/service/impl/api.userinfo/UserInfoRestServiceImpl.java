package com.fnjz.front.service.impl.api.userinfo;

import com.fnjz.front.dao.AccountBookRestDao;
import com.fnjz.front.dao.UserAccountBookRestDao;
import com.fnjz.front.dao.UserInfoRestDao;
import com.fnjz.front.dao.UserLoginRestDao;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
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
        //TODO  注册完成需缓存用户信息
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
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_login` SET `password` = " + password + " , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
       if(i>0){
           //更新info表
           int j = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_user_info` SET `password` = " + password + " , `update_date` = NOW() WHERE `mobile` = " + mobile + ";");
            return j;
       }
        return i;
    }
}