package com.fnjz.back.entity.sms;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: 短信发送记录
 * @author zhangdaihao
 * @date 2018-05-30 10:11:54
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_sms_record", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class SmsRecordEntity implements java.io.Serializable {
	/**GUID主键*/
	private String id;
	/**发送状态*/
	private String sendstate;
	/**终端类型*/
	private String terminaltype;
	/**发送手机号*/
	private String sendmobile;
	/**发送内容*/
	private String sendcontent;
	/**发送时间*/
	private Date sendtime;
	/**返回时间*/
	private Date returntime;
	/**创建人*/
	private String createBy;
	/**创建人名字*/
	private String createName;
	/**创建时间*/
	private Date createDate;
	/**修改人*/
	private String updateBy;
	/**修改人名字*/
	private String updateName;
	/**修改时间*/
	private Date updateDate;
	/**删除标记*/
	private Integer delFalg;
	/**删除时间*/
	private Date delDate;
	/**备注说明*/
	private String remark;
	/**备用字段*/
	private String tag;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  GUID主键
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
	 *@param: java.lang.String  GUID主键
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送状态
	 */
	@Column(name ="SENDSTATE",nullable=true,length=36)
	public String getSendstate(){
		return this.sendstate;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送状态
	 */
	public void setSendstate(String sendstate){
		this.sendstate = sendstate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  终端类型
	 */
	@Column(name ="TERMINALTYPE",nullable=true,length=36)
	public String getTerminaltype(){
		return this.terminaltype;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  终端类型
	 */
	public void setTerminaltype(String terminaltype){
		this.terminaltype = terminaltype;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送手机号
	 */
	@Column(name ="SENDMOBILE",nullable=true,length=56)
	public String getSendmobile(){
		return this.sendmobile;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送手机号
	 */
	public void setSendmobile(String sendmobile){
		this.sendmobile = sendmobile;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  发送内容
	 */
	@Column(name ="SENDCONTENT",nullable=true,length=1024)
	public String getSendcontent(){
		return this.sendcontent;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  发送内容
	 */
	public void setSendcontent(String sendcontent){
		this.sendcontent = sendcontent;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  发送时间
	 */
	@Column(name ="SENDTIME",nullable=true)
	public Date getSendtime(){
		return this.sendtime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  发送时间
	 */
	public void setSendtime(Date sendtime){
		this.sendtime = sendtime;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  返回时间
	 */
	@Column(name ="RETURNTIME",nullable=true)
	public Date getReturntime(){
		return this.returntime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  返回时间
	 */
	public void setReturntime(Date returntime){
		this.returntime = returntime;
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
	 *@return: java.lang.String  创建人名字
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人名字
	 */
	public void setCreateName(String createName){
		this.createName = createName;
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
	 *@return: java.lang.String  修改人
	 */
	@Column(name ="UPDATE_BY",nullable=true,length=36)
	public String getUpdateBy(){
		return this.updateBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改人
	 */
	public void setUpdateBy(String updateBy){
		this.updateBy = updateBy;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  修改人名字
	 */
	@Column(name ="UPDATE_NAME",nullable=true,length=32)
	public String getUpdateName(){
		return this.updateName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改人名字
	 */
	public void setUpdateName(String updateName){
		this.updateName = updateName;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  修改时间
	 */
	@Column(name ="UPDATE_DATE",nullable=true)
	public Date getUpdateDate(){
		return this.updateDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  修改时间
	 */
	public void setUpdateDate(Date updateDate){
		this.updateDate = updateDate;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除标记
	 */
	@Column(name ="DEL_FALG",nullable=true,precision=3,scale=0)
	public Integer getDelFalg(){
		return this.delFalg;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除标记
	 */
	public void setDelFalg(Integer delFalg){
		this.delFalg = delFalg;
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
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备注说明
	 */
	@Column(name ="REMARK",nullable=true,length=1024)
	public String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备注说明
	 */
	public void setRemark(String remark){
		this.remark = remark;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备用字段
	 */
	@Column(name ="TAG",nullable=true,length=256)
	public String getTag(){
		return this.tag;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备用字段
	 */
	public void setTag(String tag){
		this.tag = tag;
	}
}
