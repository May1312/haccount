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
     * 获取系统收入类目
     * @param userInfoId
     * @return
     */
    //Map<String,Object> getSysIncomeType(String userInfoId);

    /**
     * 获取个人常用收入类目  查库
     * @param userInfoId
     * @return
     */
    List<?> getUserCommUseType(String userInfoId,String type);

    /**
     * 获取个人常用收入类目  查库+缓存
     * @param userInfoId
     * @return
     */
    Map<String, Object> getUserCacheTypes(String userInfoId,String shareCode,String type);

    /**
     * 获取系统/个人常用类目
     * @param userInfoId
     * @return
     */
    Map<String,Object> getCacheTypes(String userInfoId,String shareCode,String type);

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
     * @param
     */
    Map<String,Object> insertCommTypeForMap(String shareCode, int accountBookId, String userInfoId, Object obj, String type);

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
    Map<String,Object> deleteCommTypeForMap(String shareCode,int accountBookId,String userInfoId, List<String> incomeTypeIds,String type);
}
