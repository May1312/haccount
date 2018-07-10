package com.fnjz.front.entity.api.apps;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: app版本管理表相关
 * @date 2018-06-26 13:11:13
 * @version V1.0   
 *
 */
@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AppsRestDTO implements java.io.Serializable {

	/**id*/
	private java.lang.String id;
	/**app版本号*/
	private String version;
	/**终端系统标识 0_android  1_ios*/
	private Integer mobileSystem;
	/**app状态,是否强制升级*/
	private Integer installStatus;
	/**app路径*/
	private String url;
	/**app大小*/
	private Integer size;
	/**更新日志*/
	private String updateLog;
	/**创建时间*/
	private Date createDate;

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
	public String getVersion(){
		return this.version;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  app版本号
	 */
	public void setVersion(String version){
		this.version = version;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  终端系统标识 0_android  1_ios
	 */
	@Column(name ="MOBILE_SYSTEM",nullable=true,precision=10,scale=0)
	public Integer getMobileSystem(){
		return this.mobileSystem;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  终端系统标识 0_android  1_ios
	 */
	public void setMobileSystem(Integer mobileSystem){
		this.mobileSystem = mobileSystem;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app状态,是否强制升级
	 */
	@Column(name ="INSTALL_STATUS",nullable=true,precision=10,scale=0)
	public Integer getInstallStatus(){
		return this.installStatus;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app状态,是否强制升级
	 */
	public void setInstallStatus(Integer installStatus){
		this.installStatus = installStatus;
	}

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  app路径
	 */
	@Column(name ="URL",nullable=true,length=255)
	public String getUrl(){
		return this.url;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  app路径
	 */
	public void setUrl(String url){
		this.url = url;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app大小
	 */
	@Column(name ="SIZE",nullable=true,precision=10,scale=0)
	public Integer getSize(){
		return this.size;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app大小
	 */
	public void setSize(Integer size){
		this.size = size;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  更新日志
	 */
	@Column(name ="UPDATE_LOG",nullable=true,length=1024)
	public String getUpdateLog(){
		return this.updateLog;
	}
	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  更新日志
	 */
	public void setUpdateLog(String updateLog){
		this.updateLog = updateLog;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建时间
	 */
	@Column(name ="CREATE_DATE",nullable=true)
	public Date getCreateDate(){
		return this.createDate;
	}
	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建时间
	 */
	public void setCreateDate(Date createDate){
		this.createDate = createDate;
	}
}
