package com.fnjz.utils.upload;

public enum QiNiuStorageSpace {

    //LABEL_PICTURE("http://p9twjlzxw.bkt.clouddn.com/", "label-picture"),
    LABEL_PICTURE("http://label.image.fengniaojizhang.cn/", "label-picture"),
    HEAD_PICTURE("http://p9tz2oly9.bkt.clouddn.com/", "head-picture"),
    FEEDBACK_PICTURE("http://p9vuhnix3.bkt.clouddn.com/", "feedback-picture");

    QiNiuStorageSpace(String domain, String storageSpaceName) {
        this.domain = domain;
        StorageSpaceName = storageSpaceName;
    }

    public String getDomain() {
        return domain;
    }

    public QiNiuStorageSpace setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getStorageSpaceName() {
        return StorageSpaceName;
    }

    public QiNiuStorageSpace setStorageSpaceName(String storageSpaceName) {
        StorageSpaceName = storageSpaceName;
        return this;
    }

    private String domain;
    private String StorageSpaceName;
}
