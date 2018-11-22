package com.fnjz.front.entity.api.useraccountbook;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 用户账本关联表相关
 * @date 2018-05-30 14:07:37
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_user_account_book", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class UserAccountBookRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**用户详情id*/
	private java.lang.Integer userInfoId;
	/**账本id*/
	private java.lang.Integer accountBookId;
	/**当前用户类型 0:owner 1:reader 2:writer*/
	private java.lang.Integer userType;
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
	/**是否为默认账本 1:默认账本 2:非默认*/
	private java.lang.Integer defaultFlag;

    /**初始绑定标识，仅作为受邀用户标识 1:绑定账本，未同步数据  2:已绑定，已同步*/
	private java.lang.Integer bindFlag;
	
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
	@Column(name ="USER_INFO_ID",nullable=false,precision=10,scale=0)
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  当前用户类型 0:owner 1:reader 2:writer
	 */
	@Column(name ="USER_TYPE",nullable=false,precision=10,scale=0)
	public java.lang.Integer getUserType(){
		return this.userType;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  当前用户类型 0:owner 1:reader 2:writer
	 */
	public void setUserType(java.lang.Integer userType){
		this.userType = userType;
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

	@Column(name ="DEFAULT_FLAG",nullable=true)
	public Integer getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(Integer defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

    @Column(name ="BIND_FLAG",nullable=true)
    public Integer getBindFlag() {
        return bindFlag;
    }
    public void setBindFlag(Integer bindFlag) {
        this.bindFlag = bindFlag;
    }
}
