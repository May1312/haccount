package com.fnjz.front.service.api.warterorder;

import com.alibaba.fastjson.JSONArray;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WarterOrderRestServiceI extends CommonService{


    /**
     * 分页查询
     * @param time
     * @param accountBookId
     * @param curPage
     * @param pageSize
     * @return
     */
    JSONArray findListForPage(String time, String accountBookId, String curPage, String pageSize);

    /**
     * 更新
     * @param charge
     */
    Integer update(WarterOrderRestEntity charge);

    /**
     * 删除单笔记录
     * @param orderId 订单id
     * @param userInfoId 用户详情id
     * @param code  用户mobile
     * @return
     */
    Integer deleteOrder(String orderId, String userInfoId, String code);

    /**
     * 根据年月获取支出收入统计金额
     * @param time
     * @param accountBookId
     * @return
     */
    Map<String,BigDecimal> getAccount(String time, String accountBookId);
}
