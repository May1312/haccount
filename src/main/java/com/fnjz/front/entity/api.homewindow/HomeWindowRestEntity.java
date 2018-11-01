package com.fnjz.front.entity.api.homewindow;

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
 * @Description: 首页弹框相关
 * @date 2018-10-19 11:18:37
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_home_window", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class HomeWindowRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.String id;
	/**弹窗名称*/
	private java.lang.String homeWindowName;
	/**跳转类型*/
	private java.lang.String jumpType;
	/**连接地址*/
	private java.lang.String connectionAddress;
	/**上线时间*/
	private java.util.Date uptime;
	/**下线时间*/
	private java.util.Date downtime;
	/**安卓需要展示的版本号*/
	private java.lang.String androidShowVersion;
	/**苹果需要展示的版本号*/
	private java.lang.String iosShowVersion;
	/**小程序需要展示的版本号*/
	private java.lang.String smallprogramShowVersion;
	/**优先级*/
	private java.lang.Integer priority;
	/**图片*/
	private java.lang.String image;
	/**分享标题*/
	private java.lang.String shareTitle;
	/**分享内容*/
	private java.lang.String shareContent;
	/**分享图片*/
	private java.lang.String shareImage;
	/**上线状态:0_下线 1_上线*/
	private java.lang.Integer status;
	/**点击人数*/
	private java.lang.Integer clicksNumber;
	/**创建时间*/
	private java.util.Date createDate;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  id
	 */
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=36)
	public java.lang.String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  id
	 */
	public void setId(java.lang.String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  弹窗名称
	 */
	@Column(name ="HOME_WINDOW_NAME",nullable=true,length=64)
	public java.lang.String getHomeWindowName(){
		return this.homeWindowName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  弹窗名称
	 */
	public void setHomeWindowName(java.lang.String homeWindowName){
		this.homeWindowName = homeWindowName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  跳转类型
	 */
	@Column(name ="JUMP_TYPE",nullable=true,length=64)
	public java.lang.String getJumpType(){
		return this.jumpType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  跳转类型
	 */
	public void setJumpType(java.lang.String jumpType){
		this.jumpType = jumpType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  连接地址
	 */
	@Column(name ="CONNECTION_ADDRESS",nullable=true,length=1024)
	public java.lang.String getConnectionAddress(){
		return this.connectionAddress;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  连接地址
	 */
	public void setConnectionAddress(java.lang.String connectionAddress){
		this.connectionAddress = connectionAddress;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  上线时间
	 */
	@Column(name ="UPTIME",nullable=true)
	public java.util.Date getUptime(){
		return this.uptime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  上线时间
	 */
	public void setUptime(java.util.Date uptime){
		this.uptime = uptime;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  下线时间
	 */
	@Column(name ="DOWNTIME",nullable=true)
	public java.util.Date getDowntime(){
		return this.downtime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  下线时间
	 */
	public void setDowntime(java.util.Date downtime){
		this.downtime = downtime;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  安卓需要展示的版本号
	 */
	@Column(name ="ANDROID_SHOW_VERSION",nullable=true,length=32)
	public java.lang.String getAndroidShowVersion(){
		return this.androidShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  安卓需要展示的版本号
	 */
	public void setAndroidShowVersion(java.lang.String androidShowVersion){
		this.androidShowVersion = androidShowVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  苹果需要展示的版本号
	 */
	@Column(name ="IOS_SHOW_VERSION",nullable=true,length=32)
	public java.lang.String getIosShowVersion(){
		return this.iosShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  苹果需要展示的版本号
	 */
	public void setIosShowVersion(java.lang.String iosShowVersion){
		this.iosShowVersion = iosShowVersion;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  小程序需要展示的版本号
	 */
	@Column(name ="SMALLPROGRAM_SHOW_VERSION",nullable=true,length=32)
	public java.lang.String getSmallprogramShowVersion(){
		return this.smallprogramShowVersion;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  小程序需要展示的版本号
	 */
	public void setSmallprogramShowVersion(java.lang.String smallprogramShowVersion){
		this.smallprogramShowVersion = smallprogramShowVersion;
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
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图片
	 */
	@Column(name ="IMAGE",nullable=true,length=256)
	public java.lang.String getImage(){
		return this.image;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图片
	 */
	public void setImage(java.lang.String image){
		this.image = image;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享标题
	 */
	@Column(name ="SHARE_TITLE",nullable=true,length=256)
	public java.lang.String getShareTitle(){
		return this.shareTitle;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享标题
	 */
	public void setShareTitle(java.lang.String shareTitle){
		this.shareTitle = shareTitle;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享内容
	 */
	@Column(name ="SHARE_CONTENT",nullable=true,length=1024)
	public java.lang.String getShareContent(){
		return this.shareContent;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享内容
	 */
	public void setShareContent(java.lang.String shareContent){
		this.shareContent = shareContent;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分享图片
	 */
	@Column(name ="SHARE_IMAGE",nullable=true,length=256)
	public java.lang.String getShareImage(){
		return this.shareImage;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分享图片
	 */
	public void setShareImage(java.lang.String shareImage){
		this.shareImage = shareImage;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  上线状态:0_下线 1_上线
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  上线状态:0_下线 1_上线
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  点击人数
	 */
	@Column(name ="CLICKS_NUMBER",nullable=true,precision=10,scale=0)
	public java.lang.Integer getClicksNumber(){
		return this.clicksNumber;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  点击人数
	 */
	public void setClicksNumber(java.lang.Integer clicksNumber){
		this.clicksNumber = clicksNumber;
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
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除标记
	 */
	@Column(name ="DELFLAG",nullable=true,precision=10,scale=0)
	public java.lang.Integer getDelflag(){
		return this.delflag;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除标记
	 */
	public void setDelflag(java.lang.Integer delflag){
		this.delflag = delflag;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  删除时间
	 */
	@Column(name ="DEL_DATE",nullable=true)
	public java.util.Date getDelDate(){
		return this.delDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  删除时间
	 */
	public void setDelDate(java.util.Date delDate){
		this.delDate = delDate;
	}
}
