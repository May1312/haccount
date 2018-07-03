package com.fnjz.front.service.impl.api.warterorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.MyCountRestDTO;
import com.fnjz.front.entity.api.statistics.*;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.EmojiUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("warterOrderRestService")
@Transactional
public class WarterOrderRestServiceImpl extends CommonServiceImpl implements WarterOrderRestServiceI {

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> findListForPage(String time, String accountBookId) {

        List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(time, accountBookId);
        //获取到当月所以记录
        Map<Date, Object> map = new HashMap<>();
        for (Iterator<WarterOrderRestDTO> it = listForPage.iterator(); it.hasNext(); ) {
            WarterOrderRestDTO warter = it.next();
            //转义表情
            if(StringUtils.isNotEmpty(warter.getRemark())){
                warter.setRemark(EmojiUtils.aliasToEmoji(warter.getRemark()));
            }
            //判断是否包含日期
            if (map.containsKey(warter.getChargeDate())) {
                ((ArrayList) map.get(warter.getChargeDate())).add(warter);
            } else {
                List<WarterOrderRestDTO> list = new ArrayList<>();
                list.add(warter);
                map.put(warter.getChargeDate(), list);
            }
        }
        Map<String, Object> ja = new HashMap();
        if (map.size() > 0) {
            Map<Date, Object> resultMap = sortMapByKey(map);
            JSONArray array = new JSONArray();
            JSONArray array2 = new JSONArray();
            for (Map.Entry<Date, Object> entry : resultMap.entrySet()) {
                //封装成key value格式
                JSONObject obj = new JSONObject();
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayTime", entry.getKey());
                }
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayArrays", entry.getValue());
                }
                array.add(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
            }
            //获取日统计
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = JSON.parseObject((String) array.get(i));
                if (StringUtils.isNotEmpty(jsonObject.getString("dayArrays"))) {
                    BigDecimal dayIncome = new BigDecimal(0);
                    BigDecimal daySpend = new BigDecimal(0);
                    List list = JSONArray.parseArray(jsonObject.getString("dayArrays"));
                    for (int j = 0; j < list.size(); j++) {
                        //支出
                        WarterOrderRestDTO warter = JSONObject.parseObject(JSONObject.toJSONString(list.get(j)), WarterOrderRestDTO.class);
                        if (warter.getOrderType() == 1) {
                            daySpend = daySpend.add(warter.getMoney());
                        }
                        if (warter.getOrderType() == 2) {
                            dayIncome = dayIncome.add(warter.getMoney());
                        }
                    }
                    jsonObject.put("dayIncome", dayIncome);
                    jsonObject.put("daySpend", daySpend);
                    array2.add(jsonObject);
                    //jsonObject转json
                }
            }
            //获取月份统计数据
            Map<String, BigDecimal> account = getAccount(time, accountBookId);
            ja.put("arrays", array2);
            ja.put("monthSpend", account.get("spend"));
            ja.put("monthIncome", account.get("income"));
            return ja;
        }
        return ja;
    }

    public static Map<Date, Object> sortMapByKey(Map<Date, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Date, Object> sortMap = new TreeMap<Date, Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    @Override
    public Integer update(WarterOrderRestEntity charge) {
        return warterOrderRestDao.update(charge);
    }

    @Override
    public Integer deleteOrder(String orderId, String userInfoId, String code) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_water_order` SET `delflag` = " + 1 + " , `del_date` = NOW(), `update_by` = " + userInfoId + ", `update_name` = '" + code + "' WHERE `id` = '" + orderId + "';");
        return i;
    }

    @Override
    public Map<String, BigDecimal> getAccount(String time, String accountBookId) {
        List<Map<String, BigDecimal>> listbySql = commonDao.findListMapbySql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END) AS spend,SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END) AS income FROM `hbird_water_order` WHERE charge_date LIKE '" + time + "%' AND account_book_id = '" + accountBookId + "' AND delflag = 0;");
        return (Map) listbySql.get(0);
    }

    @Override
    public WarterOrderRestDTO findById(String id) {
        return warterOrderRestDao.findById(id);
    }

    @Override
    public int countChargeDays(String currentYearMonth, Integer accountBookId) {
        List<Map<String, String>> maps = warterOrderRestDao.countChargeDays(currentYearMonth, accountBookId);
        return maps.size();
    }

    @Override
    public int chargeTotal(Integer accountBookId) {
        return warterOrderRestDao.chargeTotal(accountBookId);
    }

    @Override
    public void insert(WarterOrderRestEntity charge, String code, Integer accountBookId) {
        commonDao.save(charge);
        String s = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_MY_COUNT + code);
        MyCountRestDTO myCountRestDTO = JSON.parseObject(s, MyCountRestDTO.class);
        if (myCountRestDTO == null) {
            myCountRestDTO = new MyCountRestDTO();
            int chargeTotal = warterOrderRestDao.chargeTotal(accountBookId);
            myCountRestDTO.setChargeTotal(chargeTotal + 1);
            String s1 = JSON.toJSONString(myCountRestDTO);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code, s1, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        } else {
            if (myCountRestDTO.getChargeTotal() < 1) {
                int chargeTotal = warterOrderRestDao.chargeTotal(accountBookId);
                myCountRestDTO.setChargeTotal(chargeTotal + 1);
                String s1 = JSON.toJSONString(myCountRestDTO);
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code, s1, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            } else {
                myCountRestDTO.setChargeTotal(myCountRestDTO.getChargeTotal() + 1);
                String s1 = JSON.toJSONString(myCountRestDTO);
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code, s1, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public List<StatisticsDaysRestDTO> statisticsForDays(Date beginTime, Date endTime, Integer accountBookId,int orderType) {
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForDays(beginTime, endTime, accountBookId,orderType);
        return list;
    }

    @Override
    public List<StatisticsWeeksRestDTO> statisticsForWeeks(String beginWeek, String endWeek, Integer accountBookId,int orderType) {
        //周统计接口
        List<StatisticsWeeksRestDTO> list = warterOrderRestDao.statisticsForWeeks(beginWeek, endWeek, accountBookId,orderType);
        return list;
    }

    @Override
    public List<StatisticsDaysRestDTO> statisticsForMonths(Integer accountBookId,int orderType) {
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForMonths(accountBookId,orderType);
        return list;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappiness(Date time, Integer accountBookId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTime(date, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForDaysTop(Date time, Integer accountBookId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTimeOfIncome(date, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappiness(String time, Integer accountBookId) {
        Map<String,String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        String beginTime = map.get("beginTime");
        String endTime = map.get("endTime");
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTime(beginTime,endTime, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForWeeksTop(String time, Integer accountBookId) {
        Map<String,String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        String beginTime = map.get("beginTime");
        String endTime = map.get("endTime");
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTimeOfIncome(beginTime,endTime, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappiness(String time, Integer accountBookId) {
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTime(time, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForMonthsTop(String time, Integer accountBookId) {
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTimeOfIncome(time, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    /**
     * 支出统计类目排行榜和情绪公用方法
     * @param list
     * @return
     */
    public StatisticsSpendTopAndHappinessDTO statisticsForAllTopAndHappiness(List<Map<String, Object>> list){
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = new StatisticsSpendTopAndHappinessDTO();
        //总金额
        BigDecimal totalMoney = new BigDecimal(0);
        //情绪总笔数
        Integer totalCount = 0;
        //排行榜集合类
        List<StatisticsTopDTO> top = new ArrayList<>();
        //情绪集合类
        List<StatisticsSpendHappinessDTO> happiness = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //情绪统计去重map
            Map<String, StatisticsSpendHappinessDTO> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                //统计总金额
                BigDecimal bd = new BigDecimal(list.get(i).get("money") + "");
                totalMoney = totalMoney.add(bd);
                //统计深度为5
                if (i < 5) {
                    //每个类目对应金额统计
                    StatisticsTopDTO statisticsSpendTopDTO = new StatisticsTopDTO();
                    //设置金额
                    statisticsSpendTopDTO.setMoney((BigDecimal) list.get(i).get("money"));
                    //设置类目名称
                    statisticsSpendTopDTO.setTypeName(list.get(i).get("type_name") + "");
                    //设置图标
                    statisticsSpendTopDTO.setIcon(list.get(i).get("icon") + "");
                    //添加到排行榜集合
                    top.add(statisticsSpendTopDTO);
                }
                //统计总笔数 moneytimes-->会统计进没心情的笔数  count--->不会统计
                totalCount += Integer.valueOf(list.get(i).get("count") + "");
                //每个情绪对应笔数统计
                if ((list.get(i).get("spend_happiness")) != null) {
                    //map去重
                    if (map.containsKey(list.get(i).get("spend_happiness") + "")) {
                        StatisticsSpendHappinessDTO statisticsSpendHappinessDTO = map.get(list.get(i).get("spend_happiness") + "");
                        statisticsSpendHappinessDTO.setCount(statisticsSpendHappinessDTO.getCount() + Integer.valueOf(list.get(i).get("count") + ""));
                        map.put(list.get(i).get("spend_happiness") + "", statisticsSpendHappinessDTO);
                    } else {
                        StatisticsSpendHappinessDTO statisticsSpendHappinessDTO = new StatisticsSpendHappinessDTO();
                        statisticsSpendHappinessDTO.setCount(Integer.valueOf(list.get(i).get("count") + ""));
                        //设置愉悦度
                        statisticsSpendHappinessDTO.setSpendHappiness(Integer.valueOf(list.get(i).get("spend_happiness") + ""));
                        map.put(list.get(i).get("spend_happiness") + "", statisticsSpendHappinessDTO);
                    }
                }
            }
            for (Map.Entry<String, StatisticsSpendHappinessDTO> entry : map.entrySet()) {
                happiness.add(entry.getValue());
            }
            //情绪消费统计排序
            Collections.sort(happiness, new Comparator<StatisticsSpendHappinessDTO>() {
                @Override
                public int compare(StatisticsSpendHappinessDTO o1, StatisticsSpendHappinessDTO o2) {
                    int i = o1.getCount() - o2.getCount();
                    if (i > 0) {
                        return -1;
                    } else if (i < 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendHappinessArrays(happiness);
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendTopArrays(top);
            statisticsSpendTopAndHappinessDTO.setTotalCount(totalCount);
            statisticsSpendTopAndHappinessDTO.setTotalMoney(totalMoney);
        }else{
            //数据为空情况下
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendHappinessArrays(happiness);
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendTopArrays(top);
            statisticsSpendTopAndHappinessDTO.setTotalCount(totalCount);
            statisticsSpendTopAndHappinessDTO.setTotalMoney(totalMoney);
        }
        return statisticsSpendTopAndHappinessDTO;
    }

    /**
     * 收入统计类目排行榜公用方法
     * @param list
     * @return
     */
    public StatisticsIncomeTopDTO statisticsForAllTop(List<Map<String, Object>> list){
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = new StatisticsIncomeTopDTO();
        //总金额
        BigDecimal totalMoney = new BigDecimal(0);
        //排行榜集合类
        List<StatisticsTopDTO> top = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //统计总金额
                BigDecimal bd = new BigDecimal(list.get(i).get("money") + "");
                totalMoney = totalMoney.add(bd);
                //统计深度为5
                if (i < 5) {
                    //每个类目对应金额统计
                    StatisticsTopDTO statisticsTopDTO = new StatisticsTopDTO();
                    //设置金额
                    statisticsTopDTO.setMoney((BigDecimal) list.get(i).get("money"));
                    //设置类目名称
                    statisticsTopDTO.setTypeName(list.get(i).get("type_name") + "");
                    //设置图标
                    statisticsTopDTO.setIcon(list.get(i).get("icon") + "");
                    //添加到排行榜集合
                    top.add(statisticsTopDTO);
                }
            }
            statisticsIncomeTopDTO.setStatisticsIncomeTopArrays(top);
            statisticsIncomeTopDTO.setTotalMoney(totalMoney);
        }else{
            //数据为空情况下
            statisticsIncomeTopDTO.setStatisticsIncomeTopArrays(top);
            statisticsIncomeTopDTO.setTotalMoney(totalMoney);
        }
        return statisticsIncomeTopDTO;
    }
}

class MapKeyComparator implements Comparator<Date> {

    @Override
    public int compare(Date str1, Date str2) {

        return str2.compareTo(str1);
    }
}