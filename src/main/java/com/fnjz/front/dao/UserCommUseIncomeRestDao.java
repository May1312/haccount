package com.fnjz.front.dao;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/6/21.
 */
@MiniDao
public interface UserCommUseIncomeRestDao {

    /**
     * 用户所有常用收入类目获取
     * @param userInfoId
     * @return
     */
    @Sql("SELECT st.id,st.income_name,st.parent_id,st.icon,st.mark,(CASE st.parent_id WHEN null THEN null ELSE (select ss.income_name from hbird_income_type ss where ss.id = st.parent_id) END) as parent_name FROM hbird_user_comm_use_income uc,hbird_income_type st WHERE uc.income_type_id = st.id AND uc.user_info_id = :user_info_id ORDER BY uc.priority ASC ;")
    List<IncomeTypeRestDTO> select(@Param("user_info_id") String userInfoId);

    /**
     * 删除用户常用收入类目
     * @param userInfoId
     * @param incomeTypeId
     */
    @Sql("DELETE FROM hbird_user_comm_use_income WHERE user_info_id = :user_info_id AND income_type_id = :incomeTypeId")
    void delete(@Param("user_info_id")String userInfoId, @Param("incomeTypeId")String incomeTypeId);

    /**
     * 获取用户常用收入类目最大优先级数
     * @param userInfoId
     * @return
     */
    @Sql("SELECT MAX(priority) FROM hbird_user_comm_use_income where user_info_id = :userInfoId;")
    Integer getMaxPriority(@Param("userInfoId")Integer userInfoId);
}
