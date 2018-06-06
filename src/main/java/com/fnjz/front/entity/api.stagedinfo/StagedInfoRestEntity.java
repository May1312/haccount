package com.fnjz.front.entity.api.stagedinfo;

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
 * @Description: 支出关联分期详情表相关
 * @date 2018-06-06 13:21:01
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_staged_info", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class StagedInfoRestEntity implements java.io.Serializable {
	/**分期id*/
	private java.lang.Integer id;
	/**分期期数*/
	private java.lang.String stagedNum;
	/**剩余期数*/
	private java.lang.String remainingStaged;
	/**分期状态*/
	private java.lang.Integer status;
	/**创建时间*/
	private java.util.Date createDate;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	/**更新时间*/
	private java.util.Date updateDate;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  分期id
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public java.lang.Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  分期id
	 */
	public void setId(java.lang.Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  分期期数
	 */
	@Column(name ="STAGED_NUM",nullable=false,length=32)
	public java.lang.String getStagedNum(){
		return this.stagedNum;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  分期期数
	 */
	public void setStagedNum(java.lang.String stagedNum){
		this.stagedNum = stagedNum;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  剩余期数
	 */
	@Column(name ="REMAINING_STAGED",nullable=false,length=32)
	public java.lang.String getRemainingStaged(){
		return this.remainingStaged;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  剩余期数
	 */
	public void setRemainingStaged(java.lang.String remainingStaged){
		this.remainingStaged = remainingStaged;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  分期状态
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  分期状态
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
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
}
