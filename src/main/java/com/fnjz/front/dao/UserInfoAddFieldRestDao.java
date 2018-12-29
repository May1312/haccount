package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userinfo.ConsigneeAddressRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.Map;

/**
 * Created by yhang on 2018/11/28.
 */
@MiniDao
public interface UserInfoAddFieldRestDao {

    /**
     * 获取小程序 openId
     *
     * @param userInfoId
     * @return
     */
    @Sql("select wxapplet_open_id as openid from hbird_user_info_add_field where user_info_id=:userInfoId;")
    String getByUserInfoId(@Param("userInfoId") String userInfoId);

    /**
     * 获取移动端 openId
     *
     * @param userInfoId
     * @return
     */
    @Sql("select wechat_open_id as openid from hbird_user_info_add_field where user_info_id=:userInfoId;")
    String getWechatOpenId(@Param("userInfoId") String userInfoId);

    /**
     * 获取公众号 openId
     *
     * @param userInfoId
     * @return
     */
    @Sql("select official_open_id as openid from hbird_user_info_add_field where user_info_id=:userInfoId;")
    String getOfficialOpenId(@Param("userInfoId") String userInfoId);

    /**
     * 添加小程序openid
     * @param userInfoId
     * @param openId
     */
    @Sql("insert into hbird_user_info_add_field (user_info_id,wxapplet_open_id,create_date) values (:userInfoId,:openId,now());")
    void insertWXAppletOpenId(@Param("userInfoId") String userInfoId, @Param("openId") String openId);

    /**
     * 添加移动应用openid
     * @param userInfoId
     * @param openId
     */
    @Sql("insert into hbird_user_info_add_field (user_info_id,wechat_open_id,create_date) values (:userInfoId,:openId,now());")
    void insertWechatOpenId(@Param("userInfoId") String userInfoId, @Param("openId") String openId);

    /**
     * 添加公众号openid
     * @param userInfoId
     * @param openId
     */
    @Sql("insert into hbird_user_info_add_field (user_info_id,official_open_id,create_date) values (:userInfoId,:openId,now());")
    void insertOfficialOpenId(@Param("userInfoId") String userInfoId, @Param("openId") String openId);

    /**
     * 获取收货地址
     * @param userInfoId
     * @return
     */
    @ResultType(ConsigneeAddressRestDTO.class)
    @Sql("select id,consignee_name,consignee_mobile,consignee_province,consignee_city,consignee_district,consignee_detail from hbird_user_info_add_field where user_info_id=:userInfoId;")
    ConsigneeAddressRestDTO getConsigneeAddress(@Param("userInfoId")String userInfoId);

    /**
     * 判断是否存在 返回主键
     * @param userInfoId
     * @return
     */
    @Sql("select id from hbird_user_info_add_field where user_info_id=:userInfoId;")
    Integer checkExistsByUserInfoId(@Param("userInfoId")String userInfoId);

    /**
     * 判断是否存在小程序openId+id
     * @param userInfoId
     * @return
     */
    @Sql("select id,wxapplet_open_id as openid from hbird_user_info_add_field where user_info_id=:userInfoId;")
    Map<String,Object> checkExistsOpenIdByUserInfoId(@Param("userInfoId")String userInfoId);

    /**
     * 判断是否存在移动应用openId+id
     * flag 1小程序    2移动应用
     * @param userInfoId
     * @return
     */
    @Sql("select id,if(:flag=1,wxapplet_open_id,wechat_open_id) as openid from hbird_user_info_add_field where user_info_id=:userInfoId;")
    Map<String,Object> checkExistsOpenIdByUserInfoIdForWeChat(@Param("userInfoId")String userInfoId,@Param("flag")int flag);

    /**
     * 创建 收货地址
     * @param userInfoId
     * @param bean
     */
    @Sql("insert into hbird_user_info_add_field (user_info_id,consignee_name,consignee_mobile,consignee_province,consignee_city,consignee_district,consignee_detail,create_date) values (:userInfoId,:bean.consigneeName,:bean.consigneeMobile,:bean.consigneeProvince,:bean.consigneeCity,:bean.consigneeDistrict,:bean.consigneeDetail,now());")
    void insertConsigneeAddress(@Param("userInfoId") String userInfoId,@Param("bean") ConsigneeAddressRestDTO bean);

    void updateConsigneeAddress(@Param("bean")ConsigneeAddressRestDTO bean);

    /**
     * 更新小程序 openId
     */
    @Sql("update hbird_user_info_add_field set wxapplet_open_id=:openId where id=:id;")
    void updateWXAppletOpenId(@Param("id")Integer id,@Param("openId")String openId);

    /**
     * 更新移动应用 openId
     */
    @Sql("update hbird_user_info_add_field set wechat_open_id=:openId where id=:id;")
    void updateWechatOpenId(@Param("id")Integer id,@Param("openId")String openId);

    /**
     * 更新公众号 openId
     */
    @Sql("update hbird_user_info_add_field set official_open_id=:openId where id=:id;")
    void updateOfficialOpenId(@Param("id")Integer id,@Param("openId")String openId);
}
