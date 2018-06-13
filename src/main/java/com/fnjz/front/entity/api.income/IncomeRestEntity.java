package com.fnjz.front.entity.api.income;

import java.math.BigDecimal;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**   
 * @Title: Entity
 * @Description: 账本-收入表相关
 * @date 2018-06-06 13:27:56
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_income", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class IncomeRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**所属账本*/
	private java.lang.Integer accountBookId;
	/**单笔记录号*/
	private java.lang.String incomeOrder;
	/**收入金额*/
	private BigDecimal incomeMoney;
	/**二级收入类目id*/
	private java.lang.String incomeTypePid;
	/**二级收入类目_name*/
	private java.lang.String incomeTypePname;
	/**三级收入类目id*/
	private java.lang.String incomeTypeId;
	/**三级收入类目_name*/
	private java.lang.String incomeTypeName;
	/**更新时间*/
	@JsonIgnore
	private java.util.Date updateDate;
	/**创建时间*/
	private java.util.Date createDate;
	/**记账时间*/
	private java.util.Date incomeDate;
	/**删除日期*/
	@JsonIgnore
	private java.util.Date deleteDate;
	/**创建者id*/
	@JsonIgnore
	private java.lang.Integer createBy;
	/**创建者名称*/
	@JsonIgnore
	private java.lang.String createName;
	/**修改者id*/
	@JsonIgnore
	private java.lang.Integer updateBy;
	/**修改者名称*/
	@JsonIgnore
	private java.lang.String updateName;
	/**删除状态0:有效,1:删除*/
	@JsonIgnore
	private java.lang.Integer delflag;
	/**备注*/
	private java.lang.String remark;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public java.lang.Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(java.lang.Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  所属账本
	 */
	@Column(name ="ACCOUNT_BOOK_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getAccountBookId(){
		return this.accountBookId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  所属账本
	 */
	public void setAccountBookId(java.lang.Integer accountBookId){
		this.accountBookId = accountBookId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  单笔记录号
	 */
	@Column(name ="INCOME_ORDER",nullable=false,length=32)
	public java.lang.String getIncomeOrder(){
		return this.incomeOrder;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  单笔记录号
	 */
	public void setIncomeOrder(java.lang.String incomeOrder){
		this.incomeOrder = incomeOrder;
	}
	/**
	 *方法: 取得BigDecimal
	 *@return: BigDecimal  收入金额
	 */
	@Column(name ="INCOME_MONEY",nullable=true,precision=32,scale=2)
	public BigDecimal getIncomeMoney(){
		return this.incomeMoney;
	}

	/**
	 *方法: 设置BigDecimal
	 *@param: BigDecimal  收入金额
	 */
	public void setIncomeMoney(BigDecimal incomeMoney){
		this.incomeMoney = incomeMoney;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级收入类目id
	 */
	@Column(name ="INCOME_TYPE_PID",nullable=true,length=36)
	public java.lang.String getIncomeTypePid(){
		return this.incomeTypePid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级收入类目id
	 */
	public void setIncomeTypePid(java.lang.String incomeTypePid){
		this.incomeTypePid = incomeTypePid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级收入类目_name
	 */
	@Column(name ="INCOME_TYPE_PNAME",nullable=true,length=32)
	public java.lang.String getIncomeTypePname(){
		return this.incomeTypePname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级收入类目_name
	 */
	public void setIncomeTypePname(java.lang.String incomeTypePname){
		this.incomeTypePname = incomeTypePname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级收入类目id
	 */
	@Column(name ="INCOME_TYPE_ID",nullable=true,length=36)
	public java.lang.String getIncomeTypeId(){
		return this.incomeTypeId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级收入类目id
	 */
	public void setIncomeTypeId(java.lang.String incomeTypeId){
		this.incomeTypeId = incomeTypeId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级收入类目_name
	 */
	@Column(name ="INCOME_TYPE_NAME",nullable=true,length=32)
	public java.lang.String getIncomeTypeName(){
		return this.incomeTypeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级收入类目_name
	 */
	public void setIncomeTypeName(java.lang.String incomeTypeName){
		this.incomeTypeName = incomeTypeName;
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
	 *@return: java.util.Date  记账时间
	 */
	@Column(name ="INCOME_DATE",nullable=false)
	public java.util.Date getIncomeDate(){
		return this.incomeDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  记账时间
	 */
	public void setIncomeDate(java.util.Date incomeDate){
		this.incomeDate = incomeDate;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  删除日期
	 */
	@Column(name ="DELETE_DATE",nullable=true)
	public java.util.Date getDeleteDate(){
		return this.deleteDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  删除日期
	 */
	public void setDeleteDate(java.util.Date deleteDate){
		this.deleteDate = deleteDate;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  创建者id
	 */
	@Column(name ="CREATE_BY",nullable=true,precision=10,scale=0)
	public java.lang.Integer getCreateBy(){
		return this.createBy;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  创建者id
	 */
	public void setCreateBy(java.lang.Integer createBy){
		this.createBy = createBy;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建者名称
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=64)
	public java.lang.String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建者名称
	 */
	public void setCreateName(java.lang.String createName){
		this.createName = createName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  修改者id
	 */
	@Column(name ="UPDATE_BY",nullable=true,precision=10,scale=0)
	public java.lang.Integer getUpdateBy(){
		return this.updateBy;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  修改者id
	 */
	public void setUpdateBy(java.lang.Integer updateBy){
		this.updateBy = updateBy;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  修改者名称
	 */
	@Column(name ="UPDATE_NAME",nullable=true,length=64)
	public java.lang.String getUpdateName(){
		return this.updateName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改者名称
	 */
	public void setUpdateName(java.lang.String updateName){
		this.updateName = updateName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  删除状态0:有效,1:删除
	 */
	@Column(name ="DELFLAG",nullable=true,precision=10,scale=0)
	public java.lang.Integer getDelflag(){
		return this.delflag;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除状态0:有效,1:删除
	 */
	public void setDelflag(java.lang.Integer delflag){
		this.delflag = delflag;
	}
	/**
	 *方法: 取得java.lang.Object
	 *@return: java.lang.Object  备注
	 */
	@Column(name ="REMARK",nullable=true,length=65535)
	public java.lang.String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.Object
	 *@param: java.lang.Object  备注
	 */
	public void setRemark(java.lang.String remark){
		this.remark = remark;
	}
}
