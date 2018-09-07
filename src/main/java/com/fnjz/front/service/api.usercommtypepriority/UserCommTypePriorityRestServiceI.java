package com.fnjz.front.service.api.usercommtypepriority;

import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface UserCommTypePriorityRestServiceI extends CommonService{

    String saveOrUpdateRelation(int accountBookId,String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity);
}
