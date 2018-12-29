package com.fnjz.front.entity.api.goods;

/**
 * Created by yhang on 2018/12/26.
 */
public class GoodsListRestDTO extends GoodsRestDTO {

    /**商品类型 1:虚拟  2:实物  3:红包*/
    private Integer goodsType;

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }
}
