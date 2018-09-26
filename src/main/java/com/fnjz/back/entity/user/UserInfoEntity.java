package com.fnjz.back.entity.user;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**   
 * @Title: Entity
 * @Description: 用户信息
 * @date 2018-06-01 14:59:21
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
	private Integer id;
	/**昵称*/
	private String nickName;
	/**手机*/
	private String mobile;
	/**邮箱*/
	private String email;
	/**性别*/
	private String sex;
	/**出生年月日*/
	private java.util.Date birthday;
	/**密码*/
	private String password;
	/**手势密码*/
	private String gesturePw;
	/**手势密码打开关闭状态 0关闭  1打开*/
	private String gesturePwType;
	/**微信授权token*/
	private String wechatAuth;
	/**微博授权token*/
	private String weiboAuth;
	/**省份_id*/
	private String provinceId;
	/**省份_value*/
	private String provinceName;
	/**城市_id*/
	private String cityId;
	/**城市_name*/
	private String cityName;
	/**区县_id*/
	private String districtId;
	/**区县_name*/
	private String districtName;
	/**账户状态*/
	private String status;
	/**用户类型,vip*/
	private String userType;
	/**用户所属行业*/
	private String profession;
	/**用户职位,基层/中层/高层*/
	private String position;
	/**年龄*/
	private String age;
	/**星座*/
	private String constellation;
	/**终端系统*/
	private String mobileSystem;

	private String androidChannel;
	/**终端系统版本*/
	private String mobileSystemVersion;
	/**终端厂商*/
	private String mobileManufacturer;
	/**终端设备号*/
	private String mobileDevice;
	/**ios_token标识*/
	private String iosToken;
	/**登录ip*/
	private String loginIp;
	/**用户头像*/
	private String avatarUrl;
	/**注册时间*/
	private java.util.Date registerDate;
	/**更新时间*/
	private java.util.Date updateDate;

	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  蜂鸟ID
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
	 *@param: java.lang.Integer  蜂鸟ID
	 */
	public void setId(Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  昵称
	 */
	@Column(name ="NICK_NAME",nullable=true,length=32)
	public String getNickName(){
		return this.nickName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  昵称
	 */
	@Column(name ="NICK_NAME",nullable=true,length=32)
	public void setNickName(String nickName){
		this.nickName = nickName;
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
	 *@return: java.lang.String  性别
	 */
	@Column(name ="SEX",nullable=true,length=1)
	public String getSex(){
		return this.sex;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  性别
	 */
	public void setSex(String sex){
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
	 *@return: java.lang.String  手势密码打开关闭状态 0关闭  1打开
	 */
	@Column(name ="GESTURE_PW_TYPE",nullable=true,length=2)
	public String getGesturePwType(){
		return this.gesturePwType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  手势密码打开关闭状态 0关闭  1打开
	 */
	public void setGesturePwType(String gesturePwType){
		this.gesturePwType = gesturePwType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微信授权token
	 */
	@Column(name ="WECHAT_AUTH",nullable=true,length=32)
	public String getWechatAuth(){
		return this.wechatAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微信授权token
	 */
	public void setWechatAuth(String wechatAuth){
		this.wechatAuth = wechatAuth;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  微博授权token
	 */
	@Column(name ="WEIBO_AUTH",nullable=true,length=32)
	public String getWeiboAuth(){
		return this.weiboAuth;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  微博授权token
	 */
	public void setWeiboAuth(String weiboAuth){
		this.weiboAuth = weiboAuth;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  省份_id
	 */
	@Column(name ="PROVINCE_ID",nullable=true,length=32)
	public String getProvinceId(){
		return this.provinceId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  省份_id
	 */
	public void setProvinceId(String provinceId){
		this.provinceId = provinceId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  省份_value
	 */
	@Column(name ="PROVINCE_NAME",nullable=true,length=32)
	public String getProvinceName(){
		return this.provinceName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  省份_value
	 */
	public void setProvinceName(String provinceName){
		this.provinceName = provinceName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  城市_id
	 */
	@Column(name ="CITY_ID",nullable=true,length=32)
	public String getCityId(){
		return this.cityId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  城市_id
	 */
	public void setCityId(String cityId){
		this.cityId = cityId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  城市_name
	 */
	@Column(name ="CITY_NAME",nullable=true,length=32)
	public String getCityName(){
		return this.cityName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  城市_name
	 */
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  区县_id
	 */
	@Column(name ="DISTRICT_ID",nullable=true,length=32)
	public String getDistrictId(){
		return this.districtId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  区县_id
	 */
	public void setDistrictId(String districtId){
		this.districtId = districtId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  区县_name
	 */
	@Column(name ="DISTRICT_NAME",nullable=true,length=32)
	public String getDistrictName(){
		return this.districtName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  区县_name
	 */
	public void setDistrictName(String districtName){
		this.districtName = districtName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  账户状态
	 */
	@Column(name ="STATUS",nullable=true,length=2)
	public String getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  账户状态
	 */
	public void setStatus(String status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户类型,vip
	 */
	@Column(name ="USER_TYPE",nullable=true,length=2)
	public String getUserType(){
		return this.userType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户类型,vip
	 */
	public void setUserType(String userType){
		this.userType = userType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户所属行业
	 */
	@Column(name ="PROFESSION",nullable=true,length=32)
	public String getProfession(){
		return this.profession;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户所属行业
	 */
	public void setProfession(String profession){
		this.profession = profession;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户职位,基层/中层/高层
	 */
	@Column(name ="POSITION",nullable=true,length=32)
	public String getPosition(){
		return this.position;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户职位,基层/中层/高层
	 */
	public void setPosition(String position){
		this.position = position;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  年龄
	 */
	@Column(name ="AGE",nullable=true,length=32)
	public String getAge(){
		return this.age;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  年龄
	 */
	public void setAge(String age){
		this.age = age;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  星座
	 */
	@Column(name ="CONSTELLATION",nullable=true,precision=10,scale=0)
	public String getConstellation(){
		return this.constellation;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  星座
	 */
	public void setConstellation(String constellation){
		this.constellation = constellation;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端系统
	 */
	@Column(name ="MOBILE_SYSTEM",nullable=true,length=32)
	public String getMobileSystem(){
		return this.mobileSystem;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端系统
	 */
	public void setMobileSystem(String mobileSystem){
		this.mobileSystem = mobileSystem;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端系统版本
	 */
	@Column(name ="MOBILE_SYSTEM_VERSION",nullable=true,length=64)
	public String getMobileSystemVersion(){
		return this.mobileSystemVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端系统版本
	 */
	public void setMobileSystemVersion(String mobileSystemVersion){
		this.mobileSystemVersion = mobileSystemVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端厂商
	 */
	@Column(name ="MOBILE_MANUFACTURER",nullable=true,length=64)
	public String getMobileManufacturer(){
		return this.mobileManufacturer;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端厂商
	 */
	public void setMobileManufacturer(String mobileManufacturer){
		this.mobileManufacturer = mobileManufacturer;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端设备号
	 */
	@Column(name ="MOBILE_DEVICE",nullable=true,length=64)
	public String getMobileDevice(){
		return this.mobileDevice;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端设备号
	 */
	public void setMobileDevice(String mobileDevice){
		this.mobileDevice = mobileDevice;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  ios_token标识
	 */
	@Column(name ="IOS_TOKEN",nullable=true,length=64)
	public String getIosToken(){
		return this.iosToken;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  ios_token标识
	 */
	public void setIosToken(String iosToken){
		this.iosToken = iosToken;
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
	 *@return: java.lang.String  用户头像
	 */
	@Column(name ="AVATAR_URL",nullable=true,length=255)
	public String getAvatarUrl(){
		return this.avatarUrl;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户头像
	 */
	public void setAvatarUrl(String avatarUrl){
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
	@Column(name ="REGISTER_DATE",nullable=false)
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

	@Column(name ="ANDROID_CHANNEL",nullable=true,length=32)
	public String getAndroidChannel() {
		return androidChannel;
	}

	public void setAndroidChannel(String androidChannel) {
		this.androidChannel = androidChannel;
	}
}
