package com.fnjz.front.entity.api.userbadge;

/**
 * 徽章类型--标签 dto
 * Created by yhang on 2019/2/22.
 */
public class BadgeLabelRestDTO {

    /**
     * 徽章类型id
     */
    private Integer badgeTypeId;
    /**
     * 记账标签id
     */
    private String labelId;
    /**
     * 记账标签名称
     */
    private String labelName;

    public Integer getBadgeTypeId() {
        return badgeTypeId;
    }

    public void setBadgeTypeId(Integer badgeTypeId) {
        this.badgeTypeId = badgeTypeId;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
