package com.fnjz.front.entity.api.accountbooktype;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 账本类型相关
 * @date 2018-11-10 16:44:42
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_account_book_type", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AccountBookTypeRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**账本类型名称*/
	private java.lang.String abTypeName;
	/**图标*/
	private java.lang.String icon;
	/**描述图标*/
	private java.lang.String iconDescribe;
	/**账本属性(与预算时间的设置相关) 1:普通日常账本 2:场景账本(即需设置预算起始时间)*/
	private java.lang.Integer typeBudget;

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
	 *@return: java.lang.String  账本类型名称
	 */
	@Column(name ="AB_TYPE_NAME",nullable=true,length=64)
	public java.lang.String getAbTypeName(){
		return this.abTypeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  账本类型名称
	 */
	public void setAbTypeName(java.lang.String abTypeName){
		this.abTypeName = abTypeName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图标
	 */
	@Column(name ="ICON",nullable=true,length=255)
	public java.lang.String getIcon(){
		return this.icon;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图标
	 */
	public void setIcon(java.lang.String icon){
		this.icon = icon;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  账本属性(与预算时间的设置相关) 1:普通日常账本 2:场景账本(即需设置预算起始时间)
	 */
	@Column(name ="TYPE_BUDGET",nullable=true,precision=10,scale=0)
	public java.lang.Integer getTypeBudget(){
		return this.typeBudget;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  账本属性(与预算时间的设置相关) 1:普通日常账本 2:场景账本(即需设置预算起始时间)
	 */
	public void setTypeBudget(java.lang.Integer typeBudget){
		this.typeBudget = typeBudget;
	}

	public String getIconDescribe() {
		return iconDescribe;
	}

	public void setIconDescribe(String iconDescribe) {
		this.iconDescribe = iconDescribe;
	}
}
