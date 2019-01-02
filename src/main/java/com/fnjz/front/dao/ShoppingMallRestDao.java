package com.fnjz.front.dao;

import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsListRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestDTO;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangeRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/10/20.
 */
@MiniDao
public interface ShoppingMallRestDao {
    /**
     * 获取可用商品
     * @return
     */
    @ResultType(GoodsListRestDTO.class)
    @Sql("select * from hbird_goods where status=1 order by priority,create_date")
    List<GoodsListRestDTO> getGoods();

    @ResultType(GoodsRestDTO.class)
    @Sql("UPDATE `hbird_goods` SET `status` = 0 WHERE `id` = :id;")
    void downGoods(@Param("id") Integer id);

    @ResultType(GoodsInfoRestDTO.class)
    @Sql("select * from hbird_goods where id=:id")
    GoodsInfoRestDTO getGoodsInfoById(@Param("id") Integer id);

    @ResultType(GoodsRestEntity.class)
    @Sql("select * from hbird_goods where id=:id")
    GoodsRestEntity getGoodsById(@Param("id") Integer id);

    @Sql("insert into hbird_shopping_mall_integral_exchange (`id`,`user_info_id`,`goods_id`,`exchange_mobile`,`status`,`count`,`soouu_order_id`,`purchase_price`,`card_code`,`card_deadline`,`description`,`create_date`) values (:shoppingMall.id,:userInfoId,:shoppingMall.goodsId,:shoppingMall.exchangeMobile,:shoppingMall.status,:shoppingMall.count,:shoppingMall.soouuOrderId,:shoppingMall.purchasePrice,:shoppingMall.cardCode,:shoppingMall.cardDeadline,:shoppingMall.description,now());")
    void insert(@Param("shoppingMall") ShoppingMallIntegralExchangeRestEntity shoppingMall,@Param("userInfoId")String userInfoId);

    /**
     * 实物类型insert
     * @param shoppingMall
     * @param userInfoId
     */
    @Sql("insert into hbird_shopping_mall_integral_exchange (`id`,`user_info_id`,`goods_id`,`status`,`count`,`consignee_name`,`consignee_mobile`,`consignee_province`,`consignee_city`,`consignee_district`,`consignee_detail`,`create_date`,`exchange_mobile`,`description`) values (:shoppingMall.id,:userInfoId,:shoppingMall.goodsId,:shoppingMall.status,:shoppingMall.count,:shoppingMall.consigneeName,:shoppingMall.consigneeMobile,:shoppingMall.consigneeProvince,:shoppingMall.consigneeCity,:shoppingMall.consigneeDistrict,:shoppingMall.consigneeDetail,now(),:shoppingMall.exchangeMobile,:shoppingMall.description);")
    void insertPhysical(@Param("shoppingMall") ShoppingMallIntegralExchangePhysicalRestEntity shoppingMall, @Param("userInfoId")String userInfoId);

    @ResultType(ShoppingMallIntegralExchangeRestEntity.class)
    @Sql("select * from hbird_shopping_mall_integral_exchange where id = :id")
    ShoppingMallIntegralExchangeRestEntity checkStatusById(@Param("id") String customerOrderNo);

    @Sql("UPDATE `hbird_shopping_mall_integral_exchange` SET `status` = :status WHERE `id` = :id;")
    void update(@Param("id") String customerOrderNo,@Param("status")int status);

    @Sql("select shop.id,shop.exchange_mobile,shop.status,shop.card_code,shop.card_deadline,shop.create_date,goods.goods_name,goods.fengfeng_ticket_value,goods.list_picture,goods.type,goods.goods_type,goods.face_value,shop.express_company,shop.express_number from hbird_shopping_mall_integral_exchange shop LEFT JOIN hbird_goods goods on shop.goods_id=goods.id where shop.user_info_id=:userInfoId order by create_date desc;")
    List<ShoppingMallIntegralExchangePhysicalRestDTO> historyIntegralExchange(@Param("userInfoId") String userInfoId);

    @Sql("select count(id) from hbird_shopping_mall_integral_exchange where user_info_id=:userInfoId and status=1;")
    int checkExchangeStatus(@Param("userInfoId") String userInfoId);
}
