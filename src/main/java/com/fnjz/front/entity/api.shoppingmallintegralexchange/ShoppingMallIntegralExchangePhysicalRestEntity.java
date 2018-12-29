package com.fnjz.front.entity.api.shoppingmallintegralexchange;

import javax.persistence.Column;

/**
 * @Title: Entity
 * @Description: 商城积分兑换记录实物类
 * @date 2018-10-22 20:00:51
 * @version V1.0   
 *
 */
public class ShoppingMallIntegralExchangePhysicalRestEntity extends  ShoppingMallIntegralExchangeRestEntity {

	/**收货人*/
	private String consigneeName;

	/**收货人手机*/
	private String consigneeMobile;

	/**收货省份*/
	private String consigneeProvince;

	/**收货城市*/
	private String consigneeCity;

	/**收货区县*/
	private String consigneeDistrict;

	/**收货地址详情*/
	private String consigneeDetail;
	@Column(name ="CONSIGNEE_NAME")
	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	@Column(name ="CONSIGNEE_MOBILE")
	public String getConsigneeMobile() {
		return consigneeMobile;
	}

	public void setConsigneeMobile(String consigneeMobile) {
		this.consigneeMobile = consigneeMobile;
	}
	@Column(name ="CONSIGNEE_PROVINCE")
	public String getConsigneeProvince() {
		return consigneeProvince;
	}

	public void setConsigneeProvince(String consigneeProvince) {
		this.consigneeProvince = consigneeProvince;
	}
	@Column(name ="CONSIGNEE_CITY")
	public String getConsigneeCity() {
		return consigneeCity;
	}

	public void setConsigneeCity(String consigneeCity) {
		this.consigneeCity = consigneeCity;
	}
	@Column(name ="CONSIGNEE_DISTRICT")
	public String getConsigneeDistrict() {
		return consigneeDistrict;
	}

	public void setConsigneeDistrict(String consigneeDistrict) {
		this.consigneeDistrict = consigneeDistrict;
	}
	@Column(name ="CONSIGNEE_DETAIL")
	public String getConsigneeDetail() {
		return consigneeDetail;
	}

	public void setConsigneeDetail(String consigneeDetail) {
		this.consigneeDetail = consigneeDetail;
	}
}
