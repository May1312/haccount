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

    @Sql("SELECT st.id,st.income_name,st.parent_id,st.icon,st.mark,(CASE st.parent_id WHEN null THEN null ELSE (select ss.income_name from hbird_income_type ss where ss.id = st.parent_id) END) as parent_name FROM hbird_user_comm_use_income uc,hbird_income_type st WHERE uc.income_type_id = st.id AND uc.user_info_id = :user_info_id ORDER BY uc.priority ASC ;")
    List<IncomeTypeRestDTO> select(@Param("user_info_id") String user_info_id);

    @Sql("DELETE FROM hbird_user_comm_use_income WHERE user_info_id = :user_info_id AND income_type_id = :incomeTypeId")
    void delete(@Param("user_info_id")String user_info_id, @Param("incomeTypeId")String incomeTypeId);
}
