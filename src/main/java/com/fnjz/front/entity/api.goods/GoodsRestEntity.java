package com.fnjz.front.entity.api.goods;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.SequenceGenerator;

/**   
 * @Title: Entity
 * @Description: 商品
 * @date 2018-10-20 15:12:26
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_goods", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class GoodsRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**商品名称*/
	private java.lang.String goodsName;
	/**丰丰票值*/
	private java.lang.Integer fengfengTicketValue;
	/**面值*/
	private java.lang.Integer faceValue;
	/**折扣价*/
	private java.lang.Integer discountPrice;
	/**列表图片（453*327）*/
	private java.lang.String listPicture;
	/**兑换结果图片（276*276）*/
	private java.lang.String exchangeResultsPicture;
	/**详情页图片（1125*618）*/
	private java.lang.String detailsPagePicture;
	/**商品介绍*/
	private java.lang.String goodsIntroduction;
	/**兑换须知*/
	private java.lang.String exchangeNotice;
	/**描述*/
	private java.lang.String remark;
	/**上线状态:0_下线 1_上线*/
	private java.lang.Integer status;
	/**已兑换人数*/
	private java.lang.Integer exchangeNumber;
	/**创建时间*/
	private java.util.Date createDate;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public java.lang.Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(java.lang.Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  商品名称
	 */
	@Column(name ="GOODS_NAME",nullable=true,length=64)
	public java.lang.String getGoodsName(){
		return this.goodsName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  商品名称
	 */
	public void setGoodsName(java.lang.String goodsName){
		this.goodsName = goodsName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  丰丰票值
	 */
	@Column(name ="FENGFENG_TICKET_VALUE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getFengfengTicketValue(){
		return this.fengfengTicketValue;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  丰丰票值
	 */
	public void setFengfengTicketValue(java.lang.Integer fengfengTicketValue){
		this.fengfengTicketValue = fengfengTicketValue;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  面值
	 */
	@Column(name ="FACE_VALUE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getFaceValue(){
		return this.faceValue;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  面值
	 */
	public void setFaceValue(java.lang.Integer faceValue){
		this.faceValue = faceValue;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  折扣价
	 */
	@Column(name ="DISCOUNT_PRICE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getDiscountPrice(){
		return this.discountPrice;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  折扣价
	 */
	public void setDiscountPrice(java.lang.Integer discountPrice){
		this.discountPrice = discountPrice;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  列表图片（453*327）
	 */
	@Column(name ="LIST_PICTURE",nullable=true,length=256)
	public java.lang.String getListPicture(){
		return this.listPicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  列表图片（453*327）
	 */
	public void setListPicture(java.lang.String listPicture){
		this.listPicture = listPicture;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  兑换结果图片（276*276）
	 */
	@Column(name ="EXCHANGE_RESULTS_PICTURE",nullable=true,length=256)
	public java.lang.String getExchangeResultsPicture(){
		return this.exchangeResultsPicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  兑换结果图片（276*276）
	 */
	public void setExchangeResultsPicture(java.lang.String exchangeResultsPicture){
		this.exchangeResultsPicture = exchangeResultsPicture;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  详情页图片（1125*618）
	 */
	@Column(name ="DETAILS_PAGE_PICTURE",nullable=true,length=1024)
	public java.lang.String getDetailsPagePicture(){
		return this.detailsPagePicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  详情页图片（1125*618）
	 */
	public void setDetailsPagePicture(java.lang.String detailsPagePicture){
		this.detailsPagePicture = detailsPagePicture;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  商品介绍
	 */
	@Column(name ="GOODS_INTRODUCTION",nullable=true,length=1024)
	public java.lang.String getGoodsIntroduction(){
		return this.goodsIntroduction;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  商品介绍
	 */
	public void setGoodsIntroduction(java.lang.String goodsIntroduction){
		this.goodsIntroduction = goodsIntroduction;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  兑换须知
	 */
	@Column(name ="EXCHANGE_NOTICE",nullable=true,length=1024)
	public java.lang.String getExchangeNotice(){
		return this.exchangeNotice;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  兑换须知
	 */
	public void setExchangeNotice(java.lang.String exchangeNotice){
		this.exchangeNotice = exchangeNotice;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  描述
	 */
	@Column(name ="REMARK",nullable=true,length=256)
	public java.lang.String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  描述
	 */
	public void setRemark(java.lang.String remark){
		this.remark = remark;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  上线状态:0_下线 1_上线
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  上线状态:0_下线 1_上线
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  已兑换人数
	 */
	@Column(name ="EXCHANGE_NUMBER",nullable=true,precision=10,scale=0)
	public java.lang.Integer getExchangeNumber(){
		return this.exchangeNumber;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  已兑换人数
	 */
	public void setExchangeNumber(java.lang.Integer exchangeNumber){
		this.exchangeNumber = exchangeNumber;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建时间
	 */
	@Column(name ="CREATE_DATE",nullable=true)
	public java.util.Date getCreateDate(){
		return this.createDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建时间
	 */
	public void setCreateDate(java.util.Date createDate){
		this.createDate = createDate;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除标记
	 */
	@Column(name ="DELFLAG",nullable=true,precision=10,scale=0)
	public java.lang.Integer getDelflag(){
		return this.delflag;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除标记
	 */
	public void setDelflag(java.lang.Integer delflag){
		this.delflag = delflag;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  删除时间
	 */
	@Column(name ="DEL_DATE",nullable=true)
	public java.util.Date getDelDate(){
		return this.delDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  删除时间
	 */
	public void setDelDate(java.util.Date delDate){
		this.delDate = delDate;
	}
}
