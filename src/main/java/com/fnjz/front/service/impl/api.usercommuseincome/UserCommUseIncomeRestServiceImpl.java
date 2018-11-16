package com.fnjz.front.service.impl.api.usercommuseincome;

import com.alibaba.fastjson.JSON;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserCommUseIncomeRestDao;
import com.fnjz.front.dao.UserCommUseSpendRestDao;
import com.fnjz.front.dao.UserCommUseTypeOfflineCheckRestDao;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("userCommUseIncomeRestService")
@Transactional
public class UserCommUseIncomeRestServiceImpl extends CommonServiceImpl implements UserCommUseIncomeRestServiceI {

    @Autowired
    private UserCommUseIncomeRestDao userCommUseIncomeRestDao;

    @Autowired
    private UserCommUseTypeOfflineCheckRestDao userCommUseTypeOfflineCheckRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserCommUseSpendRestDao userCommUseSpendRestDao;

    @Deprecated
    @Override
    public Map<String, Object> getListById(String userInfoId) {
        String hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
        List<IncomeTypeRestEntity> list2 = commonDao.findByQueryString(hql2);
        List<IncomeTypeRestDTO> allList = new ArrayList();
        Map<String, Object> map = new HashMap();
        if (!list2.isEmpty()) {
            //组合二三级类目 获取所有三级类目
            for (int i = 0; i < list2.size(); i++) {
                if (StringUtils.isEmpty(list2.get(i).getParentId())) {
                    IncomeTypeRestDTO income1 = new IncomeTypeRestDTO();
                    BeanUtils.copyProperties(list2.get(i), income1, new String[]{"IncomeTypeSons"});
                    //二级类目单独封装
                    allList.add(income1);
                    //获取当前角标
                    int index = allList.size() - 1;
                    for (int j = 0; j < list2.size(); j++) {
                        if (StringUtils.equals(list2.get(i).getId(), list2.get(j).getParentId())) {
                            //封装三级类目
                            IncomeTypeRestDTO jIncome = new IncomeTypeRestDTO();
                            BeanUtils.copyProperties(list2.get(j), jIncome);
                            //jIncome.setParentName(income1.getIncomeName());
                            allList.get(index).getIncomeTypeSons().add(jIncome);
                        }
                    }
                }
            }
        }
        //}
        //用户常用类目获取
        List<IncomeTypeRestDTO> list3 = userCommUseIncomeRestDao.select(userInfoId);
        //获取类目优先级
        String relation_hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = 2";
        UserCommTypePriorityRestEntity u = commonDao.singleResult(relation_hql);
        if (u != null) {
            if (StringUtils.isNotEmpty(u.getRelation())) {
                //json字符串转数组
                JSONArray jsonArray = JSONArray.fromObject(u.getRelation());
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map array_map = (Map) jsonArray.get(i);
                    for (int j = 0; j < list3.size(); j++) {
                        if (StringUtils.equals(array_map.get("id") + "", list3.get(j).getId())) {
                            try {
                                list3.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                map.put("allList", allList);
                                map.put("commonList", list3);
                                return map;
                            }
                        }
                    }
                }
            }
            //list排序
            list3 = getSortListForIncome(list3);
        }
        map.put("allList", allList);
        map.put("commonList", list3);
        return map;
    }

    /**
     * 获取系统类目
     *
     * @param type
     * @return
     */
    public List<?> getSysType(String type) {
        String hql2;
        List<IncomeTypeRestEntity> incomeList = new ArrayList<>();
        List<SpendTypeRestEntity> spendList = new ArrayList<>();
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
            incomeList = commonDao.findByQueryString(hql2);
        }else{
            hql2 = "FROM SpendTypeRestDTO where status = 1 ORDER BY priority ASC";
            spendList = commonDao.findByQueryString(hql2);
        }
        List<IncomeTypeRestDTO> incomeAllList = new ArrayList();
        List<SpendTypeRestDTO> spendAllList = new ArrayList();
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            if (incomeList.size()>0) {
                //组合二三级类目 获取所有三级类目
                for (int i = 0; i < incomeList.size(); i++) {
                    if (StringUtils.isEmpty(incomeList.get(i).getParentId())) {
                        IncomeTypeRestDTO income1 = new IncomeTypeRestDTO();
                        BeanUtils.copyProperties(incomeList.get(i), income1, new String[]{"IncomeTypeSons"});
                        //二级类目单独封装
                        incomeAllList.add(income1);
                        //获取当前角标
                        int index = incomeAllList.size() - 1;
                        for (int j = 0; j < incomeList.size(); j++) {
                            if (StringUtils.equals(incomeList.get(i).getId(), incomeList.get(j).getParentId())) {
                                //封装三级类目
                                IncomeTypeRestDTO jIncome = new IncomeTypeRestDTO();
                                BeanUtils.copyProperties(incomeList.get(j), jIncome);
                                //jIncome.setParentName(income1.getIncomeName());
                                incomeAllList.get(index).getIncomeTypeSons().add(jIncome);
                            }
                        }
                    }
                }
            }
            return incomeAllList;
        }else{
            if (spendList.size()>0) {
                //组合二三级类目 获取所有三级类目
                for (int i = 0; i < spendList.size(); i++) {
                    if (StringUtils.isEmpty(spendList.get(i).getParentId())) {
                        SpendTypeRestDTO spend1 = new SpendTypeRestDTO();
                        BeanUtils.copyProperties(spendList.get(i), spend1, new String[]{"SpendTypeSons"});
                        //二级类目单独封装
                        spendAllList.add(spend1);
                        //获取当前角标
                        int index = spendAllList.size() - 1;
                        for (int j = 0; j < spendList.size(); j++) {
                            if (StringUtils.equals(spendList.get(i).getId(), spendList.get(j).getParentId())) {
                                //封装三级类目
                                SpendTypeRestDTO jSpend = new SpendTypeRestDTO();
                                BeanUtils.copyProperties(spendList.get(j), jSpend);
                                //jSpend.setParentName(spend1.getSpendName());
                                spendAllList.get(index).getSpendTypeSons().add(jSpend);
                            }
                        }
                    }
                }
            }
            return spendAllList;
        }
    }

    /**
     * 获取个人常用类目
     *
     * @param userInfoId
     * @return
     */
    @Override
    public List<?> getUserCommUseType(String userInfoId,String type) {
        List<IncomeTypeRestDTO> incomeList = new ArrayList<>();
        List<SpendTypeRestDTO> spendList = new ArrayList<>();
        String relationHql;
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            //用户常用类目获取
            this.checkUserCommUserIncome(userInfoId,type);
            incomeList = userCommUseIncomeRestDao.select(userInfoId);
            //获取类目优先级
            relationHql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = 2";
        }else{
            this.checkUserCommUserIncome(userInfoId,type);
            spendList = userCommUseSpendRestDao.select(userInfoId);
            relationHql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = 1";
        }
        UserCommTypePriorityRestEntity u = commonDao.singleResult(relationHql);
        if (u != null) {
            //区分income spend
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                if (StringUtils.isNotEmpty(u.getRelation())) {
                    //json字符串转数组
                    JSONArray jsonArray = JSONArray.fromObject(u.getRelation());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map array_map = (Map) jsonArray.get(i);
                        for (int j = 0; j < incomeList.size(); j++) {
                            if (StringUtils.equals(array_map.get("id") + "", incomeList.get(j).getId())) {
                                try {
                                    incomeList.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    return incomeList;
                                }
                            }
                        }
                    }
                }
                //list排序
                return getSortListForIncome(incomeList);
            }else{
                if (StringUtils.isNotEmpty(u.getRelation())) {
                    //json字符串转数组
                    JSONArray jsonArray = JSONArray.fromObject(u.getRelation());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map array_map = (Map) jsonArray.get(i);
                        for (int j = 0; j < spendList.size(); j++) {
                            if (StringUtils.equals(array_map.get("id") + "", spendList.get(j).getId())) {
                                try {
                                    spendList.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    return spendList;
                                }
                            }
                        }
                    }
                }
                //list排序
                return getSortListForSpend(spendList);
            }
        }
        //不需排序
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            return incomeList;
        }else{
            return spendList;
        }
    }

    /**
     * 整合系统/个人类目  (type区分  spend/income)
     *
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> getCacheTypes(String userInfoId, String shareCode,String type) {
        //区分type
        List<?> userCommUseType;
        List<?> sysType;
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            //收入类型
            userCommUseType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            sysType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.SYS_INCOME_LABEL_TYPE);
        }else{
            userCommUseType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            sysType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.SYS_SPEND_LABEL_TYPE);
        }
        Map<String, Object> map = new HashMap<>();
        if (userCommUseType.size() > 0 && sysType.size() > 0) {
            map.put("allList", sysType);
            map.put("commonList", userCommUseType);
        } else if(userCommUseType.size() > 0 &&sysType.size() < 1){
            //个人有效  系统失效
            sysType = this.getSysType(type);
            //缓存系统类目
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(sysType), RedisPrefix.SYS_INCOME_LABEL_TYPE);
            }else{
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(sysType), RedisPrefix.SYS_SPEND_LABEL_TYPE);
            }
            map.put("allList", sysType);
            map.put("commonList", userCommUseType);
        }else if(userCommUseType.size() < 1 &&sysType.size() > 0){
            //系统有效   个人失效
            userCommUseType = this.getUserCommUseType(userInfoId,type);
            //缓存个人类目
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            }else{
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            }
            map.put("allList", sysType);
            map.put("commonList", userCommUseType);
        }else{
            //都失效
            sysType = this.getSysType(type);
            userCommUseType = this.getUserCommUseType(userInfoId,type);
            //缓存系统类目
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(sysType), RedisPrefix.SYS_INCOME_LABEL_TYPE);
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            }else{
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(sysType), RedisPrefix.SYS_SPEND_LABEL_TYPE);
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            }
            map.put("allList", sysType);
            map.put("commonList", userCommUseType);
        }
        return map;
    }

    /**
     * 获取个人常用类目  自动缓存
     * @param userInfoId
     * @param shareCode
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> getUserCacheTypes(String userInfoId, String shareCode,String type) {
        //区分type
        List<?> userCommUseType;
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            //收入类型
            userCommUseType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
        }else{
            userCommUseType = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
        }
        Map<String, Object> map = new HashMap<>();
        if (userCommUseType.size() > 0 ) {
            map.put("commonList", userCommUseType);
        } else if(userCommUseType.size() < 1 ){
            //个人失效
            userCommUseType = this.getUserCommUseType(userInfoId,type);
            //缓存个人类目
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            }else{
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(userCommUseType), RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            }
            map.put("commonList", userCommUseType);
        }
        return map;
    }

    @Override
    public boolean findByUserInfoIdAndId(String userInfoId, String incomeTypeId) {
        String hql = "FROM UserCommUseIncomeRestEntity where userInfoId = " + userInfoId + " and incomeTypeId = '" + incomeTypeId + "'";
        UserCommUseIncomeRestEntity us = (UserCommUseIncomeRestEntity) commonDao.singleResult(hql);
        if (us != null) {
            return true;
        }
        return false;
    }

    @Override
    public String insertCommIncomeType(int accountBookId, String userInfoId, IncomeTypeRestEntity task) {
        UserCommUseIncomeRestEntity userCommUseIncomeRestEntity = new UserCommUseIncomeRestEntity();
        userCommUseIncomeRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
        //TODO 需要设置这么多属性么！！！！！！！
        //设置图标
        if (StringUtils.isNotEmpty(task.getIcon())) {
            userCommUseIncomeRestEntity.setIcon(task.getIcon());
        }
        //设置三级类目id
        if (StringUtils.isNotEmpty(task.getId())) {
            userCommUseIncomeRestEntity.setIncomeTypeId(task.getId());
        }
        //设置三级类目名称
        if (StringUtils.isNotEmpty(task.getIncomeName())) {
            userCommUseIncomeRestEntity.setIncomeTypeName(task.getIncomeName());
        }
        //设置二级类目id
        if (StringUtils.isNotEmpty(task.getParentId())) {
            userCommUseIncomeRestEntity.setIncomeTypePid(task.getParentId());
        }
        //获取二级类目
        IncomeTypeRestEntity task2 = commonDao.findUniqueByProperty(IncomeTypeRestEntity.class, "id", task.getParentId());
        //设置二级类目名称
        if (StringUtils.isNotEmpty(task2.getIncomeName())) {
            userCommUseIncomeRestEntity.setIncomeTypePname(task2.getIncomeName());
        }
        //获取当前db优先级
        Integer max = userCommUseIncomeRestDao.getMaxPriority(userCommUseIncomeRestEntity.getUserInfoId());
        if (max != null) {
            userCommUseIncomeRestEntity.setPriority(max + 1);
        } else {
            userCommUseIncomeRestEntity.setPriority(1);
        }
        commonDao.saveOrUpdate(userCommUseIncomeRestEntity);
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(accountBookId, "income_type");
        return version;
    }

    @Override
    public Map<String, Object> insertCommTypeForMap(String shareCode, int accountBookId, String userInfoId, Object obj,String type) {
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            IncomeTypeRestEntity task = (IncomeTypeRestEntity) obj;
            UserCommUseIncomeRestEntity userCommUseIncomeRestEntity = new UserCommUseIncomeRestEntity();
            userCommUseIncomeRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //设置三级类目id
            if (StringUtils.isNotEmpty(task.getId())) {
                userCommUseIncomeRestEntity.setIncomeTypeId(task.getId());
            }
            //获取当前db优先级
            Integer max = userCommUseIncomeRestDao.getMaxPriority(userCommUseIncomeRestEntity.getUserInfoId());
            if (max != null) {
                userCommUseIncomeRestEntity.setPriority(max + 1);
            } else {
                userCommUseIncomeRestEntity.setPriority(1);
            }
            commonDao.saveOrUpdate(userCommUseIncomeRestEntity);
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(accountBookId, "income_type");
            return this.insertOrDeleteType(userInfoId,shareCode,version,type);
        }else{
            SpendTypeRestEntity task = (SpendTypeRestEntity) obj;
            UserCommUseSpendRestEntity userCommUseSpendRestEntity = new UserCommUseSpendRestEntity();
            userCommUseSpendRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //设置三级类目id
            if (StringUtils.isNotEmpty(task.getId())) {
                userCommUseSpendRestEntity.setSpendTypeId(task.getId());
            }
            //获取当前db优先级
            Integer max = userCommUseSpendRestDao.getMaxPriority(userCommUseSpendRestEntity.getUserInfoId());
            if (max != null) {
                userCommUseSpendRestEntity.setPriority(max + 1);
            } else {
                userCommUseSpendRestEntity.setPriority(1);
            }
            commonDao.saveOrUpdate(userCommUseSpendRestEntity);
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(accountBookId, "spend_type");
            return this.insertOrDeleteType(userInfoId,shareCode,version,type);
        }
    }

    /**
     * @param userInfoId
     * @param incomeTypeIds
     */
    @Override
    public String deleteCommIncomeType(int accountBookId, String userInfoId, List<String> incomeTypeIds) {
        for (int i = 0; i < incomeTypeIds.size(); i++) {
            userCommUseIncomeRestDao.delete(userInfoId, incomeTypeIds.get(i));
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(accountBookId, "income_type");
        return version;
    }

    /**
     * @param userInfoId
     * @param typeIds
     */
    @Override
    public Map<String, Object> deleteCommTypeForMap(String shareCode, int accountBookId, String userInfoId, List<String> typeIds,String type) {
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            for (int i = 0; i < typeIds.size(); i++) {
                userCommUseIncomeRestDao.delete(userInfoId, typeIds.get(i));
            }
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(accountBookId, "income_type");
            return this.insertOrDeleteType(userInfoId,shareCode,version,RedisPrefix.INCOME);
        }else{
            for (int i = 0; i < typeIds.size(); i++) {
                userCommUseSpendRestDao.delete(userInfoId, typeIds.get(i));
            }
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(accountBookId, "spend_type");
            return this.insertOrDeleteType(userInfoId,shareCode,version,RedisPrefix.SPEND);
        }
    }

    /**
     * list排序
     *
     * @param list
     * @return
     */
    public static List<IncomeTypeRestDTO> getSortListForIncome(List<IncomeTypeRestDTO> list) {
        Collections.sort(list, new Comparator<IncomeTypeRestDTO>() {
            @Override
            public int compare(IncomeTypeRestDTO o3, IncomeTypeRestDTO o4) {
                if (o3.getPriority() != null && o4.getPriority() != null) {
                    if (o3.getPriority() > o4.getPriority()) {
                        return 1;
                    }
                    if (o3.getPriority().equals(o4.getPriority())) {
                        return 0;
                    }
                    return -1;
                } else if (o3.getPriority() == null && o4.getPriority() != null) {
                    return 1;
                } else if (o3.getPriority() != null && o4.getPriority() == null) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    public static List<SpendTypeRestDTO> getSortListForSpend(List<SpendTypeRestDTO> list) {
        Collections.sort(list, new Comparator<SpendTypeRestDTO>() {
            @Override
            public int compare(SpendTypeRestDTO o1, SpendTypeRestDTO o2) {
                if (o1.getPriority() != null && o2.getPriority() != null) {
                    if (o1.getPriority() > o2.getPriority()) {
                        return 1;
                    }
                    if (o1.getPriority().equals(o2.getPriority())) {
                        return 0;
                    }
                    return -1;
                }else if(o1.getPriority() == null && o2.getPriority() != null){
                    return 1;
                }else if(o1.getPriority() != null && o2.getPriority() == null){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
        return list;
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

    //新增/删除类目 ---->设置缓存，返回Map数据通用方法
     Map<String, Object> insertOrDeleteType(String userInfoId, String shareCode,String version,String type) {
        //重新查询排序关系
        //获取离线-用户常用类目
        List<?> list;
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            list = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
        }else{
            list = redisTemplateUtils.getCacheLabelTypeForList(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
        }
        if (!(list.size() > 0)) {
            list = this.getUserCommUseType(userInfoId,type);
            if(StringUtils.equals(type,RedisPrefix.INCOME)){
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(list), RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            }else{
                redisTemplateUtils.cacheLabelTypeForList(JSON.toJSONString(list), RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("version", version);
        map.put("commonList", list);
        return map;
    }

    /**
     * 为用户分配系统常用标签
     */
    private void checkUserCommUserIncome(String userInfoId,String type){
        //判断用户自有标签是否已存在
        if(StringUtils.equals(type,RedisPrefix.INCOME)){
            int count = userCommUseIncomeRestDao.checkUserCommUserIncome(userInfoId);
            if(count<1){
                String income_sql = "select id,priority from hbird_income_type where status = 1 AND mark = 1";
                List<Map> listbySql2 = commonDao.findListMapbySql(income_sql);
                List<UserCommUseIncomeRestEntity> list_common_income = new ArrayList<>();
                for (int j = 0; j < listbySql2.size(); j++) {
                    UserCommUseIncomeRestEntity userCommUseIncomeRestEntity = new UserCommUseIncomeRestEntity();
                    //设置三级类目id
                    userCommUseIncomeRestEntity.setIncomeTypeId(listbySql2.get(j).get("id") + "");
                    //设置优先级
                    userCommUseIncomeRestEntity.setPriority(Integer.valueOf(listbySql2.get(j).get("priority") + ""));
                    userCommUseIncomeRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                    list_common_income.add(userCommUseIncomeRestEntity);
                }
                commonDao.batchSave(list_common_income);
            }
        }else{
            int count = userCommUseIncomeRestDao.checkUserCommUserSpend(userInfoId);
            if(count<1){
                String spend_sql = "select id,priority from hbird_spend_type where status = 1 AND mark = 1";
                List<Map> listbySql = commonDao.findListMapbySql(spend_sql);
                List<UserCommUseSpendRestEntity> list_common_spend = new ArrayList<>();
                for (int i = 0; i < listbySql.size(); i++) {
                    UserCommUseSpendRestEntity userCommUseSpendRestEntity = new UserCommUseSpendRestEntity();
                    //设置三级类目id
                    userCommUseSpendRestEntity.setSpendTypeId(listbySql.get(i).get("id") + "");
                    //设置优先级
                    userCommUseSpendRestEntity.setPriority(Integer.valueOf(listbySql.get(i).get("priority") + ""));
                    userCommUseSpendRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                    list_common_spend.add(userCommUseSpendRestEntity);
                }
                commonDao.batchSave(list_common_spend);
            }
        }
    }
}