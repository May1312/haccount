package com.fnjz.back.entity.api.userinfo;

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
 * @Description: 移动端用户登录表
 * @date 2018-05-29 20:02:31
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_info", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserInfoEntity implements java.io.Serializable {
	/**蜂鸟ID*/
	private java.lang.Integer id;
	/**昵称*/
	private java.lang.String nickName;
	/**手机*/
	private java.lang.String mobile;
	/**邮箱*/
	private java.lang.String email;
	/**性别*/
	private java.lang.String sex;
	/**出生年月日*/
	private java.util.Date birthday;
	/**密码*/
	private java.lang.String password;
	/**手势密码*/
	private java.lang.String gesturePw;
	/**手势密码打开关闭状态 0关闭  1打开*/
	private java.lang.String gesturePwType;
	/**微信授权token*/
	private java.lang.String wechatAuth;
	/**微博授权token*/
	private java.lang.String weiboAuth;
	/**省份_id*/
	private java.lang.String provinceId;
	/**省份_value*/
	private java.lang.String provinceName;
	/**城市_id*/
	private java.lang.String cityId;
	/**城市_name*/
	private java.lang.String cityName;
	/**区县_id*/
	private java.lang.String districtId;
	/**区县_name*/
	private java.lang.String districtName;
	/**账户状态*/
	private java.lang.String status;
	/**用户类型,vip*/
	private java.lang.String userType;
	/**用户所属行业*/
	private java.lang.String profession;
	/**用户职位,基层/中层/高层*/
	private java.lang.String position;
	/**年龄*/
	private java.lang.String age;
	/**星座*/
	private java.lang.Integer constellation;
	/**终端系统*/
	private java.lang.String mobileSystem;
	/**终端系统版本*/
	private java.lang.String mobileSystemVersion;
	/**终端厂商*/
	private java.lang.String mobileManufacturer;
	/**终端设备号*/
	private java.lang.String mobileDevice;
	/**ios_token标识*/
	private java.lang.String iosToken;
	/**登录ip*/
	private java.lang.String loginIp;
	/**用户头像*/
	private java.lang.String avatarUrl;
	/**注册时间*/
	private java.util.Date registerDate;
	/**更新时间*/
	private java.util.Date updateDate;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  蜂鸟ID
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public java.lang.Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  蜂鸟ID
	 */
	public void setId(java.lang.Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  昵称
	 */
	@Column(name ="NICK_NAME",nullable=false,length=32)
	public java.lang.String getNickName(){
		return this.nickName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  昵称
	 */
	public void setNickName(java.lang.String nickName){
		this.nickName = nickName;
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
	 *@return: java.lang.String  性别
	 */
	@Column(name ="SEX",nullable=true,length=1)
	public java.lang.String getSex(){
		return this.sex;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  性别
	 */
	public void setSex(java.lang.String sex){
		this.sex = sex;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  出生年月日
	 */
	@Column(name ="BIRTHDAY",nullable=true)
	public java.util.Date getBirthday(){
		return this.birthday;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  出生年月日
	 */
	public void setBirthday(java.util.Date birthday){
		this.birthday = birthday;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  密码
	 */
	@Column(name ="PASSWORD",nullable=false,length=64)
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
	 *@return: java.lang.String  手势密码打开关闭状态 0关闭  1打开
	 */
	@Column(name ="GESTURE_PW_TYPE",nullable=true,length=2)
	public java.lang.String getGesturePwType(){
		return this.gesturePwType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码打开关闭状态 0关闭  1打开
	 */
	public void setGesturePwType(java.lang.String gesturePwType){
		this.gesturePwType = gesturePwType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微信授权token
	 */
	@Column(name ="WECHAT_AUTH",nullable=true,length=32)
	public java.lang.String getWechatAuth(){
		return this.wechatAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微信授权token
	 */
	public void setWechatAuth(java.lang.String wechatAuth){
		this.wechatAuth = wechatAuth;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微博授权token
	 */
	@Column(name ="WEIBO_AUTH",nullable=true,length=32)
	public java.lang.String getWeiboAuth(){
		return this.weiboAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微博授权token
	 */
	public void setWeiboAuth(java.lang.String weiboAuth){
		this.weiboAuth = weiboAuth;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  省份_id
	 */
	@Column(name ="PROVINCE_ID",nullable=true,length=32)
	public java.lang.String getProvinceId(){
		return this.provinceId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  省份_id
	 */
	public void setProvinceId(java.lang.String provinceId){
		this.provinceId = provinceId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  省份_value
	 */
	@Column(name ="PROVINCE_NAME",nullable=true,length=32)
	public java.lang.String getProvinceName(){
		return this.provinceName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  省份_value
	 */
	public void setProvinceName(java.lang.String provinceName){
		this.provinceName = provinceName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  城市_id
	 */
	@Column(name ="CITY_ID",nullable=true,length=32)
	public java.lang.String getCityId(){
		return this.cityId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  城市_id
	 */
	public void setCityId(java.lang.String cityId){
		this.cityId = cityId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  城市_name
	 */
	@Column(name ="CITY_NAME",nullable=true,length=32)
	public java.lang.String getCityName(){
		return this.cityName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  城市_name
	 */
	public void setCityName(java.lang.String cityName){
		this.cityName = cityName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  区县_id
	 */
	@Column(name ="DISTRICT_ID",nullable=true,length=32)
	public java.lang.String getDistrictId(){
		return this.districtId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  区县_id
	 */
	public void setDistrictId(java.lang.String districtId){
		this.districtId = districtId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  区县_name
	 */
	@Column(name ="DISTRICT_NAME",nullable=true,length=32)
	public java.lang.String getDistrictName(){
		return this.districtName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  区县_name
	 */
	public void setDistrictName(java.lang.String districtName){
		this.districtName = districtName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  账户状态
	 */
	@Column(name ="STATUS",nullable=true,length=2)
	public java.lang.String getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  账户状态
	 */
	public void setStatus(java.lang.String status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户类型,vip
	 */
	@Column(name ="USER_TYPE",nullable=true,length=2)
	public java.lang.String getUserType(){
		return this.userType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户类型,vip
	 */
	public void setUserType(java.lang.String userType){
		this.userType = userType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户所属行业
	 */
	@Column(name ="PROFESSION",nullable=true,length=32)
	public java.lang.String getProfession(){
		return this.profession;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户所属行业
	 */
	public void setProfession(java.lang.String profession){
		this.profession = profession;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户职位,基层/中层/高层
	 */
	@Column(name ="POSITION",nullable=true,length=32)
	public java.lang.String getPosition(){
		return this.position;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户职位,基层/中层/高层
	 */
	public void setPosition(java.lang.String position){
		this.position = position;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  年龄
	 */
	@Column(name ="AGE",nullable=true,length=32)
	public java.lang.String getAge(){
		return this.age;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  年龄
	 */
	public void setAge(java.lang.String age){
		this.age = age;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  星座
	 */
	@Column(name ="CONSTELLATION",nullable=true,precision=10,scale=0)
	public java.lang.Integer getConstellation(){
		return this.constellation;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  星座
	 */
	public void setConstellation(java.lang.Integer constellation){
		this.constellation = constellation;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端系统
	 */
	@Column(name ="MOBILE_SYSTEM",nullable=true,length=32)
	public java.lang.String getMobileSystem(){
		return this.mobileSystem;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端系统
	 */
	public void setMobileSystem(java.lang.String mobileSystem){
		this.mobileSystem = mobileSystem;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端系统版本
	 */
	@Column(name ="MOBILE_SYSTEM_VERSION",nullable=true,length=64)
	public java.lang.String getMobileSystemVersion(){
		return this.mobileSystemVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端系统版本
	 */
	public void setMobileSystemVersion(java.lang.String mobileSystemVersion){
		this.mobileSystemVersion = mobileSystemVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端厂商
	 */
	@Column(name ="MOBILE_MANUFACTURER",nullable=true,length=64)
	public java.lang.String getMobileManufacturer(){
		return this.mobileManufacturer;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端厂商
	 */
	public void setMobileManufacturer(java.lang.String mobileManufacturer){
		this.mobileManufacturer = mobileManufacturer;
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  ios_token标识
	 */
	@Column(name ="IOS_TOKEN",nullable=true,length=64)
	public java.lang.String getIosToken(){
		return this.iosToken;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  ios_token标识
	 */
	public void setIosToken(java.lang.String iosToken){
		this.iosToken = iosToken;
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
	 *@return: java.lang.String  用户头像
	 */
	@Column(name ="AVATAR_URL",nullable=true,length=255)
	public java.lang.String getAvatarUrl(){
		return this.avatarUrl;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户头像
	 */
	public void setAvatarUrl(java.lang.String avatarUrl){
		this.avatarUrl = avatarUrl;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  注册时间
	 */
	@Column(name ="REGISTER_DATE",nullable=false)
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
}
