package com.fnjz.front.entity.api.shoppingmallintegralexchange;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

/**   
 * @Title: Entity
 * @Description: 商城积分兑换记录表相关
 * @date 2018-10-22 20:00:51
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_shopping_mall_integral_exchange", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class ShoppingMallIntegralExchangeRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Long id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**商品id*/
	private java.lang.Integer goodsId;
	/**充值类商品  绑定手机号*/
	private java.lang.String exchangeMobile;
	/**兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败*/
	private java.lang.Integer status;
	/**兑换数量*/
	private java.lang.Integer count;
	/**树鱼订单号*/
	private java.lang.Integer soouuOrderId;
	/**树鱼商品进价*/
	private BigDecimal purchasePrice;
	/**视频类会员 兑换码*/
	private java.lang.String cardCode;
	/**视频类会员有效期*/
	private java.util.Date cardDeadline;
	/**错误描述*/
	private java.lang.String description;
	/**创建日期*/
	private java.util.Date createDate;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public java.lang.Long getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(java.lang.Long id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  用户详情id
	 */
	@Column(name ="USER_INFO_ID",nullable=false,precision=10,scale=0)
	public java.lang.Integer getUserInfoId(){
		return this.userInfoId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  用户详情id
	 */
	public void setUserInfoId(java.lang.Integer userInfoId){
		this.userInfoId = userInfoId;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  商品id
	 */
	@Column(name ="GOODS_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getGoodsId(){
		return this.goodsId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  商品id
	 */
	public void setGoodsId(java.lang.Integer goodsId){
		this.goodsId = goodsId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  充值类商品  绑定手机号
	 */
	@Column(name ="EXCHANGE_MOBILE",nullable=true,length=11)
	public java.lang.String getExchangeMobile(){
		return this.exchangeMobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  充值类商品  绑定手机号
	 */
	public void setExchangeMobile(java.lang.String exchangeMobile){
		this.exchangeMobile = exchangeMobile;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  兑换数量
	 */
	@Column(name ="COUNT",nullable=true,precision=10,scale=0)
	public java.lang.Integer getCount(){
		return this.count;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  兑换数量
	 */
	public void setCount(java.lang.Integer count){
		this.count = count;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  树鱼订单号
	 */
	@Column(name ="SOOUU_ORDER_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getSoouuOrderId(){
		return this.soouuOrderId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  树鱼订单号
	 */
	public void setSoouuOrderId(java.lang.Integer soouuOrderId){
		this.soouuOrderId = soouuOrderId;
	}
	/**
	 *方法: 取得BigDecimal
	 *@return: BigDecimal  树鱼商品进价
	 */
	@Column(name ="PURCHASE_PRICE",nullable=true,precision=10,scale=2)
	public BigDecimal getPurchasePrice(){
		return this.purchasePrice;
	}

	/**
	 *方法: 设置BigDecimal
	 *@param: BigDecimal  树鱼商品进价
	 */
	public void setPurchasePrice(BigDecimal purchasePrice){
		this.purchasePrice = purchasePrice;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  视频类会员 兑换码
	 */
	@Column(name ="CARD_CODE",nullable=true,length=32)
	public java.lang.String getCardCode(){
		return this.cardCode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  视频类会员 兑换码
	 */
	public void setCardCode(java.lang.String cardCode){
		this.cardCode = cardCode;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  视频类会员有效期
	 */
	@Column(name ="CARD_DEADLINE",nullable=true)
	public java.util.Date getCardDeadline(){
		return this.cardDeadline;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  视频类会员有效期
	 */
	public void setCardDeadline(java.util.Date cardDeadline){
		this.cardDeadline = cardDeadline;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  错误描述
	 */
	@Column(name ="DESCRIPTION",nullable=true,length=255)
	public java.lang.String getDescription(){
		return this.description;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  错误描述
	 */
	public void setDescription(java.lang.String description){
		this.description = description;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建日期
	 */
	@Column(name ="CREATE_DATE",nullable=true)
	public java.util.Date getCreateDate(){
		return this.createDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建日期
	 */
	public void setCreateDate(java.util.Date createDate){
		this.createDate = createDate;
	}
}
