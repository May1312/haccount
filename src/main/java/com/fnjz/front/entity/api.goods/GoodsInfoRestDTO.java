package com.fnjz.front.entity.api.goods;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;

/**   
 * @Title: Entity
 * @Description: 商品
 * @date 2018-10-20 15:12:26
 * @version V1.0   
 *
 */
@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class GoodsInfoRestDTO extends GoodsRestDTO implements java.io.Serializable {

	/**兑换结果图片（276*276）*/
	private String exchangeResultsPicture;
	/**详情页图片（1125*618）*/
	private String detailsPagePicture;
	/**商品介绍*/
	private String goodsIntroduction;
	/**兑换须知*/
	private String exchangeNotice;
	/**描述*/
	private String remark;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  兑换结果图片（276*276）
	 */
	@Column(name ="EXCHANGE_RESULTS_PICTURE",nullable=true,length=256)
	public String getExchangeResultsPicture(){
		return this.exchangeResultsPicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  兑换结果图片（276*276）
	 */
	public void setExchangeResultsPicture(String exchangeResultsPicture){
		this.exchangeResultsPicture = exchangeResultsPicture;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  详情页图片（1125*618）
	 */
	@Column(name ="DETAILS_PAGE_PICTURE",nullable=true,length=1024)
	public String getDetailsPagePicture(){
		return this.detailsPagePicture;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  详情页图片（1125*618）
	 */
	public void setDetailsPagePicture(String detailsPagePicture){
		this.detailsPagePicture = detailsPagePicture;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  商品介绍
	 */
	@Column(name ="GOODS_INTRODUCTION",nullable=true,length=1024)
	public String getGoodsIntroduction(){
		return this.goodsIntroduction;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  商品介绍
	 */
	public void setGoodsIntroduction(String goodsIntroduction){
		this.goodsIntroduction = goodsIntroduction;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  兑换须知
	 */
	@Column(name ="EXCHANGE_NOTICE",nullable=true,length=1024)
	public String getExchangeNotice(){
		return this.exchangeNotice;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  兑换须知
	 */
	public void setExchangeNotice(String exchangeNotice){
		this.exchangeNotice = exchangeNotice;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  描述
	 */
	@Column(name ="REMARK",nullable=true,length=256)
	public String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  描述
	 */
	public void setRemark(String remark){
		this.remark = remark;
	}
}
