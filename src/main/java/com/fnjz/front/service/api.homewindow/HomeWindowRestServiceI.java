package com.fnjz.front.service.api.homewindow;

import com.google.gson.JsonObject;
import org.jeecgframework.core.common.service.CommonService;

public interface HomeWindowRestServiceI extends CommonService{

    /**
     * 获取首页弹框
     * @param userInfoId
     * @param shareCode
     * @return
     */
    JsonObject listForWindow(String userInfoId, String shareCode);
}
