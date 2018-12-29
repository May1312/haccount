package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2018/10/12.
 */
@MiniDao
public interface UserWXQrCodeRestDao {

    /**
     * 新增记录
     * @param userInfoId
     * @param url
     */
    @Sql("INSERT INTO `hbird_user_wx_qr_code` (`user_info_id`,`url`,`status`,`create_date`) VALUES(:userInfoId,:url,:type,NOW());")
    void insert(@Param("userInfoId") String userInfoId,@Param("url") String url,@Param("type")String type);

    /**
     * 获取邀请小程序码   2小程序邀请码    1 爱钱鸭邀请码
     * @param userInfoId
     * @return
     */
    @Sql("SELECT url from `hbird_user_wx_qr_code` where user_info_id=:userInfoId and status=:type;")
    String getInviteQrCode(@Param("userInfoId")String userInfoId,@Param("type")String type);
}
