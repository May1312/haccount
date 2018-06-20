package com.fnjz.front.entity.api.incometype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**   
 * @Title: Entity
 * @Description: 系统收入类目表相关
 * @date 2018-06-06 13:28:45
 * @version V1.0   
 *
 */
@Entity
@Table(name = "hbird_income_type", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@SuppressWarnings("serial")
public class IncomeTypeRestDTO implements java.io.Serializable {
	/**id*/
	private String id;
	/**收入类目名称*/
	private String incomeName;
	/**收入父级类目*/
	private String parentId;
	/**收入父级类目*/
	private String parentName;
	/**图标*/
	private String icon;
	/**优先级*/
	private Integer priority;
	/**常用标记,0:不常用,1:常用*/
	private Integer mark;

	private List<IncomeTypeRestDTO> IncomeTypeSons;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  id
	 */

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=36)
	public String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  id
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  收入类目名称
	 */
	@Column(name ="INCOME_NAME",nullable=true,length=32)
	public String getIncomeName(){
		return this.incomeName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  收入类目名称
	 */
	public void setIncomeName(String incomeName){
		this.incomeName = incomeName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  收入父级类目
	 */
	@Column(name ="PARENT_ID",nullable=true,length=36)
	public String getParentId(){
		return this.parentId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  收入父级类目
	 */
	public void setParentId(String parentId){
		this.parentId = parentId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  图标
	 */
	@Column(name ="ICON",nullable=true,length=256)
	public String getIcon(){
		return this.icon;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  图标
	 */
	public void setIcon(String icon){
		this.icon = icon;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  优先级
	 */
	@Column(name ="PRIORITY",nullable=true,precision=10,scale=0)
	public Integer getPriority(){
		return this.priority;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  优先级
	 */
	public void setPriority(Integer priority){
		this.priority = priority;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  常用标记,0:不常用,1:常用
	 */
	@Column(name ="MARK",nullable=true,precision=10,scale=0)
	public Integer getMark(){
		return this.mark;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  常用标记,0:不常用,1:常用
	 */
	public void setMark(Integer mark){
		this.mark = mark;
	}
	@Transient
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Transient
	public List<IncomeTypeRestDTO> getIncomeTypeSons() {
		return IncomeTypeSons;
	}

	public void setIncomeTypeSons(List<IncomeTypeRestDTO> incomeTypeSons) {
		IncomeTypeSons = new ArrayList<>();
	}
}
