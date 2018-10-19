package com.fnjz.front.entity.api.userintegral;

import javax.persistence.Column;

/**
 * @Title: Entity
 * @Description: 积分排行榜bean
 * @date 2018-10-12 11:31:59
 * @version V1.0   
 *
 */


public class UserIntegralTopRestDTO implements java.io.Serializable {

	/**积分数*/
	private Integer integralNum;
	/**昵称**/
	private String nickName;
	/**头像url**/
	private String avatarUrl;
	/**排名**/
	private Integer rank;

	@Column(name ="INTEGRAL_NUM")
	public Integer getIntegralNum() {
		return integralNum;
	}

	public void setIntegralNum(Integer integralNum) {
		this.integralNum = integralNum;
	}
	@Column(name ="NICK_NAME")
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	@Column(name ="AVATAR_URL")
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	@Column(name ="rank")
	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}
}
