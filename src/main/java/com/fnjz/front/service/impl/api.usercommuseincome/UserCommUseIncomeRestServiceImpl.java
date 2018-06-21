package com.fnjz.front.service.impl.api.usercommuseincome;

import com.fnjz.front.dao.UserCommUseIncomeRestDao;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("userCommUseIncomeRestService")
@Transactional
public class UserCommUseIncomeRestServiceImpl extends CommonServiceImpl implements UserCommUseIncomeRestServiceI {

    @Autowired
    private UserCommUseIncomeRestDao userCommUseIncomeRestDao;

    //获取用户常用类目标签列表
    public Map<String, Object> getListById(String user_info_id) throws InvocationTargetException, IllegalAccessException {
        //根据优先级顺序排序
        //String hql = "FROM UserCommUseIncomeRestEntity where userInfoId = " + user_info_id + " ORDER BY priority ASC";
        String hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
        List<IncomeTypeRestDTO> list2 = commonDao.findByQueryString(hql2);
        List<IncomeTypeRestDTO> allList = new ArrayList();
        List<IncomeTypeRestDTO> commonList = new ArrayList();
        Map<String, Object> map = new HashMap();
        //if (list2.isEmpty()) {
            //用户常用表为null，返回系统常用
            //String hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
            //List<IncomeTypeRestDTO> list2 = commonDao.findByQueryString(hql2);
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
                        boolean flag = true;
                        for (int j = 0; j < list2.size(); j++) {
                            if (StringUtils.equals(list2.get(i).getId(), list2.get(j).getParentId())) {
                                //封装三级类目
                                IncomeTypeRestDTO jIncome = new IncomeTypeRestDTO();
                                BeanUtils.copyProperties(list2.get(j), jIncome);
                                jIncome.setParentName(income1.getIncomeName());
                                allList.get(index).getIncomeTypeSons().add(jIncome);
                                //判断是否为常用类目
                                /*if (StringUtils.isNotEmpty(list2.get(j).getParentId())) {
                                    if (list2.get(j).getMark() == 1) {
                                        if (flag) {
                                            *//*IncomeTypeRestDTO income2 = new IncomeTypeRestDTO();
                                            BeanUtils.copyProperties(list2.get(i), income2, new String[]{"IncomeTypeSons"});
                                            commonList.add(income2);
                                            //获取当前角标
                                            int index2 = commonList.size() - 1;*//*
                                            IncomeTypeRestDTO income3 = new IncomeTypeRestDTO();
                                            BeanUtils.copyProperties(list2.get(j),income3);
                                            //commonList.get(index2).getIncomeTypeSons().add(income3);
                                            income3.setParentName(income1.getIncomeName());
                                            commonList.add(income3);
                                            flag = false;
                                        } else {
                                            //int index2 = commonList.size() - 1;
                                            IncomeTypeRestDTO income4 = new IncomeTypeRestDTO();
                                            BeanUtils.copyProperties(list2.get(j), income4, new String[]{"IncomeTypeSons"});
                                            //commonList.get(index2).getIncomeTypeSons().add(income4);
                                            income4.setParentName(income1.getIncomeName());
                                            commonList.add(income4);
                                        }
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
        //}
        //用户常用类目获取
        List<IncomeTypeRestDTO> list3 = userCommUseIncomeRestDao.select(user_info_id);
        //获取类目优先级
        //判断是否已存在
        String relation_hql = "from UserCommTypePriorityRestEntity where userInfoId = "+ user_info_id +" AND type = 2";
        UserCommTypePriorityRestEntity u = commonDao.singleResult(relation_hql);
        if(u!=null){
            if(StringUtils.isNotEmpty(u.getRelation())){
                //json字符串转数组
                JSONArray jsonArray = JSONArray.fromObject(u.getRelation());
                for(int i = 0;i<jsonArray.size();i++){
                    Map array_map = (Map)jsonArray.get(i);
                    for(int j = 0;j<list3.size();j++){
                        if(StringUtils.equals(array_map.get("id")+"",list3.get(j).getId())){
                            list3.get(j).setPriority(Integer.valueOf(array_map.get("priority")+""));
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
    public boolean findByUserInfoIdAndId(String user_info_id, String incomeTypeId) {
        String hql = "FROM UserCommUseIncomeRestEntity where userInfoId = " + user_info_id + " and incomeTypeId = '" + incomeTypeId+"'";
        UserCommUseIncomeRestEntity us = (UserCommUseIncomeRestEntity)commonDao.singleResult(hql);
        if(us!=null){
            return true;
        }
        return false;
    }

    @Override
    public void insertCommIncomeType(String user_info_id, IncomeTypeRestEntity task) {
        UserCommUseIncomeRestEntity userCommUseSpendRestEntity = new UserCommUseIncomeRestEntity();
        userCommUseSpendRestEntity.setUserInfoId(Integer.valueOf(user_info_id));
        //TODO 需要设置这么多属性么！！！！！！！
        //设置图标
        if(StringUtils.isNotEmpty(task.getIcon())){
            userCommUseSpendRestEntity.setIcon(task.getIcon());
        }
        //设置三级类目id
        if(StringUtils.isNotEmpty(task.getId())){
            userCommUseSpendRestEntity.setIncomeTypeId(task.getId());
        }
        //设置三级类目名称
        if(StringUtils.isNotEmpty(task.getIncomeName())){
            userCommUseSpendRestEntity.setIncomeTypeName(task.getIncomeName());
        }
        //设置二级类目id
        if(StringUtils.isNotEmpty(task.getParentId())){
            userCommUseSpendRestEntity.setIncomeTypePid(task.getParentId());
        }
        //获取二级类目
        IncomeTypeRestEntity task2 = commonDao.findUniqueByProperty(IncomeTypeRestEntity.class, "id", task.getParentId());
        //设置二级类目名称
        if(StringUtils.isNotEmpty(task2.getIncomeName())){
            userCommUseSpendRestEntity.setIncomeTypePname(task2.getIncomeName());
        }
        commonDao.saveOrUpdate(userCommUseSpendRestEntity);
    }

    /**
     * userCommUseSpendRestDao.delete(user_info_id,spendTypeId);
     * @param user_info_id
     * @param incomeTypeIds
     */
    @Override
    public void deleteCommIncomeType(String user_info_id, List<String> incomeTypeIds) {
        for(int i = 0;i<incomeTypeIds.size();i++){
            userCommUseIncomeRestDao.delete(user_info_id,incomeTypeIds.get(i));
        }
    }
    /**
     * list排序
     * @param list
     * @return
     */
    public static List<IncomeTypeRestDTO> getSortList(List<IncomeTypeRestDTO> list){
        Collections.sort(list, new Comparator<IncomeTypeRestDTO>() {
            @Override
            public int compare(IncomeTypeRestDTO o1, IncomeTypeRestDTO o2) {
                if(o1.getPriority()!=null&&o2.getPriority()!=null){
                    if(o1.getPriority()>o2.getPriority()){
                        return 1;
                    }
                    if(o1.getPriority()==o2.getPriority()){
                        return 0;
                    }
                    return -1;
                }
                return -1;
            }
        });
        return list;
    }
}