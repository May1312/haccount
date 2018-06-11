package com.fnjz.front.service.impl.api.usercommusespend;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.usercommusespend.UserCommUseSpendRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userCommUseSpendRestService")
@Transactional
public class UserCommUseSpendRestServiceImpl extends CommonServiceImpl implements UserCommUseSpendRestServiceI {

    @Override
    public Map<String, Object> getListById(String user_info_id) {
        //根据优先级顺序排序
        String hql = "FROM UserCommUseSpendRestEntity where userInfoId = " + user_info_id + " ORDER BY priority ASC";
        List<UserCommUseSpendRestEntity> list = commonDao.findByQueryString(hql);
        List<SpendTypeRestEntity> allList = new ArrayList();
        List<SpendTypeRestEntity> commonList = new ArrayList();
        Map<String, Object> map = new HashMap();
        if (list.isEmpty()) {
            //用户常用表为null，返回系统常用
            String hql2 = "FROM SpendTypeRestEntity where status = 1 ORDER BY priority ASC";
            List<SpendTypeRestEntity> list2 = commonDao.findByQueryString(hql2);
            if (!list2.isEmpty()) {
                //组合二三级类目 获取所有三级类目
                for (int i = 0; i < list2.size(); i++) {
                    if (StringUtils.isEmpty(list2.get(i).getParentId())) {
                        SpendTypeRestEntity spend1 = new SpendTypeRestEntity();
                        BeanUtils.copyProperties(list2.get(i), spend1, new String[]{"IncomeTypeSons"});
                        //二级类目单独封装
                        allList.add(spend1);
                        //获取当前角标
                        int index = allList.size() - 1;
                        boolean flag = true;
                        for (int j = 0; j < list2.size(); j++) {
                            if (StringUtils.equals(list2.get(i).getId(), list2.get(j).getParentId())) {
                                //封装三级类目
                                SpendTypeRestEntity spend2 = new SpendTypeRestEntity();
                                BeanUtils.copyProperties(list2.get(j), spend2);
                                allList.get(index).getSpendTypeSons().add(spend2);
                                //判断是否为常用类目
                                if (StringUtils.isNotEmpty(list2.get(j).getParentId())) {
                                    if (list2.get(j).getMark() == 1) {
                                        if (flag) {
                                            SpendTypeRestEntity spend3 = new SpendTypeRestEntity();
                                            BeanUtils.copyProperties(list2.get(i), spend3, new String[]{"IncomeTypeSons"});
                                            commonList.add(spend3);
                                            //获取当前角标
                                            int index2 = commonList.size() - 1;
                                            SpendTypeRestEntity spend4 = new SpendTypeRestEntity();
                                            BeanUtils.copyProperties(list2.get(j),spend4);
                                            commonList.get(index2).getSpendTypeSons().add(spend4);
                                            flag = false;
                                        } else {
                                            int index2 = commonList.size() - 1;
                                            SpendTypeRestEntity spend5 = new SpendTypeRestEntity();
                                            BeanUtils.copyProperties(list2.get(j), spend5, new String[]{"IncomeTypeSons"});
                                            commonList.get(index2).getSpendTypeSons().add(spend5);
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