package com.fnjz.back.entity.api.accountbook;

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
 * @Description: 移动端账本表
 * @date 2018-05-30 10:05:04
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_account_book", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AccountBookEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**账本名称*/
	private java.lang.String abName;
	/**账本状态,0:正常,1:删除*/
	private java.lang.Integer status;
	/**更新时间*/
	private java.util.Date updateDate;
	/**创建时间*/
	private java.util.Date createDate;
	/**创建人*/
	private java.lang.Integer createBy;
	/**创建人名字*/
	private java.lang.String createName;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  账本名称
	 */
	@Column(name ="AB_NAME",nullable=true,length=64)
	public java.lang.String getAbName(){
		return this.abName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  账本名称
	 */
	public void setAbName(java.lang.String abName){
		this.abName = abName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  账本状态,0:正常,1:删除
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  账本状态,0:正常,1:删除
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  创建人
	 */
	@Column(name ="CREATE_BY",nullable=true,precision=10,scale=0)
	public java.lang.Integer getCreateBy(){
		return this.createBy;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  创建人
	 */
	public void setCreateBy(java.lang.Integer createBy){
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
