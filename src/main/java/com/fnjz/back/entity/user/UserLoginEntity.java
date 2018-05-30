package com.fnjz.back.entity.user;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: 用户注册登录信息
 * @author zhangdaihao
 * @date 2018-05-29 15:40:53
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_login", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserLoginEntity implements java.io.Serializable {
	/**id*/
	private Integer id;
	/**手机*/
	private String mobile;
	/**邮箱*/
	private String email;
	/**密码*/
	private String password;
	/**手势密码*/
	private String gesturePw;
	/**手势密码状态 0关闭   1打开*/
	private String gesturePwType;
	/**登录ip*/
	private String loginIp;
	/**微信授权*/
	private String wechatAuth;
	/**注册时间*/
	private Date registerDate;
	/**更新时间*/
	private Date updateDate;
	/**用户详情id*/
	private String userInfoId;

	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */

	@Id
	/*@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")*/
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
	 *@return: java.lang.String  手机
	 */
	@Column(name ="MOBILE",nullable=true,length=11)
	public String getMobile(){
		return this.mobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手机
	 */
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  邮箱
	 */
	@Column(name ="EMAIL",nullable=true,length=64)
	public String getEmail(){
		return this.email;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  邮箱
	 */
	public void setEmail(String email){
		this.email = email;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  密码
	 */
	@Column(name ="PASSWORD",nullable=true,length=64)
	public String getPassword(){
		return this.password;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  密码
	 */
	public void setPassword(String password){
		this.password = password;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  手势密码
	 */
	@Column(name ="GESTURE_PW",nullable=true,length=32)
	public String getGesturePw(){
		return this.gesturePw;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码
	 */
	public void setGesturePw(String gesturePw){
		this.gesturePw = gesturePw;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  手势密码状态 0关闭   1打开
	 */
	@Column(name ="GESTURE_PW_TYPE",nullable=true,length=2)
	public String getGesturePwType(){
		return this.gesturePwType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码状态 0关闭   1打开
	 */
	public void setGesturePwType(String gesturePwType){
		this.gesturePwType = gesturePwType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  登录ip
	 */
	@Column(name ="LOGIN_IP",nullable=true,length=32)
	public String getLoginIp(){
		return this.loginIp;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  登录ip
	 */
	public void setLoginIp(String loginIp){
		this.loginIp = loginIp;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微信授权
	 */
	@Column(name ="WECHAT_AUTH",nullable=true,length=32)
	public String getWechatAuth(){
		return this.wechatAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微信授权
	 */
	public void setWechatAuth(String wechatAuth){
		this.wechatAuth = wechatAuth;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  注册时间
	 */
	@Column(name ="REGISTER_DATE",nullable=true)
	public Date getRegisterDate(){
		return this.registerDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  注册时间
	 */
	public void setRegisterDate(Date registerDate){
		this.registerDate = registerDate;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  更新时间
	 */
	@Column(name ="UPDATE_DATE",nullable=true)
	public Date getUpdateDate(){
		return this.updateDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  更新时间
	 */
	public void setUpdateDate(Date updateDate){
		this.updateDate = updateDate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户详情id
	 */
	@Column(name ="USER_INFO_ID",nullable=false,length=32)
	public String getUserInfoId(){
		return this.userInfoId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户详情id
	 */
	public void setUserInfoId(String userInfoId){
		this.userInfoId = userInfoId;
	}
}
