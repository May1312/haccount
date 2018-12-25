package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userassets.UserAssetsRestDTO;
import com.fnjz.front.entity.api.userassets.UserAssetsRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户固定资产
 * Created by yhang on 2018/10/20.
 */
@MiniDao
public interface UserAssetsRestDao {

    @ResultType(UserAssetsRestDTO.class)
    @Sql("SELECT * FROM hbird_user_assets WHERE user_info_id=:userInfoId and if(:type=1,type=:type and money is not null,type=:type);")
    List<UserAssetsRestDTO> getAssetsAllForDTO(@Param("userInfoId") String userInfoId,@Param("type") Integer type);

    @Sql("SELECT assets_type as at FROM hbird_user_assets WHERE user_info_id =:userInfoId AND type =:type and mark=1;")
    List<Map<String,Integer>> getMarkAssets(@Param("userInfoId") String userInfoId, @Param("type") Integer type);

    @ResultType(UserAssetsRestEntity.class)
    @Sql("SELECT * FROM hbird_user_assets WHERE user_info_id=:userInfoId and type=:type;")
    List<UserAssetsRestEntity> getAssetsAll(@Param("userInfoId") String userInfoId,@Param("type") Integer type);

    @Sql("UPDATE `hbird_user_assets` SET `money`=:money where user_info_id = :userInfoId and assets_type=:assetstype;")
    void updateMoney(@Param("money") BigDecimal money, @Param("userInfoId") String userInfoId, @Param("assetstype") Integer assetstype);

    @Sql("UPDATE `hbird_user_assets` SET `init_date`=:initDate,update_date = now() where user_info_id = :userInfoId and type=2;")
    void updateInitDate(@Param("initDate") String initDate, @Param("userInfoId") String userInfoId);
    /**
     * 获取资产总额
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM(money) from hbird_user_assets where user_info_id=:userInfoId and type=1")
    String getAssetsTotal(@Param("userInfoId") String userInfoId);

    @Sql("insert into hbird_user_assets (`user_info_id`,`init_date`,`type`,`create_date`) values(:userInfoId,:initDate,2,NOW())")
    void insertInitDate(@Param("initDate") Date registerDate,@Param("userInfoId") String userInfoId);

    @Sql("select count(id) from hbird_user_assets where user_info_id=:userInfoId and assets_type=:assetsType")
    int getAssetsByAssetsType(@Param("userInfoId") String userInfoId,@Param("assetsType") Integer assetsType);

    @Sql("insert into hbird_user_assets (`user_info_id`,`assets_type`,`money`,`type`,`create_date`) values(:userInfoId,:assetsType,:money,1,NOW());")
    void insertAssets(@Param("userInfoId") String userInfoId,@Param("assetsType") Integer assetsType,@Param("money") BigDecimal money);
    /**
     * 添加到用户默认账户类型
     */
    @Sql("insert into hbird_user_assets (`user_info_id`,`assets_type`,`type`,`create_date`,`mark`) values(:userInfoId,:at,1,NOW(),1);")
    void addAT2Mark(@Param("userInfoId") String userInfoId,@Param("at") String at);

    @Sql("UPDATE `hbird_user_assets` SET `mark`=:mark where user_info_id = :userInfoId and assets_type=:at;")
    void updateAT2Mark(@Param("userInfoId") String userInfoId,@Param("at") String at,@Param("mark") int mark);
}
