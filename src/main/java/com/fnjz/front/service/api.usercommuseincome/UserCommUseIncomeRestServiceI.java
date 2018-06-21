package com.fnjz.front.service.api.usercommuseincome;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface UserCommUseIncomeRestServiceI extends CommonService{

    //获取用户常用类目标签列表
    Map<String,Object> getListById(String user_info_id) throws InvocationTargetException, IllegalAccessException;

    boolean findByUserInfoIdAndId(String user_info_id, String incomeTypeId);

    void insertCommIncomeType(String user_info_id, IncomeTypeRestEntity task);

    void deleteCommIncomeType(String user_info_id, List<String> incomeTypeIds);
}
