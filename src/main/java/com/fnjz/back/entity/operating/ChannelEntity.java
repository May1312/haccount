package com.fnjz.back.entity.operating;

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
 * @Description: 渠道
 * @date 2018-06-13 18:08:43
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_channel", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class ChannelEntity implements java.io.Serializable {
	/**id*/
	private java.lang.String id;
	/**渠道类型*/
	private java.lang.String channeltype;
	/**渠道标识*/
	private java.lang.String channelflag;
	/**渠道名称*/
	private java.lang.String channelname;
	/**渠道短链*/
	private java.lang.String channelurl;
	/**注册量*/
	private java.lang.String registrations;
	/**登陆量*/
	private java.lang.String loginnumber;
	/**创建人*/
	private java.lang.String createName;
	/**修改人名字*/
	private java.lang.String updateName;
	/**创建时间*/
	private java.util.Date createDate;
	/**修改时间*/
	private java.util.Date updateDate;
	/**状态:0_未生效   1_已生效*/
	private java.lang.String status;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	/**备注说明*/
	private java.lang.String remark;
	
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
	 *@return: java.lang.String  渠道类型
	 */
	@Column(name ="CHANNELTYPE",nullable=true,length=32)
	public java.lang.String getChanneltype(){
		return this.channeltype;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  渠道类型
	 */
	public void setChanneltype(java.lang.String channeltype){
		this.channeltype = channeltype;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  渠道标识
	 */
	@Column(name ="CHANNELFLAG",nullable=true,length=32)
	public java.lang.String getChannelflag(){
		return this.channelflag;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  渠道标识
	 */
	public void setChannelflag(java.lang.String channelflag){
		this.channelflag = channelflag;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  渠道名称
	 */
	@Column(name ="CHANNELNAME",nullable=true,length=255)
	public java.lang.String getChannelname(){
		return this.channelname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  渠道名称
	 */
	public void setChannelname(java.lang.String channelname){
		this.channelname = channelname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  渠道短链
	 */
	@Column(name ="CHANNELURL",nullable=true,length=36)
	public java.lang.String getChannelurl(){
		return this.channelurl;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  渠道短链
	 */
	public void setChannelurl(java.lang.String channelurl){
		this.channelurl = channelurl;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  注册量
	 */
	@Column(name ="REGISTRATIONS",nullable=true,length=36)
	public java.lang.String getRegistrations(){
		return this.registrations;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  注册量
	 */
	public void setRegistrations(java.lang.String registrations){
		this.registrations = registrations;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  登陆量
	 */
	@Column(name ="LOGINNUMBER",nullable=true,length=36)
	public java.lang.String getLoginnumber(){
		return this.loginnumber;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  登陆量
	 */
	public void setLoginnumber(java.lang.String loginnumber){
		this.loginnumber = loginnumber;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public java.lang.String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人
	 */
	public void setCreateName(java.lang.String createName){
		this.createName = createName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  修改人名字
	 */
	@Column(name ="UPDATE_NAME",nullable=true,length=32)
	public java.lang.String getUpdateName(){
		return this.updateName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改人名字
	 */
	public void setUpdateName(java.lang.String updateName){
		this.updateName = updateName;
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
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  修改时间
	 */
	@Column(name ="UPDATE_DATE",nullable=true)
	public java.util.Date getUpdateDate(){
		return this.updateDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  修改时间
	 */
	public void setUpdateDate(java.util.Date updateDate){
		this.updateDate = updateDate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  状态:0_未生效   1_已生效
	 */
	@Column(name ="STATUS",nullable=true,length=2)
	public java.lang.String getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  状态:0_未生效   1_已生效
	 */
	public void setStatus(java.lang.String status){
		this.status = status;
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
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备注说明
	 */
	@Column(name ="REMARK",nullable=true,length=32)
	public java.lang.String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备注说明
	 */
	public void setRemark(java.lang.String remark){
		this.remark = remark;
	}
}
