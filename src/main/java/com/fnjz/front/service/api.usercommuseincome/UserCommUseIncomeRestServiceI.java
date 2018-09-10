package com.fnjz.front.service.api.usercommuseincome;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

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
     * 添加用户常用类目 返回版本
     * @param userInfoId
     * @param task
     */
    String insertCommIncomeType(int accountBookId,String userInfoId, IncomeTypeRestEntity task);

    /**
     * 添加用户常用类目 返回版本,新数据数组
     * @param userInfoId
     * @param task
     */
    Map<String,Object> insertCommIncomeTypeForMap(String shareCode,int accountBookId,String userInfoId, IncomeTypeRestEntity task);

    /**
     * 删除用户常用类目
     * @param userInfoId
     * @param incomeTypeIds
     */
    String deleteCommIncomeType(int accountBookId,String userInfoId, List<String> incomeTypeIds);

    /**
     * 删除用户常用类目 返回版本,新数据数组
     * @param userInfoId
     * @param incomeTypeIds
     */
    Map<String,Object> deleteCommIncomeTypeForMap(String shareCode,int accountBookId,String userInfoId, List<String> incomeTypeIds);
}
