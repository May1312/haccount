package com.fnjz.front.entity.api.userintegral;

import javax.persistence.Column;

/**   
 * @Title: Entity
 * @Description: 用户积分流水表相关
 * @date 2018-10-12 11:31:59
 * @version V1.0   
 *
 */


public class UserIntegralRestDTO implements java.io.Serializable {

	/**积分数*/
	private Integer integralNum;
	/**类型描述*/
	private String description;
	/**创建日期*/
	private java.util.Date createDate;

	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  积分数
	 */
	@Column(name ="INTEGRAL_NUM",nullable=true,precision=10,scale=0)
	public Integer getIntegralNum(){
		return this.integralNum;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  积分数
	 */
	public void setIntegralNum(Integer integralNum){
		this.integralNum = integralNum;
	}

	/**
	 *方法: 取得java.lang.Object
	 *@return: java.lang.Object  类型描述
	 */
	@Column(name ="DESCRIPTION",nullable=true,length=65535)
	public String getDescription(){
		return this.description;
	}

	/**
	 *方法: 设置java.lang.Object
	 *@param: java.lang.Object  类型描述
	 */
	public void setDescription(String description){
		this.description = description;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建日期
	 */
	@Column(name ="CREATE_DATE",nullable=true)
	public java.util.Date getCreateDate(){
		return this.createDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建日期
	 */
	public void setCreateDate(java.util.Date createDate){
		this.createDate = createDate;
	}
}
