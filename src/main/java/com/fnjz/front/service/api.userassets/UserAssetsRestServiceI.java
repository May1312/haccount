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

    /**
     * 添加到用户默认账户类型
     * @param userInfoId
     * @param map
     */
    void addAT2Mark(String userInfoId, Map<String,Object> map);

    /**
     * 移除用户默认账户类型
     * @param userInfoId
     * @param map
     */
    void deleteAT2Mark(String userInfoId, Map<String,Object> map);

    /**
     * 获取用户资产 v2
     * @param userInfoId
     * @param shareCode
     * @param flag
     * @return
     */
    JSONObject getAssetsv2(String userInfoId, String shareCode, String flag);

    /**
     * v2
     * @param userInfoId
     * @param map
     */
    void saveOrUpdateAssetsv2(String userInfoId, Map<String,Object> map);
}
