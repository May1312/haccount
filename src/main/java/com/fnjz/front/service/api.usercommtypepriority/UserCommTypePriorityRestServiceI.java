package com.fnjz.front.service.api.usercommtypepriority;

import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface UserCommTypePriorityRestServiceI extends CommonService{

    /**
     * 返回更新的类目版本号
     * @param accountBookId
     * @param userInfoId
     * @param userCommTypePriorityRestEntity
     * @return
     */
    String saveOrUpdateRelation(int accountBookId,String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity);

    /**
     * 返回更新的类目版本号,新排序数据集合
     * @param accountBookId
     * @param userInfoId
     * @param userCommTypePriorityRestEntity
     * @return
     */
    Map<String,Object> saveOrUpdateRelationForMap(String shareCode, int accountBookId, String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity);

}
