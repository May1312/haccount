package com.fnjz.front.service.api.usercommuseincome;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface UserCommUseIncomeRestServiceI extends CommonService{

    /**
     * 获取用户收入类目标签列表
     */
    Map<String,Object> getListById(String userInfoId);

    /**
     * 查看是否已经是用户常用类目
     * @param userInfoId
     * @param incomeTypeId
     * @return
     */
    boolean findByUserInfoIdAndId(String userInfoId, String incomeTypeId);

    /**
     * 添加用户常用类目
     * @param userInfoId
     * @param task
     */
    void insertCommIncomeType(String userInfoId, IncomeTypeRestEntity task);

    /**
     * 删除用户常用类目
     * @param userInfoId
     * @param incomeTypeIds
     */
    void deleteCommIncomeType(String userInfoId, List<String> incomeTypeIds);
}
