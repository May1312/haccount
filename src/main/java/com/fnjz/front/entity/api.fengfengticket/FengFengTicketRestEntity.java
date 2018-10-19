package com.fnjz.front.entity.api.fengfengticket;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 丰丰票相关
 * @date 2018-10-13 13:00:07
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_fengfeng_ticket", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class FengFengTicketRestEntity implements java.io.Serializable {
	/**id*/
	private java.lang.Integer id;
	/**行为类别  ,TodayTask_今日任务, NewbieTask_新手任务, SignIn_签到 */
	private java.lang.String categoryOfBehavior;
	/**获取方式  ,Inviting_friends -邀请好友, Write_down_an_account-记一笔账, 
   Get_a_new_Badge-获得新徽章, binding_phone_or_wx-绑定手机号或微信 ，Setting_up_budget-设置预算,Setting_up_savings_efficiency  -设置存钱效率 ,Record_keeping_mood -记录记账心情
		Perfecting_personal_data - 完善个人资料  Setup_account_reminder 设置记账提醒 ，Check_in -补签到 */
	private java.lang.String acquisitionMode;
	/**周期*/
	private java.lang.String cycle;
	/**周期内次数*/
	private java.lang.Integer numberOfCycles;
	/**行为票值*/
	private java.lang.Integer behaviorTicketValue;
	/**备份*/
	private java.lang.String remark;
	/**上线状态:0_下线 1_上线*/
	private java.lang.Integer status;
	/**创建时间*/
	private java.util.Date createDate;
	/**更新时间*/
	private java.util.Date updateDate;
	/**上线时间*/
	private java.util.Date uptime;
	/**下线时间*/
	private java.util.Date downtime;
	/**创建人*/
	private java.lang.String createName;
	/**修改人名字*/
	private java.lang.String updateName;
	/**删除标记*/
	private java.lang.Integer delflag;
	/**删除时间*/
	private java.util.Date delDate;
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  id
	 */
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=36)
	public java.lang.Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  id
	 */
	public void setId(java.lang.Integer id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  行为类别  ,TodayTask_今日任务, NewbieTask_新手任务, SignIn_签到 
	 */
	@Column(name ="CATEGORY_OF_BEHAVIOR",nullable=true,length=64)
	public java.lang.String getCategoryOfBehavior(){
		return this.categoryOfBehavior;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  行为类别  ,TodayTask_今日任务, NewbieTask_新手任务, SignIn_签到 
	 */
	public void setCategoryOfBehavior(java.lang.String categoryOfBehavior){
		this.categoryOfBehavior = categoryOfBehavior;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  获取方式  ,Inviting_friends -邀请好友, Write_down_an_account-记一笔账, 
   Get_a_new_Badge-获得新徽章, binding_phone_or_wx-绑定手机号或微信 ，Setting_up_budget-设置预算,Setting_up_savings_efficiency  -设置存钱效率 ,Record_keeping_mood -记录记账心情
		Perfecting_personal_data - 完善个人资料  Setup_account_reminder 设置记账提醒 ，Check_in -补签到 
	 */
	@Column(name ="ACQUISITION_MODE",nullable=true,length=64)
	public java.lang.String getAcquisitionMode(){
		return this.acquisitionMode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  获取方式  ,Inviting_friends -邀请好友, Write_down_an_account-记一笔账, 
   Get_a_new_Badge-获得新徽章, binding_phone_or_wx-绑定手机号或微信 ，Setting_up_budget-设置预算,Setting_up_savings_efficiency  -设置存钱效率 ,Record_keeping_mood -记录记账心情
		Perfecting_personal_data - 完善个人资料  Setup_account_reminder 设置记账提醒 ，Check_in -补签到 
	 */
	public void setAcquisitionMode(java.lang.String acquisitionMode){
		this.acquisitionMode = acquisitionMode;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  周期
	 */
	@Column(name ="CYCLE",nullable=true,length=1024)
	public java.lang.String getCycle(){
		return this.cycle;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  周期
	 */
	public void setCycle(java.lang.String cycle){
		this.cycle = cycle;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  周期内次数
	 */
	@Column(name ="NUMBER_OF_CYCLES",nullable=true,precision=10,scale=0)
	public java.lang.Integer getNumberOfCycles(){
		return this.numberOfCycles;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  周期内次数
	 */
	public void setNumberOfCycles(java.lang.Integer numberOfCycles){
		this.numberOfCycles = numberOfCycles;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  行为票值
	 */
	@Column(name ="BEHAVIOR_TICKET_VALUE",nullable=true,precision=10,scale=0)
	public java.lang.Integer getBehaviorTicketValue(){
		return this.behaviorTicketValue;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  行为票值
	 */
	public void setBehaviorTicketValue(java.lang.Integer behaviorTicketValue){
		this.behaviorTicketValue = behaviorTicketValue;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备份
	 */
	@Column(name ="REMARK",nullable=true,length=256)
	public java.lang.String getRemark(){
		return this.remark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备份
	 */
	public void setRemark(java.lang.String remark){
		this.remark = remark;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  上线状态:0_下线 1_上线
	 */
	@Column(name ="STATUS",nullable=true,precision=10,scale=0)
	public java.lang.Integer getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  上线状态:0_下线 1_上线
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
	 *@return: java.util.Date  上线时间
	 */
	@Column(name ="UPTIME",nullable=true)
	public java.util.Date getUptime(){
		return this.uptime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  上线时间
	 */
	public void setUptime(java.util.Date uptime){
		this.uptime = uptime;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  下线时间
	 */
	@Column(name ="DOWNTIME",nullable=true)
	public java.util.Date getDowntime(){
		return this.downtime;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  下线时间
	 */
	public void setDowntime(java.util.Date downtime){
		this.downtime = downtime;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人
	 */
	@Column(name ="CREATE_NAME",nullable=true,length=32)
	public java.lang.String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人
	 */
	public void setCreateName(java.lang.String createName){
		this.createName = createName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  修改人名字
	 */
	@Column(name ="UPDATE_NAME",nullable=true,length=32)
	public java.lang.String getUpdateName(){
		return this.updateName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  修改人名字
	 */
	public void setUpdateName(java.lang.String updateName){
		this.updateName = updateName;
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
}
