package com.fnjz.front.service.impl.api.usercommtypepriority;

import com.fnjz.front.dao.UserCommUseTypeOfflineCheckRestDao;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("userCommTypePriorityRestService")
@Transactional
public class UserCommTypePriorityRestServiceImpl extends CommonServiceImpl implements UserCommTypePriorityRestServiceI {

    @Autowired
    private UserCommUseTypeOfflineCheckRestDao userCommUseTypeOfflineCheckRestDao;

    @Override
    public String saveOrUpdateRelation(int accountBookId,String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
        String hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = " + userCommTypePriorityRestEntity.getType() + "";
        UserCommTypePriorityRestEntity o = commonDao.singleResult(hql);
        if (o != null) {
            String sql = "UPDATE `hbird_account`.`hbird_user_comm_type_priority` SET `type` = " + userCommTypePriorityRestEntity.getType() + ", `relation` = '" + userCommTypePriorityRestEntity.getRelation() + "', `update_date` = NOW() WHERE `id` = " + o.getId() + ";";
            commonDao.updateBySqlString(sql);
        } else {
            userCommTypePriorityRestEntity.setCreateDate(new Date());
            commonDao.saveOrUpdate(userCommTypePriorityRestEntity);
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(accountBookId, "type_priority");
        return version;
    }

    /**
     * 获取用户类目版本公用方法
     * @param accountBookId
     * @param type
     * @return
     */
    private String getTypeVersion(int accountBookId,String type){
        String accountBookId2 = accountBookId+"";
        String version = userCommUseTypeOfflineCheckRestDao.selectByType(accountBookId2, type);
        if(StringUtils.isNotEmpty(version)){
            version = "v"+(Integer.valueOf(StringUtils.substring(version,1))+1);
            userCommUseTypeOfflineCheckRestDao.update(accountBookId2,type,version);
        }else{
            //version为null，打上版本号
            userCommUseTypeOfflineCheckRestDao.insert(accountBookId2,type);
            version = "v1";
        }
        return version;
    }
}