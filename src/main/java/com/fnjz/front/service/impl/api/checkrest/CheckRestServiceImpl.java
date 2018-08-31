package com.fnjz.front.service.impl.api.checkrest;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.SystemTypeRestDao;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.service.api.checkrest.CheckRestServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * app启动检查类目更新情况
 * Created by yhang on 2018/8/31.
 */
@Service("CheckRestServiceI")
@Transactional
public class CheckRestServiceImpl implements CheckRestServiceI {

    @Autowired
    private SystemTypeRestDao systemTypeRestDao;
    /**
     * 获取系统类目/用户常用类目/类目排序关系接口
     * @return
     */
    @Override
    public Map<String, Object> getSysAndUserSpendAndSynInterval(String userInfoId) {
        //获取系统支出表
        List<SpendTypeRestEntity> allSysSpendType = systemTypeRestDao.getAllSysSpendType();
        //获取系统支出表
        List<IncomeTypeRestEntity> allSysIncomeType = systemTypeRestDao.getAllSysIncomeType();
        //获取用户常用支出类目
        List<UserCommUseSpendRestEntity> allUserCommUseSpendType = systemTypeRestDao.getAllUserCommUseSpendType(userInfoId);
        //获取用户常用收入类目
        List<UserCommUseIncomeRestEntity> allUserCommUseIncomeType = systemTypeRestDao.getAllUserCommUseIncomeType(userInfoId);
        //获取用户常用类目排序关系
        List<UserCommTypePriorityRestEntity> allUserCommUseTypePriority = systemTypeRestDao.getAllUserCommUseTypePriority(userInfoId);
        Map<String,Object> map = new HashMap();
        map.put("allSysSpendType",allSysSpendType);
        map.put("allSysIncomeType",allSysIncomeType);
        map.put("allUserCommUseSpendType",allUserCommUseSpendType);
        map.put("allUserCommUseIncomeType",allUserCommUseIncomeType);
        map.put("allUserCommUseTypePriority",allUserCommUseTypePriority);
        //追加同步时间间隔
        map.put("synInterval",RedisPrefix.SYN_INTERVAL);
        return map;
    }
}
