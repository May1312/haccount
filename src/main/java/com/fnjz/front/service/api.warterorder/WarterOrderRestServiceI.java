package com.fnjz.front.service.api.warterorder;

import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.core.common.service.CommonService;

public interface WarterOrderRestServiceI extends CommonService{


    /**
     * 分页查询
     * @param time
     * @param accountBookId
     * @param curPage
     * @param pageSize
     * @return
     */
    PageRest findListForPage(String time,String accountBookId, Integer curPage, Integer pageSize);

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
}
