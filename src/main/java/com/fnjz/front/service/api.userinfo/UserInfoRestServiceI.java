package com.fnjz.front.service.api.userinfo;

import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface UserInfoRestServiceI extends CommonService{

    int insert(UserInfoRestEntity userInfoRestEntity);

    //更新密码
    int updatePWD(String mobile,String password) ;
}