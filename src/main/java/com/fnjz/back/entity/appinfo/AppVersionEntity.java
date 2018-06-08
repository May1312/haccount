package com.fnjz.back.entity.appinfo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: App版本升级
 * @author zhangdaihao
 * @date 2018-05-29 13:48:13
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_apps", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AppVersionEntity implements java.io.Serializable {
	/**id*/
	private String id;
	/**app版本号*/
	private String version;
	/**终端系统标识*/
	private Integer mobileSystem;
	/**app状态,是否强制升级*/
	private Integer installStatus;
	/**app状态,是否有效*/
	private Integer appStatus;
	/**系统适配范围max*/
	private String systemMax;
	/**系统适配范围min*/
	private String systemMin;
	/**app路径*/
	private String url;
	/**app大小*/
	private Integer size;
	/**更新日志*/
	private String updateLog;
	/**创建时间*/
	private Date createDate;
	/**创建人*/
	private String createBy;
	/**创建人名称*/
	private String createName;
	/**删除标记*/
	private Integer delflag;
	/**删除时间*/
	private Date delDate;

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
	 *@return: java.lang.Integer  终端系统标识
	 */
	@Column(name ="MOBILE_SYSTEM",nullable=true,precision=10,scale=0)
	public Integer getMobileSystem(){
		return this.mobileSystem;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  终端系统标识
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  app状态,是否有效
	 */
	@Column(name ="APP_STATUS",nullable=true,precision=10,scale=0)
	public Integer getAppStatus(){
		return this.appStatus;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  app状态,是否有效
	 */
	public void setAppStatus(Integer appStatus){
		this.appStatus = appStatus;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  系统适配范围max
	 */
	@Column(name ="SYSTEM_MAX",nullable=true,length=32)
	public String getSystemMax(){
		return this.systemMax;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  系统适配范围max
	 */
	public void setSystemMax(String systemMax){
		this.systemMax = systemMax;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  系统适配范围min
	 */
	@Column(name ="SYSTEM_MIN",nullable=true,length=32)
	public String getSystemMin(){
		return this.systemMin;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  系统适配范围min
	 */
	public void setSystemMin(String systemMin){
		this.systemMin = systemMin;
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
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人
	 */
	@Column(name ="CREATE_BY",nullable=true,length=36)
	public String getCreateBy(){
		return this.createBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人
	 */
	public void setCreateBy(String createBy){
		this.createBy = createBy;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人名称
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人名称
	 */
	public void setCreateName(String createName){
		this.createName = createName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除标记
	 */
	@Column(name ="DELFLAG",nullable=true,precision=10,scale=0)
	public Integer getDelflag(){
		return this.delflag;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除标记
	 */
	public void setDelflag(Integer delflag){
		this.delflag = delflag;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  删除时间
	 */
	@Column(name ="DEL_DATE",nullable=true)
	public Date getDelDate(){
		return this.delDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  删除时间
	 */
	public void setDelDate(Date delDate){
		this.delDate = delDate;
	}

}
