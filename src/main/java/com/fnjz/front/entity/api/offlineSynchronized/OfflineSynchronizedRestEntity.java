package com.fnjz.front.entity.api.offlineSynchronized;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 离线同步记录表相关
 * @date 2018-08-29 14:34:56
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_offline_synchronized", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class OfflineSynchronizedRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**终端设备号*/
	private java.lang.String mobileDevice;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**移动端上传同步时间*/
	private java.util.Date synDate;
	/**创建时间*/
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端设备号
	 */
	@Column(name ="MOBILE_DEVICE",nullable=true,length=64)
	public java.lang.String getMobileDevice(){
		return this.mobileDevice;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端设备号
	 */
	public void setMobileDevice(java.lang.String mobileDevice){
		this.mobileDevice = mobileDevice;
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
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  移动端上传同步时间
	 */
	@Column(name ="SYN_DATE",nullable=true)
	public java.util.Date getSynDate(){
		return this.synDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  移动端上传同步时间
	 */
	public void setSynDate(java.util.Date synDate){
		this.synDate = synDate;
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
}
