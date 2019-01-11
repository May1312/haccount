package com.fnjz.front.entity.api.shoppingmallintegralexchange;

/**
 * 商城头部 商品兑换播报
 */
public class ReportShopRestDTO{

	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 对应数值
	 */
	private Object value;

	/**
	 * 对应数值
	 */
	private Object value2;


	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}
}
