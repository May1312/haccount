package com.fnjz.front.entity.api.usercommtypepriority;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 用户所属类目排序表相关
 * @date 2018-06-21 15:47:16
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_comm_type_priority", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserCommTypePriorityRestEntity implements java.io.Serializable {

	public UserCommTypePriorityRestEntity() {}

	public UserCommTypePriorityRestEntity(Integer userInfoId, Integer type, String relation) {
		this.userInfoId = userInfoId;
		this.type = type;
		this.relation = relation;
	}

	public UserCommTypePriorityRestEntity(Integer userInfoId, Integer type,Integer abTypeId, String relation) {
		this.userInfoId = userInfoId;
		this.type = type;
		this.relation = relation;
		this.abTypeId = abTypeId;
	}

	/**id*/
	private java.lang.Integer id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**用户所属类目类型  1:支出 2:收入*/
	private java.lang.Integer type;
	/**创建时间*/
	private java.util.Date createDate;
	/**类目优先级关系*/
	private java.lang.String relation;
	//private java.lang.String relationOld;
	/**更新时间*/
	private java.util.Date updateDate;
	/**账本id*/
	private java.lang.Integer accountBookId;

	/**账本类型id*/
	private java.lang.Integer abTypeId;
	
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
	 *@return: java.lang.Integer  用户详情id
	 */
	@Column(name ="USER_INFO_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getUserInfoId(){
		return this.userInfoId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  用户详情id
	 */
	public void setUserInfoId(java.lang.Integer userInfoId){
		this.userInfoId = userInfoId;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  用户所属类目类型  1:支出 2:收入
	 */
	@Column(name ="TYPE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getType(){
		return this.type;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  用户所属类目类型  1:支出 2:收入
	 */
	public void setType(java.lang.Integer type){
		this.type = type;
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
	 *@return: java.lang.String  类目优先级关系
	 */
	@Column(name ="RELATION",nullable=true)
	public java.lang.String getRelation(){
		return this.relation;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  类目优先级关系
	 */
	public void setRelation(java.lang.String relation){
		this.relation = relation;
	}


	/*@Column(name ="RELATION_OLD",nullable=true)
	public String getRelationOld() {
		return relationOld;
	}

	public void setRelationOld(String relationOld) {
		this.relationOld = relationOld;
	}*/

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

	@Column(name ="ACCOUNT_BOOK_ID")
	public Integer getAccountBookId() {
		return accountBookId;
	}

	public void setAccountBookId(Integer accountBookId) {
		this.accountBookId = accountBookId;
	}

	@Column(name ="AB_TYPE_ID")
	public Integer getAbTypeId() {
		return abTypeId;
	}

	public void setAbTypeId(Integer abTypeId) {
		this.abTypeId = abTypeId;
	}
}
