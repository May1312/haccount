package com.fnjz.front.service.api.integralsactivity;

import com.fnjz.front.entity.api.integralsactivity.IntegralsActivityRestEntity;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivityRestDTO;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivitySumRestDTO;
import com.fnjz.front.entity.api.integralsactivityrange.IntegralsActivityRangeRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ReportShopRestDTO;

import java.util.List;

/**
 * Created by yhang on 2019/1/9.
 */
public interface IntegralsActivityService {

    /**
     * 获取积分活动页头部记录播报
     * @return
     */
    List<ReportShopRestDTO> reportForIntegral();

    /**
     * 获取期数数据
     * @return
     */
    IntegralsActivityRestEntity getActivityInfo();

    List<UserIntegralsActivityRestDTO> getPersonalActivity(String userInfoId);

    UserIntegralsActivitySumRestDTO getPersonalActivityInfo(String userInfoId);

    IntegralsActivityRangeRestEntity getIntegralsActivityRangeById(String iarId);

    /**
     * 报名接口
     * @param userInfoId
     * @param iaId
     * @param integral
     */
    void toSignup(String userInfoId, String iaId, double integral);

    List<IntegralsActivityRangeRestEntity> getIntegralActivityRange();

    /**
     * 分页查询
     * @param userInfoId
     * @param curPage
     * @param pageSize
     * @return
     */
    Object getPersonalActivityInfoForPage(String userInfoId, Integer curPage, Integer pageSize);
}
