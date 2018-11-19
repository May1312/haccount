package com.fnjz.front.service.api.userlogin;

import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface UserLoginRestServiceI extends CommonService{

    /**
     * 功能描述: 判断是不是小程序老用户
     *
     * @param: unionid
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 14:59
     */
    UserLoginRestEntity wxUnionidIsExist(String unionid);

}
