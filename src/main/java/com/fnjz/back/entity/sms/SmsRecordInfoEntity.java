package com.fnjz.back.entity.sms;

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
 * @Description: 发送记录
 * @date 2018-06-01 13:58:27
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_sms_recordinfo", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class SmsRecordInfoEntity implements java.io.Serializable {
	/**GUID主键*/
	private java.lang.String id;
	/**创建发送记录id*/
	private java.lang.String smsrecordid;
	/**发送内容*/
	private java.lang.String sendcontent;
	/**发送状态*/
	private java.lang.String sendstate;
	/**终端类型*/
	private java.lang.String terminaltype;
	/**发送手机号*/
	private java.lang.String sendmobile;
	/**发送模板id*/
	private java.lang.String sendtemplatecode;
	/**发送时间*/
	private java.util.Date sendtime;
	/**返回时间*/
	private java.util.Date returntime;
	/**创建人*/
	private java.lang.String createBy;
	/**创建人名字*/
	private java.lang.String createName;
	/**创建时间*/
	private java.util.Date createDate;
	/**修改人*/
	private java.lang.String updateBy;
	/**修改人名字*/
	private java.lang.String updateName;
	/**修改时间*/
	private java.util.Date updateDate;
	/**删除标记*/
	private java.lang.Integer delFalg;
	/**删除时间*/
	private java.util.Date delDate;
	/**备注说明*/
	private java.lang.String remark;
	/**备用字段*/
	private java.lang.String tag;
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  GUID主键
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
	 *@param: java.lang.String  GUID主键
	 */
	public void setId(java.lang.String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建发送记录id
	 */
	@Column(name ="SMSRECORDID",nullable=true,length=36)
	public java.lang.String getSmsrecordid(){
		return this.smsrecordid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建发送记录id
	 */
	public void setSmsrecordid(java.lang.String smsrecordid){
		this.smsrecordid = smsrecordid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送内容
	 */
	@Column(name ="SENDCONTENT",nullable=true,length=36)
	public java.lang.String getSendcontent(){
		return this.sendcontent;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送内容
	 */
	public void setSendcontent(java.lang.String sendcontent){
		this.sendcontent = sendcontent;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送状态
	 */
	@Column(name ="SENDSTATE",nullable=true,length=36)
	public java.lang.String getSendstate(){
		return this.sendstate;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送状态
	 */
	public void setSendstate(java.lang.String sendstate){
		this.sendstate = sendstate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端类型
	 */
	@Column(name ="TERMINALTYPE",nullable=true,length=36)
	public java.lang.String getTerminaltype(){
		return this.terminaltype;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端类型
	 */
	public void setTerminaltype(java.lang.String terminaltype){
		this.terminaltype = terminaltype;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送手机号
	 */
	@Column(name ="SENDMOBILE",nullable=true,length=56)
	public java.lang.String getSendmobile(){
		return this.sendmobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送手机号
	 */
	public void setSendmobile(java.lang.String sendmobile){
		this.sendmobile = sendmobile;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送模板id
	 */
	@Column(name ="SENDTEMPLATECODE",nullable=true,length=1024)
	public java.lang.String getSendtemplatecode(){
		return this.sendtemplatecode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送模板id
	 */
	public void setSendtemplatecode(java.lang.String sendtemplatecode){
		this.sendtemplatecode = sendtemplatecode;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  发送时间
	 */
	@Column(name ="SENDTIME",nullable=true)
	public java.util.Date getSendtime(){
		return this.sendtime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  发送时间
	 */
	public void setSendtime(java.util.Date sendtime){
		this.sendtime = sendtime;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  返回时间
	 */
	@Column(name ="RETURNTIME",nullable=true)
	public java.util.Date getReturntime(){
		return this.returntime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  返回时间
	 */
	public void setReturntime(java.util.Date returntime){
		this.returntime = returntime;
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
	 *@return: java.lang.String  创建人名字
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public java.lang.String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人名字
	 */
	public void setCreateName(java.lang.String createName){
		this.createName = createName;
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
	 *@return: java.lang.String  修改人
	 */
	@Column(name ="UPDATE_BY",nullable=true,length=36)
	public java.lang.String getUpdateBy(){
		return this.updateBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改人
	 */
	public void setUpdateBy(java.lang.String updateBy){
		this.updateBy = updateBy;
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除标记
	 */
	@Column(name ="DEL_FALG",nullable=true,precision=3,scale=0)
	public java.lang.Integer getDelFalg(){
		return this.delFalg;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除标记
	 */
	public void setDelFalg(java.lang.Integer delFalg){
		this.delFalg = delFalg;
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
	@Column(name ="REMARK",nullable=true,length=1024)
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
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备用字段
	 */
	@Column(name ="TAG",nullable=true,length=256)
	public java.lang.String getTag(){
		return this.tag;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备用字段
	 */
	public void setTag(java.lang.String tag){
		this.tag = tag;
	}
}
