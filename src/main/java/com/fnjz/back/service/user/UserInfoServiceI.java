package com.fnjz.back.service.user;

import org.jeecgframework.core.common.service.CommonService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;

public interface UserInfoServiceI extends CommonService{
    //属性统计
    HashMap<String, Object> attributeStatistics(String startDate, String endDate);

}
