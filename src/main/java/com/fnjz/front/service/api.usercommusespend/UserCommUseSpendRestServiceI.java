package com.fnjz.front.service.api.usercommusespend;

import com.fnjz.back.entity.operating.SpendTypeEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import org.jeecgframework.core.common.service.CommonService;

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
    void insertCommSpendType(String user_info_id, SpendTypeEntity task);

    boolean findByUserInfoIdAndId(String user_info_id, String spendTypeId);
}
