package com.fnjz.front.entity.api.spendtype;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**   
 * @Title: Entity
 * @Description: 系统支出类目表相关
 * @date 2018-06-06 12:01:14
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_spend_type", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@SuppressWarnings("serial")
public class SpendTypeRestEntity implements java.io.Serializable {
	/**类目id*/
	private java.lang.String id;
	/**支出类目名称*/
	private java.lang.String spendName;
	/**父级类目*/
	private java.lang.String parentId;
	/**图标*/
	private java.lang.String icon;
	/**状态(0:下线,1:上线)*/
	private java.lang.String status;
	/**优先级*/
	private java.lang.Integer priority;
	/**常用字段,0:非常用,1:常用*/
	private java.lang.Integer mark;
	/**更新时间*/
	private java.util.Date updateDate;
	/**创建时间*/
	private java.util.Date createDate;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	private List<SpendTypeRestEntity> SpendTypeSons;
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  类目id
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
	 *@param: java.lang.String  类目id
	 */
	public void setId(java.lang.String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  支出类目名称
	 */
	@Column(name ="SPEND_NAME",nullable=false,length=32)
	public java.lang.String getSpendName(){
		return this.spendName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  支出类目名称
	 */
	public void setSpendName(java.lang.String spendName){
		this.spendName = spendName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  父级类目
	 */
	@Column(name ="PARENT_ID",nullable=true,length=36)
	public java.lang.String getParentId(){
		return this.parentId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  父级类目
	 */
	public void setParentId(java.lang.String parentId){
		this.parentId = parentId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图标
	 */
	@Column(name ="ICON",nullable=true,length=256)
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
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  状态(0:下线,1:上线)
	 */
	@Column(name ="STATUS",nullable=false,length=2)
	public java.lang.String getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  状态(0:下线,1:上线)
	 */
	public void setStatus(java.lang.String status){
		this.status = status;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  优先级
	 */
	@Column(name ="PRIORITY",nullable=true,precision=10,scale=0)
	public java.lang.Integer getPriority(){
		return this.priority;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  优先级
	 */
	public void setPriority(java.lang.Integer priority){
		this.priority = priority;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  常用字段,0:非常用,1:常用
	 */
	@Column(name ="MARK",nullable=true,precision=10,scale=0)
	public java.lang.Integer getMark(){
		return this.mark;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  常用字段,0:非常用,1:常用
	 */
	public void setMark(java.lang.Integer mark){
		this.mark = mark;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  更新时间
	 */
	@Column(name ="UPDATE_DATE",nullable=true)
	@Transient
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
	@Transient
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
	@Transient
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
	@Transient
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
	@Transient
	public List<SpendTypeRestEntity> getSpendTypeSons() {
		return SpendTypeSons;
	}

	public void setSpendTypeSons(List<SpendTypeRestEntity> spendTypeSons) {
		SpendTypeSons = new ArrayList<>();
	}
}
