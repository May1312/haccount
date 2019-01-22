package com.fnjz.front.entity.api.userfestivaltags;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户-节日贴纸-标语关联表
 * Created by yhang on 2019/1/18.
 */

@Entity
@Table(name = "hbird_user_festival_tags")
public class UserFestivalTagsRestEntity implements Serializable {

    private Integer id;

    private Integer userInfoId;

    private Integer tagsId;

    private Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name ="USER_INFO_ID")
    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    @Column(name ="TAGS_ID")
    public Integer getTagsId() {
        return tagsId;
    }

    public void setTagsId(Integer tagsId) {
        this.tagsId = tagsId;
    }

    @Column(name ="CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 重新equals方法
     * 校验 tagsId和FestivalTagsRestEntity 中的id  相等
     *
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            //自己和自己比较时,直接返回true
            if (obj == this) {
                return true;
            }
            //判断是否是同类型的对象进行比较
            if (!(obj instanceof UserFestivalTagsRestEntity)) {
                FestivalTagsRestEntity dto = (FestivalTagsRestEntity) obj;
                if (dto.getId().intValue()==this.tagsId.intValue()) {
                    return true;
                }
            }
        }
        return false;
    }
}
