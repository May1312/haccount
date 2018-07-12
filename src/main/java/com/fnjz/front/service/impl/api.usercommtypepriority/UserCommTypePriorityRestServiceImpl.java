package com.fnjz.front.service.impl.api.usercommtypepriority;

import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.util.Date;

@Service("userCommTypePriorityRestService")
@Transactional
public class UserCommTypePriorityRestServiceImpl extends CommonServiceImpl implements UserCommTypePriorityRestServiceI {

    @Override
    public void saveOrUpdateRelation(String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
        String hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = " + userCommTypePriorityRestEntity.getType() + "";
        UserCommTypePriorityRestEntity o = commonDao.singleResult(hql);
        if (o != null) {
            String sql = "UPDATE `hbird_account`.`hbird_user_comm_type_priority` SET `type` = " + userCommTypePriorityRestEntity.getType() + ", `relation` = '" + userCommTypePriorityRestEntity.getRelation() + "', `update_date` = NOW() WHERE `id` = " + o.getId() + ";";
            commonDao.updateBySqlString(sql);
        } else {
            userCommTypePriorityRestEntity.setCreateDate(new Date());
            commonDao.saveOrUpdate(userCommTypePriorityRestEntity);
        }
    }
}