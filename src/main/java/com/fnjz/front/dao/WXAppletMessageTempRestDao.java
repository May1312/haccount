package com.fnjz.front.dao;

import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2018/12/1.
 */
@MiniDao
public interface WXAppletMessageTempRestDao {

    @Sql("insert into hbird_wxapplet_message_temp set (`user_info_id`,`open_id`,`form_id`,`create_date`) values (:bean.userInfoId,:bean.openId,:bean.formId,now());")
    void insert(@Param("bean") WXAppletMessageTempRestEntity bean);
}
