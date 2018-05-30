package com.fnjz.front.entity.api.userlogin;

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
 * @Description: 用户登录表相关
 * @date 2018-05-30 14:04:25
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_login", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserLoginRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**手机*/
	private java.lang.String mobile;
	/**邮箱*/
	private java.lang.String email;
	/**密码*/
	private java.lang.String password;
	/**手势密码*/
	private java.lang.String gesturePw;
	/**手势密码状态 0关闭   1打开*/
	private java.lang.String gesturePwType;
	/**登录ip*/
	private java.lang.String loginIp;
	/**微信授权*/
	private java.lang.String wechatAuth;
	/**注册时间*/
	private java.util.Date registerDate;
	/**更新时间*/
	private java.util.Date updateDate;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	
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
	 *@return: java.lang.String  手机
	 */
	@Column(name ="MOBILE",nullable=true,length=11)
	public java.lang.String getMobile(){
		return this.mobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手机
	 */
	public void setMobile(java.lang.String mobile){
		this.mobile = mobile;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  邮箱
	 */
	@Column(name ="EMAIL",nullable=true,length=64)
	public java.lang.String getEmail(){
		return this.email;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  邮箱
	 */
	public void setEmail(java.lang.String email){
		this.email = email;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  密码
	 */
	@Column(name ="PASSWORD",nullable=true,length=64)
	public java.lang.String getPassword(){
		return this.password;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  密码
	 */
	public void setPassword(java.lang.String password){
		this.password = password;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  手势密码
	 */
	@Column(name ="GESTURE_PW",nullable=true,length=32)
	public java.lang.String getGesturePw(){
		return this.gesturePw;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码
	 */
	public void setGesturePw(java.lang.String gesturePw){
		this.gesturePw = gesturePw;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  手势密码状态 0关闭   1打开
	 */
	@Column(name ="GESTURE_PW_TYPE",nullable=true,length=2)
	public java.lang.String getGesturePwType(){
		return this.gesturePwType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码状态 0关闭   1打开
	 */
	public void setGesturePwType(java.lang.String gesturePwType){
		this.gesturePwType = gesturePwType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  登录ip
	 */
	@Column(name ="LOGIN_IP",nullable=true,length=32)
	public java.lang.String getLoginIp(){
		return this.loginIp;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  登录ip
	 */
	public void setLoginIp(java.lang.String loginIp){
		this.loginIp = loginIp;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微信授权
	 */
	@Column(name ="WECHAT_AUTH",nullable=true,length=32)
	public java.lang.String getWechatAuth(){
		return this.wechatAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微信授权
	 */
	public void setWechatAuth(java.lang.String wechatAuth){
		this.wechatAuth = wechatAuth;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  注册时间
	 */
	@Column(name ="REGISTER_DATE",nullable=true)
	public java.util.Date getRegisterDate(){
		return this.registerDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  注册时间
	 */
	public void setRegisterDate(java.util.Date registerDate){
		this.registerDate = registerDate;
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
}
