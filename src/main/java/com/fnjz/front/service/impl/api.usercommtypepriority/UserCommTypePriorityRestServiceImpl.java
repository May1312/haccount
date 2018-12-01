package com.fnjz.front.service.impl.api.usercommtypepriority;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserCommUseTypeOfflineCheckRestDao;
import com.fnjz.front.dao.UserPrivateLabelRestDao;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateIncomeLabelRestDTO;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateSpendLabelRestDTO;
import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("userCommTypePriorityRestService")
@Transactional
public class UserCommTypePriorityRestServiceImpl extends CommonServiceImpl implements UserCommTypePriorityRestServiceI {

    @Autowired
    private UserCommUseTypeOfflineCheckRestDao userCommUseTypeOfflineCheckRestDao;

    @Autowired
    private UserCommUseIncomeRestServiceI userCommUseIncomeRestService;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Override
    public String saveOrUpdateRelation(int accountBookId, String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
        String hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = " + userCommTypePriorityRestEntity.getType() + "";
        UserCommTypePriorityRestEntity o = commonDao.singleResult(hql);
        if (o != null) {
            String sql = "UPDATE `hbird_user_comm_type_priority` SET `type` = " + userCommTypePriorityRestEntity.getType() + ", `relation` = '" + userCommTypePriorityRestEntity.getRelation() + "', `update_date` = NOW() WHERE `id` = " + o.getId() + ";";
            commonDao.updateBySqlString(sql);
        } else {
            userCommTypePriorityRestEntity.setCreateDate(new Date());
            commonDao.saveOrUpdate(userCommTypePriorityRestEntity);
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(accountBookId, "type_priority");
        return version;
    }

    @Override
    public Map<String, Object> saveOrUpdateRelationForMap(String shareCode, int accountBookId, String userInfoId, UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
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
        if(userCommTypePriorityRestEntity.getType()==1){
            String version = getTypeVersion(accountBookId, "spend_type");
            //支出
            //获取离线-用户常用类目
            Map<String,Object> map = userCommUseIncomeRestService.getUserCacheTypes(userInfoId,shareCode,RedisPrefix.SPEND);
            map.put("version", version);
            return map;
        }else{
            String version = getTypeVersion(accountBookId, "income_type");
            //收入
            //获取离线-用户常用类目
            Map<String,Object> map = userCommUseIncomeRestService.getUserCacheTypes(userInfoId,shareCode,RedisPrefix.INCOME);
            map.put("version", version);
            return map;
        }
    }

    @Override
    public JSONObject saveOrUpdateRelationForMapv2(UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
        String hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userCommTypePriorityRestEntity.getUserInfoId() + " AND abTypeId = "+userCommTypePriorityRestEntity.getAbTypeId()+" AND type = " + userCommTypePriorityRestEntity.getType() + "";
        UserCommTypePriorityRestEntity o = commonDao.singleResult(hql);
        if (o != null) {
            String sql = "UPDATE `hbird_user_comm_type_priority` SET `type` = " + userCommTypePriorityRestEntity.getType() + ", `relation` = '" + userCommTypePriorityRestEntity.getRelation() + "', `update_date` = NOW() WHERE `id` = " + o.getId() + ";";
            commonDao.updateBySqlString(sql);
        } else {
            userCommTypePriorityRestEntity.setCreateDate(new Date());
            commonDao.saveOrUpdate(userCommTypePriorityRestEntity);
        }
        JSONObject jsonObject = new JSONObject();
        //离线功能 更新用户当前类目版本号
        if(userCommTypePriorityRestEntity.getType()==1){
            String version = getTypeVersion(userCommTypePriorityRestEntity.getUserInfoId()+"");
            //支出
            //获取离线-用户常用类目
            //List<?> userCommUseType = userPrivateLabelRestService.getUserCommUseType(userCommTypePriorityRestEntity.getUserInfoId() + "", RedisPrefix.SPEND, userCommTypePriorityRestEntity.getAbTypeId());
            //用户常用类目获取
            List<UserPrivateSpendLabelRestDTO> spendList = userPrivateLabelRestDao.selectLabelByAbId(userCommTypePriorityRestEntity.getUserInfoId()+"",userCommTypePriorityRestEntity.getAbTypeId(), 1);
            JSONArray jsonArray = JSONArray.parseArray(userCommTypePriorityRestEntity.getRelation());
            for (int i = 0; i < jsonArray.size(); i++) {
                Map array_map = (Map) jsonArray.get(i);
                for (int j = 0; j < spendList.size(); j++) {
                    if (StringUtils.equals(array_map.get("id") + "", spendList.get(j).getId() + "")) {
                        try {
                            spendList.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }//list排序
            Collections.sort(spendList, Comparator.comparing(UserPrivateSpendLabelRestDTO::getPriority));
            jsonObject.put("version", version);
            jsonObject.put("commonList", spendList);
            return jsonObject;
        }else{
            String version = getTypeVersion(userCommTypePriorityRestEntity.getUserInfoId()+"");
            //收入
            //获取离线-用户常用类目
            //List<?> userCommUseType = userPrivateLabelRestService.getUserCommUseType(userCommTypePriorityRestEntity.getUserInfoId() + "", RedisPrefix.INCOME, userCommTypePriorityRestEntity.getAbTypeId());
            //json字符串转数组
            List<UserPrivateIncomeLabelRestDTO> incomeList = userPrivateLabelRestDao.selectLabelByAbId2(userCommTypePriorityRestEntity.getUserInfoId()+"",userCommTypePriorityRestEntity.getAbTypeId(), 1);
            JSONArray jsonArray = JSONArray.parseArray(userCommTypePriorityRestEntity.getRelation());
            for (int i = 0; i < jsonArray.size(); i++) {
                Map array_map = (Map) jsonArray.get(i);
                for (int j = 0; j < incomeList.size(); j++) {
                    if (StringUtils.equals(array_map.get("id") + "", incomeList.get(j).getId() + "")) {
                        try {
                            incomeList.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }//list排序
            Collections.sort(incomeList, Comparator.comparing(UserPrivateIncomeLabelRestDTO::getPriority));
            jsonObject.put("version", version);
            jsonObject.put("commonList", incomeList);
            return jsonObject;
        }
    }

    @Override
    public String saveOrUpdateRelationv2(UserCommTypePriorityRestEntity userCommTypePriorityRestEntity) {
        String hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userCommTypePriorityRestEntity.getUserInfoId() + " AND abTypeId = "+userCommTypePriorityRestEntity.getAbTypeId()+" AND type = " + userCommTypePriorityRestEntity.getType() + "";
        UserCommTypePriorityRestEntity o = commonDao.singleResult(hql);
        if (o != null) {
            String sql = "UPDATE `hbird_user_comm_type_priority` SET `type` = " + userCommTypePriorityRestEntity.getType() + ", `relation` = '" + userCommTypePriorityRestEntity.getRelation() + "', `update_date` = NOW() WHERE `id` = " + o.getId() + ";";
            commonDao.updateBySqlString(sql);
        } else {
            userCommTypePriorityRestEntity.setCreateDate(new Date());
            commonDao.saveOrUpdate(userCommTypePriorityRestEntity);
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(userCommTypePriorityRestEntity.getUserInfoId()+"");
        return version;
    }

    /**
     * 获取用户类目版本公用方法
     *
     * @param accountBookId
     * @param type
     * @return
     */
    private String getTypeVersion(int accountBookId, String type) {
        String accountBookId2 = accountBookId + "";
        String version = userCommUseTypeOfflineCheckRestDao.selectByType(accountBookId2, type);
        if (StringUtils.isNotEmpty(version)) {
            version = "v" + (Integer.valueOf(StringUtils.substring(version, 1)) + 1);
            userCommUseTypeOfflineCheckRestDao.update(accountBookId2, type, version);
        } else {
            //version为null，打上版本号
            userCommUseTypeOfflineCheckRestDao.insert(accountBookId2, type);
            version = "v1";
        }
        return version;
    }

    private String getTypeVersion(String userInfoId) {
        String version = userCommUseTypeOfflineCheckRestDao.getUserCommUseTypeOfflineCheckV2(userInfoId);
        if (StringUtils.isNotEmpty(version)) {
            version = "v" + (Integer.valueOf(StringUtils.substring(version, 1)) + 1);
            userCommUseTypeOfflineCheckRestDao.updatev2(userInfoId, version);
        } else {
            //version为null，打上版本号
            userCommUseTypeOfflineCheckRestDao.insertV2(userInfoId);
            version = "v1";
        }
        return version;
    }
}