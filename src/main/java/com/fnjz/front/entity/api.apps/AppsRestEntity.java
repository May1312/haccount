package com.fnjz.front.entity.api.apps;

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
 * @Description: app版本管理表相关
 * @date 2018-06-26 13:11:13
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_apps", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AppsRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.String id;
	/**app版本号*/
	private java.lang.String version;
	/**终端系统标识 0_android  1_ios*/
	private java.lang.Integer mobileSystem;
	/**app状态,是否强制升级*/
	private java.lang.Integer installStatus;
	/**app状态,是否有效 0_无效 1_有效*/
	private java.lang.Integer appStatus;
	/**系统适配范围max*/
	private java.lang.String systemMax;
	/**系统适配范围min*/
	private java.lang.String systemMin;
	/**app路径*/
	private java.lang.String url;
	/**app大小*/
	private java.lang.Integer size;
	/**更新日志*/
	private java.lang.String updateLog;
	/**创建时间*/
	private java.util.Date createDate;
	/**创建人*/
	private java.lang.String createBy;
	/**创建人名称*/
	private java.lang.String createName;
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
	 *@return: java.lang.String  app版本号
	 */
	@Column(name ="VERSION",nullable=true,length=255)
	public java.lang.String getVersion(){
		return this.version;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  app版本号
	 */
	public void setVersion(java.lang.String version){
		this.version = version;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  终端系统标识 0_android  1_ios
	 */
	@Column(name ="MOBILE_SYSTEM",nullable=true,precision=10,scale=0)
	public java.lang.Integer getMobileSystem(){
		return this.mobileSystem;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  终端系统标识 0_android  1_ios
	 */
	public void setMobileSystem(java.lang.Integer mobileSystem){
		this.mobileSystem = mobileSystem;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app状态,是否强制升级
	 */
	@Column(name ="INSTALL_STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getInstallStatus(){
		return this.installStatus;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app状态,是否强制升级
	 */
	public void setInstallStatus(java.lang.Integer installStatus){
		this.installStatus = installStatus;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app状态,是否有效 0_无效 1_有效
	 */
	@Column(name ="APP_STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getAppStatus(){
		return this.appStatus;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app状态,是否有效 0_无效 1_有效
	 */
	public void setAppStatus(java.lang.Integer appStatus){
		this.appStatus = appStatus;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  系统适配范围max
	 */
	@Column(name ="SYSTEM_MAX",nullable=true,length=32)
	public java.lang.String getSystemMax(){
		return this.systemMax;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  系统适配范围max
	 */
	public void setSystemMax(java.lang.String systemMax){
		this.systemMax = systemMax;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  系统适配范围min
	 */
	@Column(name ="SYSTEM_MIN",nullable=true,length=32)
	public java.lang.String getSystemMin(){
		return this.systemMin;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  系统适配范围min
	 */
	public void setSystemMin(java.lang.String systemMin){
		this.systemMin = systemMin;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  app路径
	 */
	@Column(name ="URL",nullable=true,length=255)
	public java.lang.String getUrl(){
		return this.url;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  app路径
	 */
	public void setUrl(java.lang.String url){
		this.url = url;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app大小
	 */
	@Column(name ="SIZE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getSize(){
		return this.size;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app大小
	 */
	public void setSize(java.lang.Integer size){
		this.size = size;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  更新日志
	 */
	@Column(name ="UPDATE_LOG",nullable=true,length=1024)
	public java.lang.String getUpdateLog(){
		return this.updateLog;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  更新日志
	 */
	public void setUpdateLog(java.lang.String updateLog){
		this.updateLog = updateLog;
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人
	 */
	@Column(name ="CREATE_BY",nullable=true,length=36)
	public java.lang.String getCreateBy(){
		return this.createBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人
	 */
	public void setCreateBy(java.lang.String createBy){
		this.createBy = createBy;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人名称
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public java.lang.String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人名称
	 */
	public void setCreateName(java.lang.String createName){
		this.createName = createName;
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
