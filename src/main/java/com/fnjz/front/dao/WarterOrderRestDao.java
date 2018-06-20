package com.fnjz.front.dao;

import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/6/14.
 */
@MiniDao
public interface WarterOrderRestDao {

    /**
     * 分页查询流水
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(WarterOrderRestDTO.class)
    //@Sql("SELECT * FROM hbird_water_order where account_book_id=:accountBookId AND delflag = 0 AND create_date like concat(:time,'%') ORDER BY create_date DESC")
    //List<WarterOrderRestDTO> findListForPage(@Param("time") String time, @Param("accountBookId") String accountBookId, @Param("curPage") Integer curPage, @Param("pageSize") Integer pageSize);
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.account_book_id=:accountBookId AND wo.delflag = 0 AND wo.create_date like concat(:time,'%') ORDER BY wo.create_date DESC")
    List<WarterOrderRestDTO> findListForPage(@Param("time") String time, @Param("accountBookId") String accountBookId);
    /**
     * 查询总记录数
     * @param time
     * @param accountBookId
     * @return
     */
    @Sql("select count(*) from hbird_water_order where account_book_id=:accountBookId AND delflag = 0 AND create_date like concat(:time,'%')")
    Integer getCount(@Param("time") String time , @Param("accountBookId") String accountBookId);


    Integer update(@Param("warterOrderRestEntity") WarterOrderRestEntity warterOrderRestEntity);
}
