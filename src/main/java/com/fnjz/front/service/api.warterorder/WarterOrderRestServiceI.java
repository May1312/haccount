package com.fnjz.front.service.api.warterorder;

import com.fnjz.front.entity.api.statistics.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.statistics.StatisticsSpendTopAndHappinessDTO;
import com.fnjz.front.entity.api.statistics.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WarterOrderRestServiceI extends CommonService{


    /**
     * 分页查询
     * @param time
     * @param accountBookId
     * @return
     */
    Map<String,Object> findListForPage(String time, String accountBookId);

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
    void insert(WarterOrderRestEntity charge,String code,Integer accountBookId);

    /**
     * 日统计接口
     * @param beginTime
     * @param endTime
     * @param accountBookId
     */
    List<StatisticsDaysRestDTO> statisticsForDays(Date beginTime, Date endTime, Integer accountBookId);

    /**
     * 周统计接口
     * @param beginWeek
     * @param endWeek
     * @param accountBookId
     * @return
     */
    List<StatisticsWeeksRestDTO> statisticsForWeeks(String beginWeek, String endWeek, Integer accountBookId);

    /**
     * 月统计接口
     * @param accountBookId
     * @return
     */
    List<StatisticsDaysRestDTO> statisticsForMonths(Integer accountBookId);

    /**
     * 日支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappiness(Date time, Integer accountBookId);

    /**
     * 周支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappiness(String time, Integer accountBookId);

    /**
     * 月支出排行榜和消费情绪统计
     * @param time
     * @param accountBookId
     * @return
     */
    StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappiness(String time, Integer accountBookId);
}
