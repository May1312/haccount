package com.fnjz.front.entity.api.usercommusespend;

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
 * @Description: 用户常用支出类目表
 * @date 2018-06-06 13:25:23
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_comm_use_spend", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserCommUseSpendRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**二级类目id*/
	private java.lang.String spendTypePid;
	/**二级栏目名称*/
	private java.lang.String spendTypePname;
	/**三级类目id*/
	private java.lang.String spendTypeId;
	/**三级类目名称*/
	private java.lang.String spendTypeName;
	/**图标*/
	private java.lang.String icon;
	/**优先级*/
	private java.lang.Integer priority;
	
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
	@Column(name ="USER_INFO_ID",nullable=true,precision=10,scale=0)
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级类目id
	 */
	@Column(name ="SPEND_TYPE_PID",nullable=true,length=36)
	public java.lang.String getSpendTypePid(){
		return this.spendTypePid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级类目id
	 */
	public void setSpendTypePid(java.lang.String spendTypePid){
		this.spendTypePid = spendTypePid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级栏目名称
	 */
	@Column(name ="SPEND_TYPE_PNAME",nullable=true,length=64)
	public java.lang.String getSpendTypePname(){
		return this.spendTypePname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级栏目名称
	 */
	public void setSpendTypePname(java.lang.String spendTypePname){
		this.spendTypePname = spendTypePname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目id
	 */
	@Column(name ="SPEND_TYPE_ID",nullable=true,length=36)
	public java.lang.String getSpendTypeId(){
		return this.spendTypeId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目id
	 */
	public void setSpendTypeId(java.lang.String spendTypeId){
		this.spendTypeId = spendTypeId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目名称
	 */
	@Column(name ="SPEND_TYPE_NAME",nullable=true,length=64)
	public java.lang.String getSpendTypeName(){
		return this.spendTypeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目名称
	 */
	public void setSpendTypeName(java.lang.String spendTypeName){
		this.spendTypeName = spendTypeName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图标
	 */
	@Column(name ="ICON",nullable=true,length=255)
	public java.lang.String getIcon(){
		return this.icon;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图标
	 */
	public void setIcon(java.lang.String icon){
		this.icon = icon;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  优先级
	 */
	@Column(name ="PRIORITY",nullable=true,precision=10,scale=0)
	public java.lang.Integer getPriority(){
		return this.priority;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  优先级
	 */
	public void setPriority(java.lang.Integer priority){
		this.priority = priority;
	}
}
