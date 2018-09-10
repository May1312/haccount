package com.fnjz.front.service.api.usercommusespend;

import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;
import java.util.Map;

public interface UserCommUseSpendRestServiceI extends CommonService{

    /**
     * 获取用户支出类目标签列表
     * @param userInfoId
     * @return
     */
    Map<String,Object> getListById(String userInfoId);

    /**
     * 添加用户常用支出类目
     * @param userInfoId
     * @param task
     * @return
     */
    String insertCommSpendType(int accountBookId,String userInfoId, SpendTypeRestEntity task);

    /**
     * 添加用户常用支出类目 返回版本和新数据数组
     * @param userInfoId
     * @param task
     * @return
     */
    Map<String,Object> insertCommSpendTypeForMap(String shareCode,int accountBookId,String userInfoId, SpendTypeRestEntity task);

    /**
     * 查看是否已经是用户常用类目
     * @param userInfoId
     * @param spendTypeId
     * @return
     */
    boolean findByUserInfoIdAndId(String userInfoId, String spendTypeId);

    /**
     * 删除用户常用类目
     * @param userInfoId
     * @param spendTypeId
     */
    String deleteCommSpendType(int accountBookId,String userInfoId, List<String> spendTypeId);

    /**
     * 删除用户常用类目 返回版本，新数据集合
     * @param userInfoId
     * @param spendTypeId
     */
    Map<String,Object> deleteCommSpendTypeForMap(String shareCode,int accountBookId,String userInfoId, List<String> spendTypeId);
}
