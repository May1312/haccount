package com.fnjz.front.entity.api.shoppingmallintegralexchange;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
public class ShoppingMallIntegralExchangeRestDTO implements java.io.Serializable {
	/**id*/
	private Long id;

	/**充值类商品  绑定手机号*/
	private String exchangeMobile;
	/**兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败*/
	private Integer status;
	/**视频类会员 兑换码*/
	private String cardCode;
	/**视频类会员有效期*/
	private java.util.Date cardDeadline;
	/**创建日期*/
	private java.util.Date createDate;
	/**商品名称*/
	private String goodsName;
	/**丰丰票值*/
	private Integer fengfengTicketValue;
	/**列表图片（453*327）*/
	private String listPicture;
	/**兑换类型*/
	private Integer type;

	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public Long getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(Long id){
		this.id = id;
	}

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  充值类商品  绑定手机号
	 */
	@Column(name ="EXCHANGE_MOBILE",nullable=true,length=11)
	public String getExchangeMobile(){
		return this.exchangeMobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  充值类商品  绑定手机号
	 */
	public void setExchangeMobile(String exchangeMobile){
		this.exchangeMobile = exchangeMobile;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  兑换状态 定义1：兑换中 2：兑换成功  3：兑换失败
	 */
	public void setStatus(Integer status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  视频类会员 兑换码
	 */
	@Column(name ="CARD_CODE",nullable=true,length=32)
	public String getCardCode(){
		return this.cardCode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  视频类会员 兑换码
	 */
	public void setCardCode(String cardCode){
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

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  商品名称
	 */
	@Column(name ="GOODS_NAME",nullable=true,length=64)
	public String getGoodsName(){
		return this.goodsName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  商品名称
	 */
	public void setGoodsName(String goodsName){
		this.goodsName = goodsName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  丰丰票值
	 */
	@Column(name ="FENGFENG_TICKET_VALUE",nullable=true,precision=10,scale=0)
	public Integer getFengfengTicketValue(){
		return this.fengfengTicketValue;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  丰丰票值
	 */
	public void setFengfengTicketValue(Integer fengfengTicketValue){
		this.fengfengTicketValue = fengfengTicketValue;
	}

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  列表图片（453*327）
	 */
	@Column(name ="LIST_PICTURE",nullable=true,length=256)
	public String getListPicture(){
		return this.listPicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  列表图片（453*327）
	 */
	public void setListPicture(String listPicture){
		this.listPicture = listPicture;
	}
	@Column(name ="TYPE",nullable=true)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
