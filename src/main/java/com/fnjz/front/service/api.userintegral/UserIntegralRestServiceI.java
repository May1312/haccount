package com.fnjz.front.service.api.userintegral;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.PageRest;
import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface UserIntegralRestServiceI extends CommonService{

    /**
     * 添加积分流水记录
     * @param userInfoId
     * @param map
     */
    void signInIntegral(String userInfoId, String shareCode,Map<String,String> map);

    /**
     * 分页查询
     * @param userInfoId
     * @return
     */
    PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize);

    /**
     * 获取今日任务/新手任务完成情况
     * @param userInfoId
     * @return
     */
    JSONObject integralTask(String userInfoId,String shareCode);
}
