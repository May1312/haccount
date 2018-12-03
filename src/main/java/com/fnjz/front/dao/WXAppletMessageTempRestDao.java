package com.fnjz.front.dao;

import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletAccountNotifyTempRestEntity;
import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/12/1.
 */
@MiniDao
public interface WXAppletMessageTempRestDao {

    @Sql("insert into hbird_wxapplet_message_temp set (`user_info_id`,`open_id`,`form_id`,`create_date`) values (:bean.userInfoId,:bean.openId,:bean.formId,now());")
    void insert(@Param("bean") WXAppletMessageTempRestEntity bean);

    /**
     * 统计月收入支出
     */
    @Sql("INSERT INTO hbird_wxapplet_account_notify_temp ( `user_info_id`, `open_id`, `form_id`, `spend`, `income`, `create_date` ) ( SELECT base4.user_info_id, base4.open_id, base4.form_id, SUM( CASE WHEN base1.order_type = 1 THEN money ELSE 0 END ) AS spend, SUM( CASE WHEN base1.order_type = 2 THEN money ELSE 0 END ) AS income, now( ) FROM `hbird_water_order` AS base1 RIGHT JOIN ( SELECT base2.account_book_id, base3.user_info_id, base3.open_id, base3.form_id FROM hbird_user_account_book AS base2 INNER JOIN hbird_wxapplet_message_temp AS base3 ON base2.user_info_id = base3.user_info_id WHERE base2.delflag = 0 ) AS base4 ON base4.account_book_id = base1.account_book_id AND base4.user_info_id = base1.update_by WHERE base1.charge_date BETWEEN :first AND :end AND base1.delflag = 0 AND base1.update_by = base4.user_info_id GROUP BY base1.update_by );")
    void insert2ForMonth(@Param("first")String first,@Param("end")String end);

    /**
     * 获取待发送账单数据
     */
    @ResultType(WXAppletAccountNotifyTempRestEntity.class)
    @Sql("select user_info_id,open_id,form_id,spend,income from hbird_wxapplet_account_notify_temp;")
    List<WXAppletAccountNotifyTempRestEntity> getAccountNotifyData();

    @Sql("delete from hbird_wxapplet_message_temp;")
    void deleteDate1();

    @Sql("delete from hbird_wxapplet_account_notify_temp;")
    void deleteDate2();
}
