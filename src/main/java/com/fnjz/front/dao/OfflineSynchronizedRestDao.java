package com.fnjz.front.dao;

import com.fnjz.front.entity.api.offlineSynchronized.SynDateRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.Date;

/**
 * Created by yhang on 2018/8/29.
 */
@MiniDao
public interface OfflineSynchronizedRestDao {

    /**
     * 获取最新同步时间
     *
     * @return
     */
    @ResultType(SynDateRestDTO.class)
    @Sql("select syn_date as synDate from hbird_offline_synchronized where mobile_device = :mobileDevice and user_info_id = :userInfoId order by id desc limit 1;")
    SynDateRestDTO getLatelySynDate(@Param("mobileDevice") String mobileDevice, @Param("userInfoId") String userInfoId);

    /**
     * 新增同步记录
     * @param mobileDevice
     * @param userInfoId
     */
    @Sql("INSERT INTO `hbird_offline_synchronized` (`mobile_device`,`user_info_id`,`syn_date`,`create_date`) VALUES (:mobileDevice,:userInfoId,NOW(),NOW())")
    void insert(@Param("mobileDevice")String mobileDevice, @Param("userInfoId")String userInfoId);

    /**
     * 第一次同步设置 同步时间
     * @param mobileDevice
     * @param userInfoId
     */
    @Sql("INSERT INTO `hbird_offline_synchronized` (`mobile_device`,`user_info_id`,`syn_date`,`create_date`) VALUES (:mobileDevice,:userInfoId,:synDate,NOW())")
    void firstInsert(@Param("mobileDevice")String mobileDevice, @Param("userInfoId")String userInfoId,@Param("synDate") Date synDate);
}
