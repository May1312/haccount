package com.fnjz.front.service.impl.api.usercommuseincome;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userCommUseIncomeRestService")
@Transactional
public class UserCommUseIncomeRestServiceImpl extends CommonServiceImpl implements UserCommUseIncomeRestServiceI {

    //获取用户常用类目标签列表
    public Map<String, Object> getListById(String user_info_id) throws InvocationTargetException, IllegalAccessException {
        //根据优先级顺序排序
        String hql = "FROM UserCommUseIncomeRestEntity where userInfoId = " + user_info_id + " ORDER BY priority ASC";
        List<UserCommUseIncomeRestEntity> list = commonDao.findByQueryString(hql);
        List<IncomeTypeRestDTO> allList = new ArrayList();
        List<IncomeTypeRestDTO> commonList = new ArrayList();
        Map<String, Object> map = new HashMap();
        if (list.isEmpty()) {
            //用户常用表为null，返回系统常用
            String hql2 = "FROM IncomeTypeRestDTO where status = 1 ORDER BY priority ASC";
            List<IncomeTypeRestDTO> list2 = commonDao.findByQueryString(hql2);
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
                                if (StringUtils.isNotEmpty(list2.get(j).getParentId())) {
                                    if (list2.get(j).getMark() == 1) {
                                        if (flag) {
                                            /*IncomeTypeRestDTO income2 = new IncomeTypeRestDTO();
                                            BeanUtils.copyProperties(list2.get(i), income2, new String[]{"IncomeTypeSons"});
                                            commonList.add(income2);
                                            //获取当前角标
                                            int index2 = commonList.size() - 1;*/
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
                                }
                            }
                        }
                    }
                }
            }
        }
        map.put("allList", allList);
        map.put("commonList", commonList);
        return map;
    }
}