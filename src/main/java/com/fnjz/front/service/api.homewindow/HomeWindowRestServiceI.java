package com.fnjz.front.service.api.homewindow;

import com.alibaba.fastjson.JSONObject;
import org.jeecgframework.core.common.service.CommonService;

public interface HomeWindowRestServiceI extends CommonService{

    /**
     * 获取首页弹框
     * @param userInfoId
     * @param shareCode
     * @return
     */
    JSONObject listForWindow(String userInfoId, String shareCode);

    /**
     * 获取轮播图list
     * @param userInfoId
     * @param shareCode
     * @return
     */
    JSONObject listForSlideShow(String userInfoId, String shareCode);
}
