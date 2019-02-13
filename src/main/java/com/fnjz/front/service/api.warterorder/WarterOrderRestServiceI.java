package com.fnjz.front.service.api.warterorder;

import com.fnjz.front.entity.api.statistics.StatisticsIncomeTopDTO;
import com.fnjz.front.entity.api.statistics.StatisticsSpendTopAndHappinessDTO;
import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;
import com.fnjz.front.entity.api.warterorder.WXAppletWarterOrderRestInfoDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import org.jeecgframework.core.common.service.CommonService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface WarterOrderRestServiceI extends CommonService{


    /**
     * 分页查询
     * @param time
     * @param accountBookId
     * @return
     */
    Map<String,Object> findListForPage(String time, String accountBookId,Integer curPage,Integer pageSize);

    /**
     * 小程序端分页
     * @param time
     * @param accountBookId
     * @param curPage
     * @param pageSize
     * @return
     */
    Map<String,Object> findListForPagev2(String time, String accountBookId,Integer curPage,Integer pageSize,Integer abId,String userInfoId);

    /**
     * 更新
     * @param charge
     */
    Integer update(WarterOrderRestNewLabel charge);

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

    /**
     * 根据流水号获取订单详情
     * @param id
     * @return
     */
    WarterOrderRestDTO findById(String id);

    /**
     * 获取本月记账天数
     * @param currentYearMonth
     * @param accountBookId
     * @return
     */
    int countChargeDays(String currentYearMonth, Integer accountBookId);

    /**
     * 获取用户记账总笔数
     * @param accountBookId
     * @return
     */
    int chargeTotal(Integer accountBookId);

    /**
     * 记账功能
     * @param charge
     */
    void insert(WarterOrderRestNewLabel charge, String code, Integer accountBookId);

    /**
     * 日统计接口
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @param orderType
     */
    Map<String,Object> statisticsForDays(Date beginTime, Date endTime, Integer accountBookId,int orderType);

    /**
     * 周统计接口
     * @param beginWeek
     * @param endWeek
     * @param accountBookId
     * @param orderType
     * @return
     */
    Map<String,Object> statisticsForWeeks(String beginWeek, String endWeek, Integer accountBookId,int orderType);

    /**
     * 月统计接口
     * @param accountBookId
     * @param orderType
     * @return
     */
    Map<String,Object> statisticsForMonths(Integer accountBookId,int orderType);

    /**
     * 日支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappiness(Date time, Integer accountBookId);

    /**
     * 日支出排行榜统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForDaysTop(Date time, Integer accountBookId);

    /**
     * 周支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappiness(String time, Integer accountBookId);

    /**
     * 周支出排行榜统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForWeeksTop(String time, Integer accountBookId);

    /**
     * 月支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappiness(String time, Integer accountBookId);

    /**
     * 月支出排行榜统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForMonthsTop(String time, Integer accountBookId);

    /**
     * 根据记账时间统计记账天数
     * @param time
     * @param accountBookId
     * @return
     */
    int countChargeDaysByChargeDays(String time, Integer accountBookId);

    /**
     * 获取订单详情
     * @param id
     * @param memberFlag
     * @return
     */
    WXAppletWarterOrderRestInfoDTO findByIdv2(String id, Integer memberFlag);

    /**
     * v2 记账
     * @param charge
     */
    FestivalTagsRestEntity insertv2(WarterOrderRestNewLabel charge);

    /**
     * v2 统计日
     * @param beginTime
     * @param endTime
     * @param userInfoId
     * @param i
     * @return
     */
    Map<String,Object> statisticsForDaysv2(Date beginTime, Date endTime, String userInfoId, int i);

    /**
     * v2 统计周
     * @param beginWeek
     * @param endWeek
     * @param userInfoId
     * @param i
     * @return
     */
    Map<String,Object> statisticsForWeeksv2(String beginWeek, String endWeek, String userInfoId, int i);

    /**
     * v2  统计月
     * @param userInfoId
     * @param i
     * @return
     */
    Map<String,Object> statisticsForMonthsv2(String userInfoId, int i);

    /**
     * v2 日统计 支出排行榜
     * @param dayTime
     * @param userInfoId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappinessv2(Date dayTime, String userInfoId);

    /**
     * v2 周统计 支出排行榜
     * @param time
     * @param userInfoId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappinessv2(String time, String userInfoId);

    /**
     * v2 月统计 支出排行榜
     * @param time
     * @param userInfoId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappinessv2(String time, String userInfoId);

    /**
     * v2 日统计 收入排行榜
     * @param dayTime
     * @param userInfoId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForDaysTopv2(Date dayTime, String userInfoId);

    /**
     * v2 周统计 收入排行榜
     * @param time
     * @param userInfoId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForWeeksTopv2(String time, String userInfoId);

    /**
     * v2 月统计 收入排行榜
     * @param time
     * @param userInfoId
     * @return
     */
    StatisticsIncomeTopDTO statisticsForMonthsTopv2(String time, String userInfoId);

    /**
     * 统计记账天数
     * @param userInfoId
     * @return
     */
    int countChargeDaysv2(String userInfoId);

    /**
     * 统计记账笔数
     * @param userInfoId
     * @return
     */
    int chargeTotalv2(String userInfoId);
}
