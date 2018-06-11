package com.fnjz.front.service.api.usercommuseincome;

import org.jeecgframework.core.common.service.CommonService;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface UserCommUseIncomeRestServiceI extends CommonService{

    //获取用户常用类目标签列表
    Map<String,Object> getListById(String user_info_id) throws InvocationTargetException, IllegalAccessException;
}
