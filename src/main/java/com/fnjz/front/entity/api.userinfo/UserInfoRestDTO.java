package com.fnjz.front.entity.api.userinfo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 用户详情表相关
 * @date 2018-05-30 14:05:50
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_info", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@SuppressWarnings("serial")
public class UserInfoRestDTO implements java.io.Serializable {
	/**蜂鸟ID*/
	private Integer id;
	/**昵称*/
	private String nickName;
	/**手机*/
	private String mobile;
	/**性别*/
	private String sex;
	/**出生年月日*/
	private java.util.Date birthday;
	/**省份_id*/
	private String provinceId;
	/**省份_value*/
	private String provinceName;
	/**城市_id*/
	private String cityId;
	/**城市_name*/
	private String cityName;
	/**用户所属行业*/
	private String profession;
	/**用户职位,基层/中层/高层*/
	private String position;
	/**年龄*/
	private String age;
	/**星座*/
	private String constellation;
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
	@Column(name ="NICK_NAME",length=32)
	public String getNickName(){
		return this.nickName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  昵称
	 */
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
	@Column(name ="REGISTER_DATE")
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
