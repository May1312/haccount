package com.fnjz.front.service.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface UserInfoRestServiceI extends CommonService{

    /**
     * 小程序注册用户
     */
    int insert(UserInfoRestEntity userInfoRestEntity);
    /**
     * 微信注册用户
     */
    int wechatinsert(JSONObject jsonObject,String mobileDevice);

    /**
     * 根据userInfoId更新密码
     * @param userInfoId
     * @param password
     * @return
     */
    int updatePWD(int userInfoId,String password) ;

    /**
     * 根据userInfoId更新密码
     * @param mobile
     * @param password
     * @return
     */
    int updatePWDByMobile(String mobile,String password) ;

    /**
     * 更新手势开关状态
     * @param userInfoId
     * @param gesturePwType
     * @return
     */
    int updateGestureType(String userInfoId, String gesturePwType);

    /**
     * 更新手势密码
     * @param userInfoId
     * @param gesturePw
     * @return
     */
    int updateGesture(String userInfoId, String gesturePw);

    /**
     * 更新手机号
     * @param userInfoId
     * @param mobile
     * @return
     */
    int updateMobile(String userInfoId, String mobile);

    /**
     * 绑定手机号密码
     * @param userInfoId
     * @param mobile
     * @param password
     * @return
     */
    int updateMobileAndPWD(String userInfoId, String mobile, String password);

    /**
     * 绑定(更新)wechat_auth
     * @param code
     * @param unionid
     * @return
     */
    int updateWeChat(String code, String unionid);

    /**
     * 更新用户详情
     * @param userInfoRestEntity
     */
    void updateUserInfo(UserInfoRestEntity userInfoRestEntity);
}
