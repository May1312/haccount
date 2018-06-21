package com.fnjz.front.service.api.usercommusespend;

import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;
import java.util.Map;

public interface UserCommUseSpendRestServiceI extends CommonService{

    /**
     * 获取支出类目
     * @param user_info_id
     * @return
     */
    Map<String,Object> getListById(String user_info_id);

    /**
     * 添加用户常用支出类目
     * @param user_info_id
     * @param task
     * @return
     */
    void insertCommSpendType(String user_info_id, SpendTypeRestEntity task);

    boolean findByUserInfoIdAndId(String user_info_id, String spendTypeId);

    void deleteCommSpendType(String user_info_id, List<String> spendTypeId);
}
