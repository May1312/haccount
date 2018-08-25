package com.fnjz.front.service.impl.api.usercommusespend;

import com.fnjz.front.dao.UserCommUseSpendRestDao;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.usercommusespend.UserCommUseSpendRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.util.*;

@Service("userCommUseSpendRestService")
@Transactional
public class UserCommUseSpendRestServiceImpl extends CommonServiceImpl implements UserCommUseSpendRestServiceI {

    @Autowired
    private UserCommUseSpendRestDao userCommUseSpendRestDao;

    @Override
    public Map<String, Object> getListById(String userInfoId) {
        //所有类目获取
        String hql = "FROM SpendTypeRestDTO where status = 1 ORDER BY priority ASC";
        List<SpendTypeRestDTO> list2 = commonDao.findByQueryString(hql);
        List<SpendTypeRestDTO> allList = new ArrayList();
        Map<String, Object> map = new HashMap();
        if (!list2.isEmpty()) {
            //组合二三级类目 获取所有三级类目
            for (int i = 0; i < list2.size(); i++) {
                if (StringUtils.isEmpty(list2.get(i).getParentId())) {
                    SpendTypeRestDTO spend1 = new SpendTypeRestDTO();
                    BeanUtils.copyProperties(list2.get(i), spend1, new String[]{"IncomeTypeSons"});
                    //二级类目单独封装
                    allList.add(spend1);
                    //获取当前角标
                    int index = allList.size() - 1;
                    for (int j = 0; j < list2.size(); j++) {
                        if (StringUtils.equals(list2.get(i).getId(), list2.get(j).getParentId())) {
                            //封装三级类目
                            SpendTypeRestDTO spend2 = new SpendTypeRestDTO();
                            BeanUtils.copyProperties(list2.get(j), spend2);
                            spend2.setParentName(spend1.getSpendName());
                            allList.get(index).getSpendTypeSons().add(spend2);
                        }
                    }
                }
            }
        }
        //用户常用类目获取
        List<SpendTypeRestDTO> list3 = userCommUseSpendRestDao.select(userInfoId);
        //获取类目优先级
        //判断是否已存在
        String relation_hql = "from UserCommTypePriorityRestEntity where userInfoId = " + userInfoId + " AND type = 1";
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
            list3 = getSortList(list3);
        }
        map.put("allList", allList);
        map.put("commonList", list3);
        return map;
    }

    @Override
    public void insertCommSpendType(String userInfoId, SpendTypeRestEntity task) {
        UserCommUseSpendRestEntity userCommUseSpendRestEntity = new UserCommUseSpendRestEntity();
        userCommUseSpendRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
        //设置图标
        if (StringUtils.isNotEmpty(task.getIcon())) {
            userCommUseSpendRestEntity.setIcon(task.getIcon());
        }
        //设置三级类目id
        if (StringUtils.isNotEmpty(task.getId())) {
            userCommUseSpendRestEntity.setSpendTypeId(task.getId());
        }
        //设置三级类目名称
        if (StringUtils.isNotEmpty(task.getSpendName())) {
            userCommUseSpendRestEntity.setSpendTypeName(task.getSpendName());
        }
        //设置二级类目id
        if (StringUtils.isNotEmpty(task.getParentId())) {
            userCommUseSpendRestEntity.setSpendTypePid(task.getParentId());
        }
        //获取二级类目
        SpendTypeRestEntity task2 = commonDao.findUniqueByProperty(SpendTypeRestEntity.class, "id", task.getParentId());
        //设置二级类目名称
        if (StringUtils.isNotEmpty(task2.getSpendName())) {
            userCommUseSpendRestEntity.setSpendTypePname(task2.getSpendName());
        }
        //获取当前db优先级
        Integer max = userCommUseSpendRestDao.getMaxPriority(userCommUseSpendRestEntity.getUserInfoId());
        if(max!=null){
            userCommUseSpendRestEntity.setPriority(max+1);
        }else{
            userCommUseSpendRestEntity.setPriority(1);
        }
        commonDao.saveOrUpdate(userCommUseSpendRestEntity);
    }

    @Override
    public boolean findByUserInfoIdAndId(String userInfoId, String spendTypeId) {
        String hql = "FROM UserCommUseSpendRestEntity where userInfoId = " + userInfoId + " and spendTypeId = '" + spendTypeId + "'";
        UserCommUseSpendRestEntity us = (UserCommUseSpendRestEntity) commonDao.singleResult(hql);
        if (us != null) {
            return true;
        }
        return false;
    }

    /**
     * 删除用户常用标签
     *
     * @param userInfoId
     * @param spendTypeIds
     */
    @Override
    public void deleteCommSpendType(String userInfoId, List<String> spendTypeIds) {
        for (int i = 0; i < spendTypeIds.size(); i++) {
            userCommUseSpendRestDao.delete(userInfoId, spendTypeIds.get(i));
        }
    }

    /**
     * list排序
     *
     * @param list
     * @return
     */
    public static List<SpendTypeRestDTO> getSortList(List<SpendTypeRestDTO> list) {
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
}