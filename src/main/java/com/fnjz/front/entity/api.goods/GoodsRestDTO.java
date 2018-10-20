package com.fnjz.front.entity.api.goods;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
public class GoodsRestDTO implements java.io.Serializable {
	/**id*/
	private Integer id;
	/**商品名称*/
	private String goodsName;
	/**丰丰票值*/
	private Integer fengfengTicketValue;
	/**面值*/
	private Integer faceValue;
	/**列表图片（453*327）*/
	private String listPicture;

	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(Integer id){
		this.id = id;
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  面值
	 */
	@Column(name ="FACE_VALUE",nullable=true,precision=10,scale=0)
	public Integer getFaceValue(){
		return this.faceValue;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  面值
	 */
	public void setFaceValue(Integer faceValue){
		this.faceValue = faceValue;
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

}
