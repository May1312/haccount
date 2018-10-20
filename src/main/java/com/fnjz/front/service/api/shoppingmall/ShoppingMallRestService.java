package com.fnjz.front.service.api.shoppingmall;

import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;

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
}
