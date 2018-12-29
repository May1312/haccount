package com.fnjz.front.service.api.shoppingmall;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsListRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/10/20.
 */
public interface ShoppingMallRestService {

    /**
     * 获取可用商品
     * @return
     */
    List<GoodsListRestDTO> getGoods();

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
    JSONObject toExchange(Map<String,String> map, GoodsRestEntity goodsInfoRestDTO, String userInfoId) throws Exception;

    /**
     * 树鱼回调接口
     * @param orderNo
     * @param customerOrderNo
     * @param status
     * @param reMark
     */
    void updateExchange(String orderNo, String customerOrderNo, String status, String reMark);

    /**
     * 积分兑换---->历史兑换列表
     * @param userInfoId
     * @return
     */
    List<ShoppingMallIntegralExchangePhysicalRestDTO> historyIntegralExchange(String userInfoId);

    /**
     * 查看用户当前是否存在兑换中商品
     * @param userInfoId
     * @return
     */
    boolean checkExchangeStatus(String userInfoId);
}
