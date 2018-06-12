package com.fnjz.front.entity.api.spend;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fnjz.front.entity.api.stagedinfo.StagedInfoRestEntity;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

/**   
 * @Title: Entity
 * @Description: 账本-支出表相关
 * @date 2018-06-06 11:59:48
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_spend", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class SpendRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**所属账本*/
	private java.lang.Integer accountBookId;
	/**单笔记录号*/
	private java.lang.String spendOrder;
	/**二级支出类目id*/
	private java.lang.String spendTypePid;
	/**二级支出类目_name*/
	private java.lang.String spendTypePname;
	/**三级支出类目id*/
	private java.lang.String spendTypeId;
	/**三级支出类目_name*/
	private java.lang.String spendTypeName;
	/**支出金额*/
	private BigDecimal spendMoney;
	/**付款方式,分期*/
	private java.lang.Integer isStaged;
	/**分期parent_id,有此项为分期父级,无此项为即时*/
	private java.lang.Integer parentId;
	/**愉悦度,0:高兴 1:一般 2:差*/
	private java.lang.Integer spendHappiness;
	/**图片记录*/
	private java.lang.String pictureUrl;
	/**更新时间*/
	private java.util.Date updateDate;
	/**创建时间*/
	private java.util.Date createDate;
	/**记账时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date spendDate;
	/**删除时间*/
	private java.util.Date delDate;
	/**创建者id*/
	private java.lang.Integer createBy;
	/**创建者名称*/
	private java.lang.String createName;
	/**修改者id*/
	private java.lang.Integer updateBy;
	/**修改者名称*/
	private java.lang.String updateName;
	/**删除状态,0:有效 1:删除*/
	private java.lang.Integer delflag;
	/**备注*/
	private java.lang.String remark;
	//封装分期详情对象
	private StagedInfoRestEntity stagedInfo;
	
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
	@Column(name ="SPEND_ORDER",nullable=true,length=32)
	public java.lang.String getSpendOrder(){
		return this.spendOrder;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  单笔记录号
	 */
	public void setSpendOrder(java.lang.String spendOrder){
		this.spendOrder = spendOrder;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级支出类目id
	 */
	@Column(name ="SPEND_TYPE_PID",nullable=true,length=36)
	public java.lang.String getSpendTypePid(){
		return this.spendTypePid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级支出类目id
	 */
	public void setSpendTypePid(java.lang.String spendTypePid){
		this.spendTypePid = spendTypePid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级支出类目_name
	 */
	@Column(name ="SPEND_TYPE_PNAME",nullable=true,length=32)
	public java.lang.String getSpendTypePname(){
		return this.spendTypePname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级支出类目_name
	 */
	public void setSpendTypePname(java.lang.String spendTypePname){
		this.spendTypePname = spendTypePname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级支出类目id
	 */
	@Column(name ="SPEND_TYPE_ID",nullable=true,length=36)
	public java.lang.String getSpendTypeId(){
		return this.spendTypeId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级支出类目id
	 */
	public void setSpendTypeId(java.lang.String spendTypeId){
		this.spendTypeId = spendTypeId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级支出类目_name
	 */
	@Column(name ="SPEND_TYPE_NAME",nullable=true,length=32)
	public java.lang.String getSpendTypeName(){
		return this.spendTypeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级支出类目_name
	 */
	public void setSpendTypeName(java.lang.String spendTypeName){
		this.spendTypeName = spendTypeName;
	}
	/**
	 *方法: 取得BigDecimal
	 *@return: BigDecimal  支出金额
	 */
	@Column(name ="SPEND_MONEY",nullable=true,precision=32,scale=2)
	public BigDecimal getSpendMoney(){
		return this.spendMoney;
	}

	/**
	 *方法: 设置BigDecimal
	 *@param: BigDecimal  支出金额
	 */
	public void setSpendMoney(BigDecimal spendMoney){
		this.spendMoney = spendMoney;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  付款方式,分期
	 */
	@Column(name ="IS_STAGED",nullable=true,precision=10,scale=0)
	public java.lang.Integer getIsStaged(){
		return this.isStaged;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  付款方式,分期
	 */
	public void setIsStaged(java.lang.Integer isStaged){
		this.isStaged = isStaged;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  分期parent_id,有此项为分期父级,无此项为即时
	 */
	@Column(name ="PARENT_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getParentId(){
		return this.parentId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  分期parent_id,有此项为分期父级,无此项为即时
	 */
	public void setParentId(java.lang.Integer parentId){
		this.parentId = parentId;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  愉悦度,0:高兴 1:一般 2:差
	 */
	@Column(name ="SPEND_HAPPINESS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getSpendHappiness(){
		return this.spendHappiness;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  愉悦度,0:高兴 1:一般 2:差
	 */
	public void setSpendHappiness(java.lang.Integer spendHappiness){
		this.spendHappiness = spendHappiness;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图片记录
	 */
	@Column(name ="PICTURE_URL",nullable=true,length=255)
	public java.lang.String getPictureUrl(){
		return this.pictureUrl;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图片记录
	 */
	public void setPictureUrl(java.lang.String pictureUrl){
		this.pictureUrl = pictureUrl;
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
	@Column(name ="SPEND_DATE",nullable=true)
	public java.util.Date getSpendDate(){
		return this.spendDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  记账时间
	 */
	public void setSpendDate(java.util.Date spendDate){
		this.spendDate = spendDate;
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
	 *@return: java.lang.Integer  删除状态,0:有效 1:删除
	 */
	@Column(name ="DELFLAG",nullable=true,precision=10,scale=0)
	public java.lang.Integer getDelflag(){
		return this.delflag;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  删除状态,0:有效 1:删除
	 */
	public void setDelflag(java.lang.Integer delflag){
		this.delflag = delflag;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备注
	 */
	@Column(name ="REMARK",nullable=true,length=65535)
	public java.lang.String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备注
	 */
	public void setRemark(java.lang.String remark){
		this.remark = remark;
	}
	@Transient
	public StagedInfoRestEntity getStagedInfo() {
		return stagedInfo;
	}

	public void setStagedInfo(StagedInfoRestEntity stagedInfo) {
		this.stagedInfo = stagedInfo;
	}
}
