package com.fnjz.front.service.api.wxappletmessagetemp;

import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;

import java.util.List;

/**
 * Created by yhang on 2018/12/1.
 */
public interface WXAppletMessageTempService {

    /**
     * 批量插入数据
     * @param list
     */
    void foreachInsert(List<WXAppletMessageTempRestEntity> list);
}
