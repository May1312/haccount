package com.fnjz.front.dao;

import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/6/21.
 */
@MiniDao
public interface UserCommUseSpendRestDao {

    @Sql("SELECT st.id,st.spend_name,st.parent_id,st.icon,st.mark,(CASE st.parent_id WHEN null THEN null ELSE (select ss.spend_name from hbird_spend_type ss where ss.id = st.parent_id) END) as parent_name FROM hbird_user_comm_use_spend uc,hbird_spend_type st WHERE uc.spend_type_id = st.id AND uc.user_info_id = :user_info_id ORDER BY uc.priority ASC ;")
    List<SpendTypeRestDTO> select(@Param("user_info_id") String user_info_id);
    @Sql("DELETE FROM hbird_user_comm_use_spend WHERE user_info_id = :user_info_id AND spend_type_id = :spendTypeId")
    void delete(@Param("user_info_id")String user_info_id, @Param("spendTypeId")String spendTypeId);
}
