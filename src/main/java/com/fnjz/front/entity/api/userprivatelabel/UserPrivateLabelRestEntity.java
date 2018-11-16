package com.fnjz.front.entity.api.userprivatelabel;

import javax.persistence.*;

/**
 * Created by yhang on 2018/11/7.
 */
@Entity
@Table(name = "hbird_user_private_label", schema = "")
public class UserPrivateLabelRestEntity implements java.io.Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private java.lang.Integer id;
    /**
     * 用户详情id
     */
    private java.lang.Integer userInfoId;
    /**
     * 二级类目id
     */
    private java.lang.String typePid;
    /**
     * 二级类目名称
     */
    private java.lang.String typePname;
    /**
     * 三级类目id
     */
    private java.lang.String typeId;
    /**
     * 三级类目名称
     */
    private java.lang.String typeName;
    /**
     * 图标
     */
    private java.lang.String icon;
    /**
     * 优先级
     */
    private java.lang.Integer priority;
    /**
     * 标签属性 1:支出 2:收入
     */
    private java.lang.Integer property;
    /**
     * 标签类型 1:系统分配  2:用户自建
     */
    private java.lang.Integer type;
    /**
     * 标签状态 1:有效  0:失效
     */
    private java.lang.Integer status;
    /**
     * 账本id
     */
    private java.lang.Integer accountBookId;
    /**
     * 账本类型标签表对应id
     */
    private java.lang.Integer abTypeLabelId;

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
    @Column(name ="ICON")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    @Column(name ="PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    @Column(name ="PROPERTY")
    public Integer getProperty() {
        return property;
    }

    public void setProperty(Integer property) {
        this.property = property;
    }
    @Column(name ="TYPE")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    @Column(name ="STATUS")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    @Column(name ="ACCOUNT_BOOK_ID",nullable=true,precision=10,scale=0)
    public Integer getAccountBookId() {
        return accountBookId;
    }

    public void setAccountBookId(Integer accountBookId) {
        this.accountBookId = accountBookId;
    }
    @Column(name ="TYPE_PID")
    public String getTypePid() {
        return typePid;
    }

    public void setTypePid(String typePid) {
        this.typePid = typePid;
    }
    @Column(name ="TYPE_PNAME")
    public String getTypePname() {
        return typePname;
    }

    public void setTypePname(String typePname) {
        this.typePname = typePname;
    }
    @Column(name ="TYPE_ID")
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    @Column(name ="TYPE_NAME")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getAbTypeLabelId() {
        return abTypeLabelId;
    }

    public void setAbTypeLabelId(Integer abTypeLabelId) {
        this.abTypeLabelId = abTypeLabelId;
    }
}