package com.fnjz.front.service.api.wxappletmessagetemp;

import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletAccountNotifyTempRestEntity;
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

    /**
     * 统计月
     */
    void foreachInsert2ForMonth(String begin,String end);

    /**
     * 获取待发送数据
     * @return
     */
    List<WXAppletAccountNotifyTempRestEntity> getAccountNotifyData();

    /**
     * 删除临时表数据
     */
    void deleteDate();
}
