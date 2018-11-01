package com.fnjz.front.entity.api.usersignin;

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
 * @Description: 用户签到表相关
 * @date 2018-10-10 14:23:20
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_sign_in", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserSignInRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**签到日期*/
	private java.util.Date signInDate;
	/**签到状态 1:已签到*/
	private java.lang.Integer status;
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
	 *@return: java.util.Date  签到日期
	 */
	@Column(name ="SIGN_IN_DATE",nullable=true)
	public java.util.Date getSignInDate(){
		return this.signInDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  签到日期
	 */
	public void setSignInDate(java.util.Date signInDate){
		this.signInDate = signInDate;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  签到状态 1:已签到
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  签到状态 1:已签到
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
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
