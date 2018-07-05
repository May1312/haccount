package com.fnjz.front.service.impl.api.usercommuseincome;

import com.fnjz.front.dao.UserCommUseIncomeRestDao;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import java.util.*;

@Service("userCommUseIncomeRestService")
@Transactional
public class UserCommUseIncomeRestServiceImpl extends CommonServiceImpl implements UserCommUseIncomeRestServiceI {

    @Autowired
    private UserCommUseIncomeRestDao userCommUseIncomeRestDao;

    @Override
    public Map<String, Object> getListById(String userInfoId) {
        String hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
        List<IncomeTypeRestDTO> list2 = commonDao.findByQueryString(hql2);
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
                            jIncome.setParentName(income1.getIncomeName());
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
                            list3.get(j).setPriority(Integer.valueOf(array_map.get("priority") + ""));
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
    public boolean findByUserInfoIdAndId(String userInfoId, String incomeTypeId) {
        String hql = "FROM UserCommUseIncomeRestEntity where userInfoId = " + userInfoId + " and incomeTypeId = '" + incomeTypeId + "'";
        UserCommUseIncomeRestEntity us = (UserCommUseIncomeRestEntity) commonDao.singleResult(hql);
        if (us != null) {
            return true;
        }
        return false;
    }

    @Override
    public void insertCommIncomeType(String userInfoId, IncomeTypeRestEntity task) {
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
        if(max!=null){
            userCommUseIncomeRestEntity.setPriority(max+1);
        }else{
            userCommUseIncomeRestEntity.setPriority(1);
        }
        commonDao.saveOrUpdate(userCommUseIncomeRestEntity);
    }

    /**
     * @param userInfoId
     * @param incomeTypeIds
     */
    @Override
    public void deleteCommIncomeType(String userInfoId, List<String> incomeTypeIds) {
        for (int i = 0; i < incomeTypeIds.size(); i++) {
            userCommUseIncomeRestDao.delete(userInfoId, incomeTypeIds.get(i));
        }
    }

    /**
     * list排序
     *
     * @param list
     * @return
     */
    public static List<IncomeTypeRestDTO> getSortList(List<IncomeTypeRestDTO> list) {
        Collections.sort(list, new Comparator<IncomeTypeRestDTO>() {
            @Override
            public int compare(IncomeTypeRestDTO o3, IncomeTypeRestDTO o4) {
                if (o3.getPriority() != null && o4.getPriority() != null) {
                    if (o3.getPriority() > o4.getPriority()) {
                        return 1;
                    }
                    if (o3.getPriority().equals(o4.getPriority()) ) {
                        return 0;
                    }
                    return -1;
                }else if(o3.getPriority() == null && o4.getPriority() != null){
                    return 1;
                }else if(o3.getPriority() != null && o4.getPriority() == null){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
        return list;
    }
}