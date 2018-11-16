package com.fnjz.front.entity.api.userprivatelabel;

/**
 * 添加用户常用标签封装类
 * Created by yhang on 2018/11/13.
 */

public class UserPrivateLabelUpdateRestDTO extends UserPrivateLabelRestDTO {

    /**
     * 二级类目id
     */
    private String typePid;
    /**
     * 三级类目id
     */
    private String typeId;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypePid() {
        return typePid;
    }

    public void setTypePid(String typePid) {
        this.typePid = typePid;
    }
}