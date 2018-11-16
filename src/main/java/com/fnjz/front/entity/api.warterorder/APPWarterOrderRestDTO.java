package com.fnjz.front.entity.api.warterorder;

import com.fnjz.front.entity.api.stagedinfo.StagedInfoRestEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Description: 账本流水表相关--->移动端多账本封装类  添加用户头像  昵称  账本名称
 */
@Entity
public class APPWarterOrderRestDTO extends WarterOrderRestEntity {

    /**用户自有类目id*/
    private Integer userPrivateLabelId;

    /**三级类目icon*/
    private String icon;

    /**修改者信息--昵称*/
    private String reporterNickName;

    /**修改者信息--头像*/
    private String reporterAvatar;

    /**账本名称*/
    private String abName;

    public Integer getUserPrivateLabelId() {
        return userPrivateLabelId;
    }

    public void setUserPrivateLabelId(Integer userPrivateLabelId) {
        this.userPrivateLabelId = userPrivateLabelId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getReporterNickName() {
        return reporterNickName;
    }

    public void setReporterNickName(String reporterNickName) {
        this.reporterNickName = reporterNickName;
    }

    public String getReporterAvatar() {
        return reporterAvatar;
    }

    public void setReporterAvatar(String reporterAvatar) {
        this.reporterAvatar = reporterAvatar;
    }

    public String getAbName() {
        return abName;
    }

    public void setAbName(String abName) {
        this.abName = abName;
    }
}
