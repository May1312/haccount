package com.fnjz.front.entity.api.usersignin;

import javax.persistence.Column;
import java.util.Date;

/**   
 * @Title: Entity
 * @Description: 用户签到表相关
 * @date 2018-10-10 14:23:20
 * @version V1.0   
 *
 */

public class UserSignInRestDTO {

	/**签到日期*/
	private Date signInDate;

	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  签到日期
	 */
	@Column(name ="SIGN_IN_DATE",nullable=true)
	public Date getSignInDate(){
		return this.signInDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  签到日期
	 */
	public void setSignInDate(Date signInDate){
		this.signInDate = signInDate;
	}

}
