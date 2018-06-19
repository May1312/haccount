package com.fnjz.front.entity.api.warterorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fnjz.front.entity.api.stagedinfo.StagedInfoRestEntity;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;

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
public class WarterOrderRestDTO implements java.io.Serializable {
	/**流水记录号*/
	private String id;
	/**单笔金额*/
	private BigDecimal money;
	/**所属账本*/
	private Integer accountBookId;
	/**订单类型 1:支出  2:收入*/
	private Integer orderType;
	/**付款方式 1:即时 2:分期*/
	private Integer isStaged;
	/**愉悦度,0:高兴 1:一般 2:差*/
	private Integer spendHappiness;
	/**使用度(保留字段)*/
	private Integer useDegree;
	/**二级类目id*/
	private String typePid;
	/**二级类目_name*/
	private String typePname;
	/**三级类目id*/
	private String typeId;
	/**三级类目_name*/
	private String typeName;
	/**分期parent_id,有此项为分期父级,无此项为即时*/
	private Integer parentId;
	/**图片记录*/
	private String pictureUrl;
	/**创建时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date createDate;
	/**记账时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date chargeDate;
	/**备注*/
	private String remark;
	//封装分期详情对象
	private StagedInfoRestEntity stagedInfo;
	/**三级类目icon*/
	private String icon;
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  流水记录号
	 */

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=32)
	public String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  流水记录号
	 */
	public void setId(String id){
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
	public Integer getOrderType(){
		return this.orderType;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  订单类型 1:支出  2:收入
	 */
	public void setOrderType(Integer orderType){
		this.orderType = orderType;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  付款方式 1:即时 2:分期
	 */
	@Column(name ="IS_STAGED",nullable=true,precision=10,scale=0)
	public Integer getIsStaged(){
		return this.isStaged;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  付款方式 1:即时 2:分期
	 */
	public void setIsStaged(Integer isStaged){
		this.isStaged = isStaged;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  愉悦度,0:高兴 1:一般 2:差
	 */
	@Column(name ="SPEND_HAPPINESS",nullable=true,precision=10,scale=0)
	public Integer getSpendHappiness(){
		return this.spendHappiness;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  愉悦度,0:高兴 1:一般 2:差
	 */
	public void setSpendHappiness(Integer spendHappiness){
		this.spendHappiness = spendHappiness;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  使用度(保留字段)
	 */
	@Column(name ="USE_DEGREE",nullable=true,precision=10,scale=0)
	public Integer getUseDegree(){
		return this.useDegree;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  使用度(保留字段)
	 */
	public void setUseDegree(Integer useDegree){
		this.useDegree = useDegree;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级类目id
	 */
	@Column(name ="TYPE_PID",nullable=true,length=36)
	public String getTypePid(){
		return this.typePid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级类目id
	 */
	public void setTypePid(String typePid){
		this.typePid = typePid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  二级类目_name
	 */
	@Column(name ="TYPE_PNAME",nullable=true,length=32)
	public String getTypePname(){
		return this.typePname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  二级类目_name
	 */
	public void setTypePname(String typePname){
		this.typePname = typePname;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目id
	 */
	@Column(name ="TYPE_ID",nullable=true,length=36)
	public String getTypeId(){
		return this.typeId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目id
	 */
	public void setTypeId(String typeId){
		this.typeId = typeId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  三级类目_name
	 */
	@Column(name ="TYPE_NAME",nullable=true,length=32)
	public String getTypeName(){
		return this.typeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  三级类目_name
	 */
	public void setTypeName(String typeName){
		this.typeName = typeName;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  分期parent_id,有此项为分期父级,无此项为即时
	 */
	@Column(name ="PARENT_ID",nullable=true,precision=10,scale=0)
	public Integer getParentId(){
		return this.parentId;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  分期parent_id,有此项为分期父级,无此项为即时
	 */
	public void setParentId(Integer parentId){
		this.parentId = parentId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图片记录
	 */
	@Column(name ="PICTURE_URL",nullable=true,length=255)
	public String getPictureUrl(){
		return this.pictureUrl;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图片记录
	 */
	public void setPictureUrl(String pictureUrl){
		this.pictureUrl = pictureUrl;
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

	@Column(name ="REMARK",nullable=true,length=65535)
	public String getRemark(){
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
	public Integer getAccountBookId(){
		return this.accountBookId;
	}

	public void setAccountBookId(Integer accountBookId) {
		this.accountBookId = accountBookId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
