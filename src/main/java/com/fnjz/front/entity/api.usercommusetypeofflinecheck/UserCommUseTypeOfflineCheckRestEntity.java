package com.fnjz.front.entity.api.usercommusetypeofflinecheck;

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
 * @Description: 离线-用户常用类目/排序关系检查表
 * @date 2018-09-04 16:22:10
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_comm_use_type_offline_check", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserCommUseTypeOfflineCheckRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**账本id*/
	private java.lang.Integer accountBookId;
	/**类型 支出类目:spend_type 收入类目:income_type 排序关系:type_priority*/
	private java.lang.String type;
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
	 *@return: java.lang.Integer  账本id
	 */
	@Column(name ="ACCOUNT_BOOK_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getAccountBookId(){
		return this.accountBookId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  账本id
	 */
	public void setAccountBookId(java.lang.Integer accountBookId){
		this.accountBookId = accountBookId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  类型 支出类目:spend_type 收入类目:income_type 排序关系:type_priority
	 */
	@Column(name ="TYPE",nullable=true,length=32)
	public java.lang.String getType(){
		return this.type;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  类型 支出类目:spend_type 收入类目:income_type 排序关系:type_priority
	 */
	public void setType(java.lang.String type){
		this.type = type;
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
