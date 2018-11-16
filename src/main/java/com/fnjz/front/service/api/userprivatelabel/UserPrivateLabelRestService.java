package com.fnjz.front.service.api.userprivatelabel;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/11/7.
 */
public interface UserPrivateLabelRestService {
    /**
     * 根据账本id获取记账标签
     * @param integer
     * @param userInfoId
     * @param shareCode
     * @param income
     * @return
     */
    JSONObject getCacheLabel(Integer integer, String userInfoId, String shareCode, String income);

    /**
     * 检查用户是否已添加此标签
     * @param abId
     * @param labelId
     * @return
     */
    Integer checkExists(String abId, String labelId);

    /**
     * ios/android 添加标签
     * @param shareCode
     * @param abId
     * @param userInfoId
     * @param type
     * @return
     */
    JSONObject insertUserPrivateLabelForMap(String shareCode, String abId,String labelId, String userInfoId, String type);

    /**
     * wxapplet
     * @param abId
     * @param userInfoId
     * @return
     */
    String insertUserPrivateLabelType(String abId,String labelId, String userInfoId,String type);

    /**
     * 删除标签
     * @param shareCode
     * @param map
     * @param userInfoId
     * @param income
     * @return
     */
    JSONObject deleteUserPrivateLabelForMap(String shareCode, Map<String,Object> map, String userInfoId, String income);

    String deleteUserPrivateLabelType(String shareCode, Map<String,Object> map, String userInfoId, String income);

    List<?>  getUserCommUseType(String userInfoId, String type, Integer abId);
}
