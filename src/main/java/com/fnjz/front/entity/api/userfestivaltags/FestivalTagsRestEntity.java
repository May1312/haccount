package com.fnjz.front.entity.api.userfestivaltags;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 节日贴纸-标语表
 * Created by yhang on 2019/1/18.
 */

@Entity
@Table(name = "hbird_festival_tags")
public class FestivalTagsRestEntity implements Serializable {

    private Integer id;

    /**
     * 贴纸类型 1:春节  2:情人节
     */
    private Integer festivalType;
    /**
     * icon类型 1:贴纸icon 2:标语icon
     */
    private Integer iconType;
    /**
     * 图标
     */
    private String icon;
    /**
     * 描述
     */
    private String description;
    /**
     * 解锁状态  默认0:未解锁   1:已解锁
     */
    private Integer status;
    /**
     * 创建时间
     */
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
    @Column(name ="ICON_TYPE")
    public Integer getIconType() {
        return iconType;
    }

    public void setIconType(Integer iconType) {
        this.iconType = iconType;
    }
    @Column(name ="TAGS_ICON")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Column(name ="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name ="STATUS")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    @Column(name ="FESTIVAL_TYPE")
    public Integer getFestivalType() {
        return festivalType;
    }

    public void setFestivalType(Integer festivalType) {
        this.festivalType = festivalType;
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
     * 校验 id和UserFestivalTagsRestEntity 中的tagsId 相等
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
            if (obj.getClass()!=getClass()) {
                UserFestivalTagsRestEntity dto = (UserFestivalTagsRestEntity) obj;
                if (dto.getTagsId().intValue()==this.id.intValue()) {
                    return true;
                }
            }
        }
        return false;
    }
}
