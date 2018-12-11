package com.fnjz.front.service.impl.api.userinfoaddfield;

import com.fnjz.front.dao.UserInfoAddFieldRestDao;
import com.fnjz.front.entity.api.userinfo.ConsigneeAddressRestDTO;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by yhang on 2018/11/28.
 */
@Service("userInfoAddFieldServiceImpl")
@Transactional
public class UserInfoAddFieldServiceImpl implements UserInfoAddFieldRestService {

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;

    @Override
    public Map<String,Object> checkExists(String userInfoId) {
        return userInfoAddFieldRestDao.checkExistsOpenIdByUserInfoId(userInfoId);
    }

    @Override
    public ConsigneeAddressRestDTO getConsigneeAddress(String userInfoId) {
        return userInfoAddFieldRestDao.getConsigneeAddress(userInfoId);
    }

    @Override
    public void updateConsigneeAddress(String userInfoId, ConsigneeAddressRestDTO bean) {
        //判断新增还是更新
        Integer id = userInfoAddFieldRestDao.checkExistsByUserInfoId(userInfoId);
        if(id==null){
            //insert
            userInfoAddFieldRestDao.insertConsigneeAddress(userInfoId,bean);
        }else{
            bean.setId(id);
            userInfoAddFieldRestDao.updateConsigneeAddress(bean);
        }
    }

    /**
     * 定义  1  小程序   2 移动应用   3 公众号
     * @param userInfoId
     * @param openId
     * @param i
     */
    @Override
    public void insertOpenId(String userInfoId, String openId, int i) {
        if(1==i){
            userInfoAddFieldRestDao.insertWXAppletOpenId(userInfoId,openId);
        }else if(2==i){
            userInfoAddFieldRestDao.insertWechatOpenId(userInfoId,openId);
        }else if(3==i){
            userInfoAddFieldRestDao.insertOfficialOpenId(userInfoId,openId);
        }

    }

    @Override
    public void updateOpenId(String userInfoId, String openId, Integer id, int i) {
        if(1==i){
            userInfoAddFieldRestDao.updateWXAppletOpenId(id,openId);
        }else if(2==i){
            userInfoAddFieldRestDao.updateWechatOpenId(id,openId);
        }else if(3==i){
            userInfoAddFieldRestDao.updateOfficialOpenId(id,openId);
        }
    }

    @Override
    public Integer checkExistsOpenId(String userInfoId, Integer type) {
        String openId=null;
        if(1==type){
            openId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId);
        }else if(2==type){
            openId = userInfoAddFieldRestDao.getWechatOpenId(userInfoId);
        }else if(3==type){
            openId = userInfoAddFieldRestDao.getOfficialOpenId(userInfoId);
        }
        if(StringUtils.isEmpty(openId)){
            return 2;
        }else{
            return 1;
        }
    }
}
