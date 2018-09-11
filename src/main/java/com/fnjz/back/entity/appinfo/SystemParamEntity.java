package com.fnjz.back.entity.appinfo;

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
 * @Description: 系统参数控制表
 * @date 2018-09-10 11:10:12
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_system_param", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class SystemParamEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**参数类型 支出类目:spend_type 收入类目:income_type 同步时间间隔:syn_interval*/
	private java.lang.String paramType;
	/**用户详情id*/
	private java.lang.String version;
	/**创建时间*/
	private java.util.Date createDate;
	/**更新时间*/
	private java.util.Date updateDate;
	
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
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
	 *@return: java.lang.String  参数类型 支出类目:spend_type 收入类目:income_type 同步时间间隔:syn_interval
	 */
	@Column(name ="PARAM_TYPE",nullable=true,length=32)
	public java.lang.String getParamType(){
		return this.paramType;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  参数类型 支出类目:spend_type 收入类目:income_type 同步时间间隔:syn_interval
	 */
	public void setParamType(java.lang.String paramType){
		this.paramType = paramType;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户详情id
	 */
	@Column(name ="VERSION",nullable=true,length=32)
	public java.lang.String getVersion(){
		return this.version;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户详情id
	 */
	public void setVersion(java.lang.String version){
		this.version = version;
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
