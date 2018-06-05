package com.fnjz.front.service.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface UserInfoRestServiceI extends CommonService{

    //小程序注册用户
    int insert(UserInfoRestEntity userInfoRestEntity);
    //微信注册用户
    int wechatinsert(JSONObject jsonObject);
    //更新密码
    int updatePWD(String mobile,String password) ;
    //更新手势开关状态
    int updateGestureType(String userInfoId, String gesturePwType);
    //更新手势密码
    int updateGesture(String userInfoId, String gesturePw);
    //更新手机号
    int updateMobile(String userInfoId, String mobile);
}
