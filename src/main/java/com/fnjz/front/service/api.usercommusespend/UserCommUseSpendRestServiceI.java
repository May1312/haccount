package com.fnjz.front.service.api.usercommusespend;

import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface UserCommUseSpendRestServiceI extends CommonService{
    //获取支出类目
    Map<String,Object> getListById(String user_info_id);
}
