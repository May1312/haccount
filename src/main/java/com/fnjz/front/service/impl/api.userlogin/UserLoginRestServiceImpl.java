package com.fnjz.front.service.impl.api.userlogin;

import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userLoginRestService")
@Transactional
public class UserLoginRestServiceImpl extends CommonServiceImpl implements UserLoginRestServiceI {

    @Override
    public UserLoginRestEntity wxUnionidIsExist(String unionid) {
        UserLoginRestEntity task = this.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", unionid);
        if (task != null){
            return task;
        }
        return null;
    }
}