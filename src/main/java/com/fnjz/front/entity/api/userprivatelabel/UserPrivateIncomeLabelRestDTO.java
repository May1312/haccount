package com.fnjz.front.entity.api.userprivatelabel;

/**
 * Created by yhang on 2018/11/7.
 */

public class UserPrivateIncomeLabelRestDTO implements java.io.Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 三级类目名称
     */
    private String incomeName;
    /**
     * 图标
     */
    private String icon;
    /**
     * 账本id
     */
    private Integer accountBookId;

    private Integer priority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getAccountBookId() {
        return accountBookId;
    }

    public void setAccountBookId(Integer accountBookId) {
        this.accountBookId = accountBookId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getIncomeName() {
        return incomeName;
    }

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }
}