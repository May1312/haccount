package com.fnjz.front.dao;

import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
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
    @ResultType(GoodsRestDTO.class)
    @Sql("select * from hbird_goods where status=1")
    List<GoodsRestDTO> getGoods();

    @ResultType(GoodsInfoRestDTO.class)
    @Sql("select * from hbird_goods where id=:id")
    GoodsInfoRestDTO getGoodsInfoById(@Param("id") Integer id);

    @ResultType(GoodsRestEntity.class)
    @Sql("select * from hbird_goods where id=:id")
    GoodsRestEntity getGoodsById(@Param("id") Integer id);

    @Sql("insert into hbird_shopping_mall_integral_exchange (`id`,`user_info_id`,`goods_id`,`exchange_mobile`,`status`,`count`,`soouu_order_id`,`purchase_price`,`card_code`,`card_deadline`,`description`,`create_date`) values (:shoppingMall.id,:userInfoId,:shoppingMall.goodsId,:shoppingMall.exchangeMobile,:shoppingMall.status,:shoppingMall.count,:shoppingMall.soouuOrderId,:shoppingMall.purchasePrice,:shoppingMall.cardCode,:shoppingMall.cardDeadline,:shoppingMall.description,now());")
    void insert(@Param("shoppingMall") ShoppingMallIntegralExchangeRestEntity shoppingMall,@Param("userInfoId")String userInfoId);
}
