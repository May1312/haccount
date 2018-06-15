package com.fnjz.front.entity.api.warterorder;

import java.math.BigDecimal;
import javax.persistence.*;
import com.fnjz.front.entity.api.stagedinfo.StagedInfoRestEntity;
import com.google.gson.annotations.Expose;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

/**   
 * @Title: Entity
 * @Description: 账本流水表相关
 * @date 2018-06-14 13:15:47
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_water_order", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) //为空字段不返回
@SuppressWarnings("serial")
public class WarterOrderRestEntity implements java.io.Serializable {
	/**流水记录号*/
	private java.lang.String id;
	/**单笔金额*/
	private BigDecimal money;
	/**所属账本*/
	private java.lang.Integer accountBookId;
	/**订单类型 1:支出  2:收入*/
	private java.lang.Integer orderType;
	/**付款方式 1:即时 2:分期*/
	private java.lang.Integer isStaged;
	/**愉悦度,0:高兴 1:一般 2:差*/
	private java.lang.Integer spendHappiness;
	/**使用度(保留字段)*/
	private java.lang.Integer useDegree;
	/**二级类目id*/
	private java.lang.String typePid;
	/**二级类目_name*/
	private java.lang.String typePname;
	/**三级类目id*/
	private java.lang.String typeId;
	/**三级类目_name*/
	private java.lang.String typeName;
	/**分期parent_id,有此项为分期父级,无此项为即时*/
	private java.lang.Integer parentId;
	/**图片记录*/
	private java.lang.String pictureUrl;
	/**更新时间*/
	private java.util.Date updateDate;
	/**创建时间*/
	private java.util.Date createDate;
	/**记账时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date chargeDate;
	/**删除状态,0:有效 1:删除*/
	private java.lang.Integer delflag;
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
	/**备注*/
	private java.lang.String remark;
	//封装分期详情对象
	private StagedInfoRestEntity stagedInfo;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  流水记录号
	 */
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=32)
	public java.lang.String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  流水记录号
	 */
	public void setId(java.lang.String id){
		this.id = id;
	}
	/**
	 *方法: 取得BigDecimal
	 *@return: BigDecimal  单笔金额
	 */
	@Column(name ="MONEY",nullable=true,precision=32,scale=2)
	public BigDecimal getMoney(){
		return this.money;
	}

	/**
	 *方法: 设置BigDecimal
	 *@param: BigDecimal  单笔金额
	 */
	public void setMoney(BigDecimal money){
		this.money = money;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  订单类型 1:支出  2:收入
	 */
	@Column(name ="ORDER_TYPE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getOrderType(){
		return this.orderType;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  订单类型 1:支出  2:收入
	 */
	public void setOrderType(java.lang.Integer orderType){
		this.orderType = orderType;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  付款方式 1:即时 2:分期
	 */
	@Column(name ="IS_STAGED",nullable=true,precision=10,scale=0)
	public java.lang.Integer getIsStaged(){
		return this.isStaged;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  付款方式 1:即时 2:分期
	 */
	public void setIsStaged(java.lang.Integer isStaged){
		this.isStaged = isStaged;
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
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  使用度(保留字段)
	 */
	@Column(name ="USE_DEGREE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getUseDegree(){
		return this.useDegree;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  使用度(保留字段)
	 */
	public void setUseDegree(java.lang.Integer useDegree){
		this.useDegree = useDegree;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级类目id
	 */
	@Column(name ="TYPE_PID",nullable=true,length=36)
	public java.lang.String getTypePid(){
		return this.typePid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级类目id
	 */
	public void setTypePid(java.lang.String typePid){
		this.typePid = typePid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级类目_name
	 */
	@Column(name ="TYPE_PNAME",nullable=true,length=32)
	public java.lang.String getTypePname(){
		return this.typePname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级类目_name
	 */
	public void setTypePname(java.lang.String typePname){
		this.typePname = typePname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目id
	 */
	@Column(name ="TYPE_ID",nullable=true,length=36)
	public java.lang.String getTypeId(){
		return this.typeId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目id
	 */
	public void setTypeId(java.lang.String typeId){
		this.typeId = typeId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目_name
	 */
	@Column(name ="TYPE_NAME",nullable=true,length=32)
	public java.lang.String getTypeName(){
		return this.typeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目_name
	 */
	public void setTypeName(java.lang.String typeName){
		this.typeName = typeName;
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
	@Column(name ="CHARGE_DATE",nullable=true)
	public java.util.Date getChargeDate(){
		return this.chargeDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  记账时间
	 */
	public void setChargeDate(java.util.Date chargeDate){
		this.chargeDate = chargeDate;
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
	@Column(name ="REMARK",nullable=true,length=65535)
	public java.lang.String getRemark(){
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Transient
	public StagedInfoRestEntity getStagedInfo() {
		return stagedInfo;
	}

	public void setStagedInfo(StagedInfoRestEntity stagedInfo) {
		this.stagedInfo = stagedInfo;
	}

	@Column(name ="ACCOUNT_BOOK_ID",nullable=true,precision=10,scale=0)
	public java.lang.Integer getAccountBookId(){
		return this.accountBookId;
	}

	public void setAccountBookId(Integer accountBookId) {
		this.accountBookId = accountBookId;
	}
}
