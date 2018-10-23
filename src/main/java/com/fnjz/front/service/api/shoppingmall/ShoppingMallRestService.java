package com.fnjz.front.service.api.shoppingmall;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;

import java.util.List;

/**
 * Created by yhang on 2018/10/20.
 */
public interface ShoppingMallRestService {

    /**
     * 获取可用商品
     * @return
     */
    List<GoodsRestDTO> getGoods();

    /**
     * 根据id获取 商品详情
     * @param id
     * @return
     */
    GoodsInfoRestDTO getGoodsInfoById(Integer id);

    /**
     * 根据id获取 商品详情
     * @param id
     * @return
     */
    GoodsRestEntity getGoodsById(Integer id);

    /**
     * 商品兑换接口
     * @return
     */
    JSONObject toExchange(String exchangeMobile,GoodsRestEntity goodsInfoRestDTO,String userInfoId) throws Exception;
}
