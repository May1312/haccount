package com.fnjz.front.service.impl.api.userprivatelabel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserCommUseTypeOfflineCheckRestDao;
import com.fnjz.front.dao.UserPrivateLabelRestDao;
import com.fnjz.front.entity.api.incometype.IncomeTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.*;
import com.fnjz.front.service.api.userprivatelabel.UserPrivateLabelRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yhang on 2018/11/7.
 */
@Service("userPrivateLabelRestService")
@Transactional
public class UserPrivateLabelRestServiceImpl extends CommonServiceImpl implements UserPrivateLabelRestService {

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Autowired
    private UserCommUseTypeOfflineCheckRestDao userCommUseTypeOfflineCheckRestDao;

    @Override
    public JSONObject getCacheLabel(Integer abTypeId, String userInfoId, String shareCode, String type) {
        //根据账本id获取当前账本类型
        //Integer typeId = userPrivateLabelRestDao.getAccountBookTypeIdByABId(abTypeId);
        List userType = redisTemplateUtils.getHashValueV2(RedisPrefix.USER_LABEL + shareCode + ":" + abTypeId, type);
        List sysType = redisTemplateUtils.getHashValueV2(RedisPrefix.SYS_LABEL + abTypeId, type);
        JSONObject map = new JSONObject();
        if (userType != null && sysType != null) {
            map.put("allList", sysType);
            map.put("commonList", userType);
        } else if (userType != null && userType == null) {
            //个人有效  系统失
            List<?> sysType2 = this.getSysType(type, abTypeId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(type, sysType2);
            //缓存系统类目
            redisTemplateUtils.updateForHash(RedisPrefix.SYS_LABEL + abTypeId, jsonObject);
            map.put("allList", sysType2);
            map.put("commonList", userType);
        } else if (userType == null && sysType != null) {
            //系统有效   个人失效
            List<?> userCommUseType = this.getUserCommUseType(userInfoId, type, abTypeId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(type, userCommUseType);
            //缓存个人类目
            redisTemplateUtils.updateForHash(RedisPrefix.USER_LABEL + shareCode + ":" + abTypeId, jsonObject);
            map.put("allList", sysType);
            map.put("commonList", userCommUseType);
        } else {
            //都失效
            List<?> sysType2 = this.getSysType(type, abTypeId);
            List<?> userCommUseType = this.getUserCommUseType(userInfoId, type, abTypeId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(type, userCommUseType);
            //缓存个人类目
            redisTemplateUtils.updateForHash(RedisPrefix.USER_LABEL + shareCode + ":" + abTypeId, jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put(type, sysType2);
            //缓存系统类目
            redisTemplateUtils.updateForHash(RedisPrefix.SYS_LABEL + abTypeId, jsonObject2);
            map.put("allList", sysType2);
            map.put("commonList", userCommUseType);
        }
        return map;
    }

    /**
     * 检查用户是否已添加此标签
     *
     * @param labelId
     * @return
     */
    @Override
    public Integer checkExists(String userInfoId,String abTypeId, String labelId) {
        return userPrivateLabelRestDao.checkExists(userInfoId,abTypeId, labelId);
    }

    @Override
    public JSONObject insertUserPrivateLabelForMap(String shareCode, String abTypeId, String labelId, String userInfoId, String type) {
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            //获取标签详情
            UserPrivateLabelUpdateRestDTO userCommUseIncomeRestEntity = userPrivateLabelRestDao.getLabelInfoForIncome(labelId);
            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
            //绑定用户id
            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //绑定二级id
            userPrivateLabelRestEntity.setTypePid(userCommUseIncomeRestEntity.getTypePid());
            //绑定三级id
            userPrivateLabelRestEntity.setTypeId(userCommUseIncomeRestEntity.getTypeId());
            //绑定二级名称
            userPrivateLabelRestEntity.setTypeName(userCommUseIncomeRestEntity.getTypeName());
            //绑定图标
            userPrivateLabelRestEntity.setIcon(userCommUseIncomeRestEntity.getIcon());
            //标签属性 1:支出 2:收入
            userPrivateLabelRestEntity.setProperty(2);
            //标签类型 1:系统分配  2:用户自建
            userPrivateLabelRestEntity.setType(1);
            //设置状态标签状态 1:有效  0:失效
            userPrivateLabelRestEntity.setStatus(1);
            //绑定账本类型id
            userPrivateLabelRestEntity.setAbTypeId(Integer.valueOf(abTypeId));
            userPrivateLabelRestEntity.setAbTypeLabelId(Integer.valueOf(labelId));
            //获取当前db优先级
            Integer max = userPrivateLabelRestDao.getMaxPriorityForIncome(abTypeId,userInfoId);
            if (max != null) {
                userPrivateLabelRestEntity.setPriority(++max);
            } else {
                userPrivateLabelRestEntity.setPriority(1);
            }
            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(userInfoId);
            return this.insertOrDeleteType(userInfoId, shareCode, version, type,abTypeId);
        } else {
            //获取标签详情
            UserPrivateLabelUpdateRestDTO userCommUseSpendRestEntity = userPrivateLabelRestDao.getLabelInfoForSpend(labelId);
            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
            //绑定用户id
            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //绑定二级id
            userPrivateLabelRestEntity.setTypePid(userCommUseSpendRestEntity.getTypePid());
            //绑定三级id
            userPrivateLabelRestEntity.setTypeId(userCommUseSpendRestEntity.getTypeId());
            //绑定二级名称
            userPrivateLabelRestEntity.setTypeName(userCommUseSpendRestEntity.getTypeName());
            //绑定图标
            userPrivateLabelRestEntity.setIcon(userCommUseSpendRestEntity.getIcon());
            //标签属性 1:支出 2:收入
            userPrivateLabelRestEntity.setProperty(1);
            //标签类型 1:系统分配  2:用户自建
            userPrivateLabelRestEntity.setType(1);
            //设置状态标签状态 1:有效  0:失效
            userPrivateLabelRestEntity.setStatus(1);
            //绑定账本类型id
            userPrivateLabelRestEntity.setAbTypeId(Integer.valueOf(abTypeId));
            userPrivateLabelRestEntity.setAbTypeLabelId(Integer.valueOf(labelId));
            //获取当前db优先级
            Integer max = userPrivateLabelRestDao.getMaxPriorityForSpend(abTypeId,userInfoId);
            if (max != null) {
                userPrivateLabelRestEntity.setPriority(max + 1);
            } else {
                userPrivateLabelRestEntity.setPriority(1);
            }
            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
            //离线功能 更新用户当前类目版本号
            String version = getTypeVersion(userInfoId);
            return this.insertOrDeleteType(userInfoId, shareCode, version, type, abTypeId);
        }
    }

    //新增/删除类目 ---->设置缓存，返回Map数据通用方法
    private JSONObject insertOrDeleteType(String userInfoId, String shareCode, String version, String type, String abTypeId) {
        //重新查询排序关系
        //获取离线-用户常用类目
        List<?> list = this.getUserCommUseType(userInfoId, type, Integer.valueOf(abTypeId));
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            jsonObject.put(type, list);
            redisTemplateUtils.updateForHash(RedisPrefix.USER_LABEL + shareCode + ":" + abTypeId,jsonObject);
        } else {
            jsonObject.put(type, list);
            redisTemplateUtils.updateForHash(RedisPrefix.USER_LABEL + shareCode + ":" + abTypeId, jsonObject);
        }
        JSONObject map = new JSONObject();
        map.put("version", version);
        map.put("commonList", list);
        return map;
    }

    @Override
    public String insertUserPrivateLabelType(String abTypeId, String labelId, String userInfoId,String type) {
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            //获取标签详情
            UserPrivateLabelUpdateRestDTO userCommUseIncomeRestEntity = userPrivateLabelRestDao.getLabelInfoForIncome(labelId);
            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
            //绑定用户id
            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //绑定二级id
            userPrivateLabelRestEntity.setTypePid(userCommUseIncomeRestEntity.getTypePid());
            //绑定三级id
            userPrivateLabelRestEntity.setTypeId(userCommUseIncomeRestEntity.getTypeId());
            //绑定二级名称
            userPrivateLabelRestEntity.setTypeName(userCommUseIncomeRestEntity.getTypeName());
            //绑定图标
            userPrivateLabelRestEntity.setIcon(userCommUseIncomeRestEntity.getIcon());
            //标签属性 1:支出 2:收入
            userPrivateLabelRestEntity.setProperty(2);
            //标签类型 1:系统分配  2:用户自建
            userPrivateLabelRestEntity.setType(1);
            //设置状态标签状态 1:有效  0:失效
            userPrivateLabelRestEntity.setStatus(1);
            //绑定账本类型id
            userPrivateLabelRestEntity.setAbTypeId(Integer.valueOf(abTypeId));
            //
            userPrivateLabelRestEntity.setAbTypeLabelId(Integer.valueOf(labelId));
            //获取当前db优先级
            Integer max = userPrivateLabelRestDao.getMaxPriorityForIncome(abTypeId,userInfoId);
            if (max != null) {
                userPrivateLabelRestEntity.setPriority(++max);
            } else {
                userPrivateLabelRestEntity.setPriority(1);
            }
            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
            //离线功能 更新用户当前类目版本号
            return getTypeVersion(userInfoId);
        } else {
            //获取标签详情
            UserPrivateLabelUpdateRestDTO userCommUseSpendRestEntity = userPrivateLabelRestDao.getLabelInfoForSpend(labelId);
            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
            //绑定用户id
            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
            //绑定二级id
            userPrivateLabelRestEntity.setTypePid(userCommUseSpendRestEntity.getTypePid());
            //绑定三级id
            userPrivateLabelRestEntity.setTypeId(userCommUseSpendRestEntity.getTypeId());
            //绑定二级名称
            userPrivateLabelRestEntity.setTypeName(userCommUseSpendRestEntity.getTypeName());
            //绑定图标
            userPrivateLabelRestEntity.setIcon(userCommUseSpendRestEntity.getIcon());
            //标签属性 1:支出 2:收入
            userPrivateLabelRestEntity.setProperty(1);
            //标签类型 1:系统分配  2:用户自建
            userPrivateLabelRestEntity.setType(1);
            //设置状态标签状态 1:有效  0:失效
            userPrivateLabelRestEntity.setStatus(1);
            //绑定账本类型id
            userPrivateLabelRestEntity.setAbTypeId(Integer.valueOf(abTypeId));
            //
            userPrivateLabelRestEntity.setAbTypeLabelId(Integer.valueOf(labelId));
            //获取当前db优先级
            Integer max = userPrivateLabelRestDao.getMaxPriorityForSpend(abTypeId,userInfoId);
            if (max != null) {
                userPrivateLabelRestEntity.setPriority(max + 1);
            } else {
                userPrivateLabelRestEntity.setPriority(1);
            }
            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
            //离线功能 更新用户当前类目版本号
            return getTypeVersion(userInfoId);
        }
    }

    @Override
    public JSONObject deleteUserPrivateLabelForMap(String shareCode, Map<String, Object> map, String userInfoId, String type) {
        JSONArray labelIds = JSONArray.parseArray(JSON.toJSONString(map.get("labelIds")));
        for (int i = 0; i < labelIds.size(); i++) {
            userPrivateLabelRestDao.delete(map.get("abTypeId") + "", labelIds.get(i) + "");
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(userInfoId);
        return this.insertOrDeleteType(userInfoId, shareCode, version, type, map.get("abTypeId") + "");
    }

    @Override
    public String deleteUserPrivateLabelType(String shareCode, Map<String, Object> map, String userInfoId, String income) {
        JSONArray labelIds = JSONArray.parseArray(JSON.toJSONString(map.get("labelIds")));
        for (int i = 0; i < labelIds.size(); i++) {
            userPrivateLabelRestDao.delete(map.get("abTypeId") + "", labelIds.get(i) + "");
        }
        //离线功能 更新用户当前类目版本号
        String version = getTypeVersion(userInfoId);
        return version;
    }

    /**
     * 获取系统类目   读取账本类型-->标签关联表
     *
     * @param type
     * @return
     */
    private List<?> getSysType(String type, Integer abTypeId) {
        List<IncomeTypeRestEntity> incomeList = new ArrayList<>();
        List<SpendTypeRestEntity> spendList = new ArrayList<>();
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            //获取当前账本类型对应系统三级标签
            incomeList = userPrivateLabelRestDao.listLabelByAbIdForIncome(abTypeId);
        } else {
            //获取当前账本类型对应系统标签
            spendList = userPrivateLabelRestDao.listLabelByAbIdForSpend(abTypeId);
        }
        List<IncomeTypeRestDTO> incomeAllList = new ArrayList();
        List<SpendTypeRestDTO> spendAllList = new ArrayList();
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            Map<String, IncomeTypeRestDTO> jsonObject = new HashMap<>();
            if (incomeList.size() > 0) {
                //二级封装三级
                incomeList.forEach(v -> {
                    if (jsonObject.containsKey(v.getParentId())) {
                        IncomeTypeRestDTO income = new IncomeTypeRestDTO();
                        BeanUtils.copyProperties(v, income);
                        jsonObject.get(v.getParentId()).getIncomeTypeSons().add(income);
                    } else {
                        //根据此二级类目id获取详情
                        IncomeTypeRestDTO dto = userPrivateLabelRestDao.getSystemLabelInfoByParentIdForIncome(v.getParentId());
                        IncomeTypeRestDTO incomeTypeRestDTO = new IncomeTypeRestDTO();
                        BeanUtils.copyProperties(v, incomeTypeRestDTO, new String[]{"IncomeTypeSons"});
                        if (dto.getIncomeTypeSons() == null) {
                            List<IncomeTypeRestDTO> list = new ArrayList<>();
                            dto.setIncomeTypeSons(list);
                        }
                        //将userprivatelabelid  赋予
                        dto.setId(v.getId());
                        IncomeTypeRestDTO income = new IncomeTypeRestDTO();
                        BeanUtils.copyProperties(v, income);
                        dto.getIncomeTypeSons().add(income);
                        jsonObject.put(v.getParentId(), dto);
                    }
                });
            }
            incomeAllList = jsonObject.values().stream().collect(Collectors.toList());
            //顺序
            Collections.sort(incomeAllList, Comparator.comparing(IncomeTypeRestDTO::getPriority));
            return incomeAllList;
        } else {
            Map<String, SpendTypeRestDTO> jsonObject = new HashMap<>();
            if (spendList.size() > 0) {
                //二级封装三级
                spendList.forEach(v -> {
                    if (jsonObject.containsKey(v.getParentId())) {
                        SpendTypeRestDTO spend = new SpendTypeRestDTO();
                        BeanUtils.copyProperties(v, spend);
                        jsonObject.get(v.getParentId()).getSpendTypeSons().add(spend);
                    } else {
                        //根据此二级类目id获取详情
                        SpendTypeRestDTO dto = userPrivateLabelRestDao.getSystemLabelInfoByParentIdForSpend(v.getParentId());
                        SpendTypeRestDTO spendTypeRestDTO = new SpendTypeRestDTO();
                        BeanUtils.copyProperties(v, spendTypeRestDTO, new String[]{"SpendTypeSons"});
                        if (dto.getSpendTypeSons() == null) {
                            List<SpendTypeRestDTO> list = new ArrayList<>();
                            dto.setSpendTypeSons(list);
                        }
                        //将userprivatelabelid  赋予
                        dto.setId(v.getId());
                        SpendTypeRestDTO spend = new SpendTypeRestDTO();
                        BeanUtils.copyProperties(v, spend);
                        dto.getSpendTypeSons().add(spend);
                        jsonObject.put(v.getParentId(), dto);
                    }
                });
            }
            spendAllList = jsonObject.values().stream().collect(Collectors.toList());
            //顺序
            Collections.sort(spendAllList, Comparator.comparing(SpendTypeRestDTO::getPriority));
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
     public List<?> getUserCommUseType(String userInfoId, String type, Integer abTypeId) {
        List<UserPrivateIncomeLabelRestDTO> incomeList = new ArrayList<>();
        List<UserPrivateSpendLabelRestDTO> spendList = new ArrayList<>();
        String relationHql;
        //判断是否为注册用户首次调用
        this.checkUserPrivateLabel(userInfoId, type, abTypeId);
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            //用户常用类目获取
            incomeList = userPrivateLabelRestDao.selectLabelByAbId2(userInfoId,abTypeId, 2);
            //获取类目优先级
            relationHql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND abTypeId = " + abTypeId + " AND type = 2";
        } else {
            spendList = userPrivateLabelRestDao.selectLabelByAbId(userInfoId,abTypeId, 1);
            relationHql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND abTypeId = " + abTypeId + " AND type = 1";
        }
        UserCommTypePriorityRestEntity u = (UserCommTypePriorityRestEntity) commonDao.singleResult(relationHql);
        if (u != null) {
            //区分income spend
            if (StringUtils.equals(type, RedisPrefix.INCOME)) {
                if (StringUtils.isNotEmpty(u.getRelation())) {
                    //json字符串转数组
                    JSONArray jsonArray = JSONArray.parseArray(u.getRelation());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map array_map = (Map) jsonArray.get(i);
                        for (int j = 0; j < incomeList.size(); j++) {
                            if (StringUtils.equals(array_map.get("id") + "", incomeList.get(j).getId() + "")) {
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
                Collections.sort(incomeList, Comparator.comparing(UserPrivateIncomeLabelRestDTO::getPriority));
                return incomeList;
            } else {
                if (StringUtils.isNotEmpty(u.getRelation())) {
                    //json字符串转数组
                    JSONArray jsonArray = JSONArray.parseArray(u.getRelation());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map array_map = (Map) jsonArray.get(i);
                        for (int j = 0; j < spendList.size(); j++) {
                            if (StringUtils.equals(array_map.get("id") + "", spendList.get(j).getId() + "")) {
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
                Collections.sort(spendList, Comparator.comparing(UserPrivateSpendLabelRestDTO::getPriority));
                return spendList;
            }
        }
        //不需排序
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            return incomeList;
        } else {
            return spendList;
        }
    }

    /**
     * 为新注册用户分配系统常用标签
     */
    @Override
    public boolean checkUserPrivateLabel(String userInfoId, String type, Integer abTypeId) {
        //判断用户自有标签是否已存在
        if (StringUtils.equals(type, RedisPrefix.INCOME)) {
            int count = userPrivateLabelRestDao.checkUserPrivateLabelForIncome(userInfoId,abTypeId);
            if (count < 1) {
                List<IncomeTypeLabelIdRestDTO> incomeTypeRestDTOS = userPrivateLabelRestDao.listMarkLabelByAbIdForIncome(abTypeId);
                if (incomeTypeRestDTOS != null) {
                    if (incomeTypeRestDTOS.size() > 0) {
                        incomeTypeRestDTOS.forEach(v -> {
                            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
                            //设置三级类目id
                            userPrivateLabelRestEntity.setTypeId(v.getId());
                            //设置三级类目名称
                            userPrivateLabelRestEntity.setTypeName(v.getIncomeName());
                            //设置二级类目id
                            userPrivateLabelRestEntity.setTypePid(v.getParentId());
                            //设置优先级
                            userPrivateLabelRestEntity.setPriority(v.getPriority());
                            //绑定用户
                            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                            //绑定账本id
                            //userPrivateLabelRestEntity.setAccountBookId(abId);
                            //账本类型id
                            userPrivateLabelRestEntity.setAbTypeId(abTypeId);
                            //图标
                            userPrivateLabelRestEntity.setIcon(v.getIcon());
                            userPrivateLabelRestEntity.setAbTypeLabelId(v.getLabelId());
                            //设置属性  1:支出 2:收入
                            userPrivateLabelRestEntity.setProperty(2);
                            //设置属性  1:系统分配  2:用户自建
                            userPrivateLabelRestEntity.setType(1);
                            //设置属性  1:有效  0:失效
                            userPrivateLabelRestEntity.setStatus(1);
                            //insert
                            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
                        });

                    }
                }
                return false;
            }
            return true;
        } else {
            int count = userPrivateLabelRestDao.checkUserPrivateLabelForSpend(userInfoId,abTypeId);
            if (count < 1) {
                List<SpendTypeLabelIdRestDTO> spendTypeRestDTOS = userPrivateLabelRestDao.listMarkLabelByAbIdForSpend(abTypeId);
                if (spendTypeRestDTOS != null) {
                    if (spendTypeRestDTOS.size() > 0) {
                        spendTypeRestDTOS.forEach(v -> {
                            UserPrivateLabelRestEntity userPrivateLabelRestEntity = new UserPrivateLabelRestEntity();
                            //设置三级类目id
                            userPrivateLabelRestEntity.setTypeId(v.getId());
                            //设置三级类目名称
                            userPrivateLabelRestEntity.setTypeName(v.getSpendName());
                            //设置二级类目id
                            userPrivateLabelRestEntity.setTypePid(v.getParentId());
                            //设置优先级
                            userPrivateLabelRestEntity.setPriority(v.getPriority());
                            //绑定用户
                            userPrivateLabelRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
                            //绑定账本id
                            //userPrivateLabelRestEntity.setAccountBookId(abId);
                            //账本类型id
                            userPrivateLabelRestEntity.setAbTypeId(abTypeId);
                            //图标
                            userPrivateLabelRestEntity.setIcon(v.getIcon());
                            //
                            userPrivateLabelRestEntity.setAbTypeLabelId(v.getLabelId());
                            //设置属性  1:支出 2:收入
                            userPrivateLabelRestEntity.setProperty(1);
                            //设置属性  1:系统分配  2:用户自建
                            userPrivateLabelRestEntity.setType(1);
                            //设置属性  1:有效  0:失效
                            userPrivateLabelRestEntity.setStatus(1);
                            //insert
                            userPrivateLabelRestDao.insert(userPrivateLabelRestEntity);
                        });
                    }
                }
                return false;
            }
            return true;
        }
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

    /**
     * list排序
     *
     * @param list
     * @return
     */
    public static List<UserPrivateLabelRestDTO> getSortList(List<UserPrivateLabelRestDTO> list) {
        Collections.sort(list, new Comparator<UserPrivateLabelRestDTO>() {
            @Override
            public int compare(UserPrivateLabelRestDTO o3, UserPrivateLabelRestDTO o4) {
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
}
