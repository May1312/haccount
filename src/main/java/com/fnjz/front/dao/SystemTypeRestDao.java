package com.fnjz.front.dao;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/8/31.
 */
@MiniDao
public interface SystemTypeRestDao {

    /**
     * 获取所有系统支出类目
     * @return
     */
    @ResultType(SpendTypeRestEntity.class)
    @Sql("select * from hbird_spend_type where status=1")
    List<SpendTypeRestEntity> getAllSysSpendType();

    /**
     * 获取所有系统收入类目
     * @return
     */
    @ResultType(IncomeTypeRestEntity.class)
    @Sql("select * from hbird_income_type where status=1")
    List<IncomeTypeRestEntity> getAllSysIncomeType();

    /**
     * 获取用户常用支出类目
     * @return
     */
    @ResultType(UserCommUseSpendRestEntity.class)
    @Sql("select * from hbird_user_comm_use_spend where user_info_id=:userInfoId")
    List<UserCommUseSpendRestEntity> getAllUserCommUseSpendType(@Param("userInfoId")String userInfoId);

    /**
     * 获取用户常用收入类目
     * @return
     */
    @ResultType(UserCommUseIncomeRestEntity.class)
    @Sql("select * from hbird_user_comm_use_income where user_info_id=:userInfoId")
    List<UserCommUseIncomeRestEntity> getAllUserCommUseIncomeType(@Param("userInfoId")String userInfoId);

    /**
     * 获取用户常用类目排序关系
     * @return
     */
    @ResultType(UserCommTypePriorityRestEntity.class)
    @Sql("select * from hbird_user_comm_type_priority where user_info_id=:userInfoId")
    List<UserCommTypePriorityRestEntity> getAllUserCommUseTypePriority(@Param("userInfoId")String userInfoId);
}
