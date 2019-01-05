package com.fnjz.front.entity.api.buriedpointtype;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hbird_buried_point_type", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class BuriedPointTypeRestEntity implements java.io.Serializable {
	/**id*/
	private Integer id;
	/**埋点描述*/
	private String pointDesc;
	/**创建时间*/
	private Date createTime;


	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  id
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true,nullable=false)
	public Integer getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  id
	 */
	public void setId(Integer id){
		this.id = id;
	}

	public String getPointDesc() {
		return pointDesc;
	}

	public void setPointDesc(String pointDesc) {
		this.pointDesc = pointDesc;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
