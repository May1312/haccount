package com.fnjz.front.entity.api.banner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: 轮播图相关
 * @date 2018-10-19 18:52:50
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_banner", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class BannerRestDTO implements java.io.Serializable {
	/**id*/
	private String id;
	/**轮播图名称*/
	private String bannerName;
	/**跳转类型*/
	private String jumpType;
	/**连接地址*/
	private String connectionAddress;
	/**上线时间*/
	private Date uptime;
	/**下线时间*/
	private Date downtime;
	/**安卓需要展示的版本号*/
	private String androidShowVersion;
	/**苹果需要展示的版本号*/
	private String iosShowVersion;
	/**小程序需要展示的版本号*/
	private String smallprogramShowVersion;
	/**优先级*/
	private Integer priority;
	/**图片（375*1035*/
	private String image;
	/**分享标题*/
	private String shareTitle;
	/**分享内容*/
	private String shareContent;
	/**分享图片（300*300*/
	private String shareImage;
	/**描述*/
	private String remark;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  id
	 */

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=36)
	public String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  id
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  轮播图名称
	 */
	@Column(name ="BANNER_NAME",nullable=true,length=64)
	public String getBannerName(){
		return this.bannerName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  轮播图名称
	 */
	public void setBannerName(String bannerName){
		this.bannerName = bannerName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  跳转类型
	 */
	@Column(name ="JUMP_TYPE",nullable=true,length=64)
	public String getJumpType(){
		return this.jumpType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  跳转类型
	 */
	public void setJumpType(String jumpType){
		this.jumpType = jumpType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  连接地址
	 */
	@Column(name ="CONNECTION_ADDRESS",nullable=true,length=1024)
	public String getConnectionAddress(){
		return this.connectionAddress;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  连接地址
	 */
	public void setConnectionAddress(String connectionAddress){
		this.connectionAddress = connectionAddress;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  上线时间
	 */
	@Column(name ="UPTIME",nullable=true)
	public Date getUptime(){
		return this.uptime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  上线时间
	 */
	public void setUptime(Date uptime){
		this.uptime = uptime;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  下线时间
	 */
	@Column(name ="DOWNTIME",nullable=true)
	public Date getDowntime(){
		return this.downtime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  下线时间
	 */
	public void setDowntime(Date downtime){
		this.downtime = downtime;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  安卓需要展示的版本号
	 */
	@Column(name ="ANDROID_SHOW_VERSION",nullable=true,length=32)
	public String getAndroidShowVersion(){
		return this.androidShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  安卓需要展示的版本号
	 */
	public void setAndroidShowVersion(String androidShowVersion){
		this.androidShowVersion = androidShowVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  苹果需要展示的版本号
	 */
	@Column(name ="IOS_SHOW_VERSION",nullable=true,length=32)
	public String getIosShowVersion(){
		return this.iosShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  苹果需要展示的版本号
	 */
	public void setIosShowVersion(String iosShowVersion){
		this.iosShowVersion = iosShowVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  小程序需要展示的版本号
	 */
	@Column(name ="SMALLPROGRAM_SHOW_VERSION",nullable=true,length=32)
	public String getSmallprogramShowVersion(){
		return this.smallprogramShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  小程序需要展示的版本号
	 */
	public void setSmallprogramShowVersion(String smallprogramShowVersion){
		this.smallprogramShowVersion = smallprogramShowVersion;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  优先级
	 */
	@Column(name ="PRIORITY",nullable=true,precision=10,scale=0)
	public Integer getPriority(){
		return this.priority;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  优先级
	 */
	public void setPriority(Integer priority){
		this.priority = priority;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图片（375*1035
	 */
	@Column(name ="IMAGE",nullable=true,length=256)
	public String getImage(){
		return this.image;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图片（375*1035
	 */
	public void setImage(String image){
		this.image = image;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享标题
	 */
	@Column(name ="SHARE_TITLE",nullable=true,length=256)
	public String getShareTitle(){
		return this.shareTitle;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享标题
	 */
	public void setShareTitle(String shareTitle){
		this.shareTitle = shareTitle;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享内容
	 */
	@Column(name ="SHARE_CONTENT",nullable=true,length=1024)
	public String getShareContent(){
		return this.shareContent;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享内容
	 */
	public void setShareContent(String shareContent){
		this.shareContent = shareContent;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享图片（300*300
	 */
	@Column(name ="SHARE_IMAGE",nullable=true,length=256)
	public String getShareImage(){
		return this.shareImage;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享图片（300*300
	 */
	public void setShareImage(String shareImage){
		this.shareImage = shareImage;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  描述
	 */
	@Column(name ="REMARK",nullable=true,length=256)
	public String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  描述
	 */
	public void setRemark(String remark){
		this.remark = remark;
	}
}
