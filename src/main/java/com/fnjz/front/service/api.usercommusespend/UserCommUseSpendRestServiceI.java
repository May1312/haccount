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
    void insertCommSpendType(String userInfoId, SpendTypeRestEntity task);

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
    void deleteCommSpendType(String userInfoId, List<String> spendTypeId);
}
