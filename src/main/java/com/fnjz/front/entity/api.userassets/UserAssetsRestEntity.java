package com.fnjz.front.entity.api.userassets;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

/**   
 * @Title: Entity
 * @Description: 用户资产相关
 * @date 2018-10-20 11:11:26
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_assets", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserAssetsRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**资产类型 当类型为1时 此字段有意义*/
	private java.lang.Integer assetsType;
	/**初始时间 当类型为2时  此字段有意义*/
	private java.util.Date initDate;
	/**金额*/
	private BigDecimal money;
	/**类型1:资产类  2:初始时间*/
	private java.lang.Integer type;
	/**更新时间*/
	private java.util.Date updateDate;
	/**创建日期*/
	private java.util.Date createDate;
	
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
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  资产类型 当类型为1时 此字段有意义
	 */
	@Column(name ="ASSETS_TYPE",nullable=true)
	public java.lang.Integer getAssetsType(){
		return this.assetsType;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  资产类型 当类型为1时 此字段有意义
	 */
	public void setAssetsType(java.lang.Integer assetsType){
		this.assetsType = assetsType;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  初始时间 当类型为2时  此字段有意义
	 */
	@Column(name ="INIT_DATE",nullable=true)
	public java.util.Date getInitDate(){
		return this.initDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  初始时间 当类型为2时  此字段有意义
	 */
	public void setInitDate(java.util.Date initDate){
		this.initDate = initDate;
	}
	/**
	 *方法: 取得BigDecimal
	 *@return: BigDecimal  金额
	 */
	@Column(name ="MONEY")
	public BigDecimal getMoney(){
		return this.money;
	}

	/**
	 *方法: 设置BigDecimal
	 *@param: BigDecimal  金额
	 */
	public void setMoney(BigDecimal money){
		this.money = money;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  类型1:资产类  2:初始时间
	 */
	@Column(name ="TYPE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getType(){
		return this.type;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  类型1:资产类  2:初始时间
	 */
	public void setType(java.lang.Integer type){
		this.type = type;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  更新时间
	 */
	@Column(name ="UPDATE_DATE",nullable=true)
	public java.util.Date getUpdateDate(){
		return this.updateDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  更新时间
	 */
	public void setUpdateDate(java.util.Date updateDate){
		this.updateDate = updateDate;
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
