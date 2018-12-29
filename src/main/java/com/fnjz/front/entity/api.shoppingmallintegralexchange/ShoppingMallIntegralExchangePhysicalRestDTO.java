package com.fnjz.front.entity.api.shoppingmallintegralexchange;

import javax.persistence.Column;

/**
 * @Title: Entity
 * @Description: 商城积分兑换记录实物类
 * @date 2018-10-22 20:00:51
 * @version V1.0   
 *
 */

public class ShoppingMallIntegralExchangePhysicalRestDTO extends ShoppingMallIntegralExchangeRestDTO{

	/**商品类型 1:虚拟  2:实物  3:红包*/
	private Integer goodsType;

	/**快递公司*/
	private String expressCompany;

	/**快递单号*/
	private String expressNumber;

	@Column(name ="GOODS_TYPE")
	public Integer getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(Integer goodsType) {
		this.goodsType =goodsType;
	}
	@Column(name ="EXPRESS_COMPANY")
	public String getExpressCompany() {
		return expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}
	@Column(name ="EXPRESS_NUMBER")
	public String getExpressNumber() {
		return expressNumber;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}
}
