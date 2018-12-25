package com.fnjz.front.service.api.userassets;

import com.alibaba.fastjson.JSONObject;
import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface UserAssetsRestServiceI extends CommonService{

    /**
     * 获取用户资产
     * @param userInfoId
     * @return
     */
    JSONObject getAssets(String userInfoId,String shareCode,String flag);

    void saveOrUpdateAssets(String userInfoId, Map<String,Object> map);

    void updateInitDate(String userInfoId, Map<String,Object> map);
}
