package com.fnjz.front.service.impl.api.warterorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.MyCountRestDTO;
import com.fnjz.front.entity.api.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
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
    public Map<String,Object> findListForPage(String time, String accountBookId) {

        //List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(time,accountBookId,pageRest.getStartIndex(),pageRest.getPageSize());
         List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(time,accountBookId);
        //获取到当月所以记录  如何按天分组呢！！！
        Map<Date,Object> map= new HashMap<>();
        for (Iterator<WarterOrderRestDTO> it = listForPage.iterator(); it.hasNext();)
        {
            WarterOrderRestDTO warter = it.next();
            //判断是否包含日期
            if(map.containsKey(warter.getChargeDate())){
                ((ArrayList)map.get(warter.getChargeDate())).add(warter);
            }else{
                List<WarterOrderRestDTO> list = new ArrayList<>();
                list.add(warter);
                map.put(warter.getChargeDate(),list);
            }
        }
            //获取总条数
        //Integer count = warterOrderRestDao.getCount(time,accountBookId);
        //设置总记录数
        //pageRest.setTotalCount(count);
        //设置返回结果
        //pageRest.setContent(listForPage);
        Map<String,Object> ja = new HashMap();
        if(map.size()>0){
            Map<Date, Object> resultMap = sortMapByKey(map);
            JSONArray array = new JSONArray();
            JSONArray array2 = new JSONArray();
            for (Map.Entry<Date, Object> entry : resultMap.entrySet()) {
                //封装成key value格式
                JSONObject obj = new JSONObject();
                if(!StringUtils.equals(entry.getKey()+"","dayTime")){
                    obj.put("dayTime",entry.getKey());
                }
                if(!StringUtils.equals(entry.getKey()+"","dayTime")){
                    obj.put("dayArrays",entry.getValue());
                }
                array.add(obj.toJSONString());
            }
            //获取日统计
            for (int i = 0;i<array.size();i++) {
                JSONObject jsonObject = JSON.parseObject((String)array.get(i));
                if(StringUtils.isNotEmpty(jsonObject.getString("dayArrays"))){
                    BigDecimal dayIncome = new BigDecimal(0);
                    BigDecimal daySpend = new BigDecimal(0);
                    List list = JSONArray.parseArray(jsonObject.getString("dayArrays"));
                    for (int j = 0;j<list.size();j++) {
                        //支出
                        WarterOrderRestDTO warter = JSONObject.parseObject(JSONObject.toJSONString(list.get(j)),WarterOrderRestDTO.class);
                        if(warter.getOrderType()==1){
                            daySpend = daySpend.add(warter.getMoney());
                        }
                        if(warter.getOrderType()==2){
                            dayIncome = dayIncome.add(warter.getMoney());
                        }
                    }
                    jsonObject.put("dayIncome",dayIncome);
                    jsonObject.put("daySpend",daySpend);
                    array2.add(jsonObject);
                    //jsonObject转json
                }
            }
            //获取月份统计数据
            Map<String, BigDecimal> account = getAccount(time, accountBookId);
            ja.put("arrays",array2);
            ja.put("monthSpend",account.get("spend"));
            ja.put("monthIncome",account.get("income"));
            return ja;
        }
        return ja;
    }

    public static Map<Date, Object> sortMapByKey(Map<Date, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Date, Object> sortMap = new TreeMap<Date,Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    @Override
    public Integer update(WarterOrderRestEntity charge) {
        return  warterOrderRestDao.update(charge);
    }

    @Override
    public Integer deleteOrder(String orderId, String userInfoId, String code) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_water_order` SET `delflag` = " + 1 + " , `del_date` = NOW(), `update_by` = "+userInfoId+", `update_name` = '"+code+"' WHERE `id` = '" + orderId + "';");
        return i;
    }

    @Override
    public Map<String, BigDecimal> getAccount(String time, String accountBookId) {
        List<Map<String,BigDecimal>> listbySql = commonDao.findListMapbySql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END) AS spend,SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END) AS income FROM `hbird_water_order` WHERE charge_date LIKE '" + time + "%' AND account_book_id = '" + accountBookId + "' AND delflag = 0;");
        return (Map)listbySql.get(0);
    }

    @Override
    public WarterOrderRestDTO findById(String id) {
        return  warterOrderRestDao.findById(id);
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
    public void insert(WarterOrderRestEntity charge,String code,Integer accountBookId) {
        commonDao.save(charge);
        String s =(String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_MY_COUNT + code);
        MyCountRestDTO myCountRestDTO = JSON.parseObject(s, MyCountRestDTO.class);
        if(myCountRestDTO==null){
            myCountRestDTO = new MyCountRestDTO();
            int  chargeTotal = warterOrderRestDao.chargeTotal(accountBookId);
            myCountRestDTO.setChargeTotal(chargeTotal+1);
            String s1 = JSON.toJSONString(myCountRestDTO);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code,s1,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }else{
            if(myCountRestDTO.getChargeTotal()<1){
                int  chargeTotal = warterOrderRestDao.chargeTotal(accountBookId);
                myCountRestDTO.setChargeTotal(chargeTotal+1);
                String s1 = JSON.toJSONString(myCountRestDTO);
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code,s1,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            }else{
                myCountRestDTO.setChargeTotal(myCountRestDTO.getChargeTotal()+1);
                String s1 = JSON.toJSONString(myCountRestDTO);
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + code,s1,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public List<StatisticsDaysRestDTO> statisticsForDays(Date beginTime, Date endTime, Integer accountBookId) {
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForDays(beginTime,endTime,accountBookId);
        return list;
    }

    @Override
    public List<StatisticsWeeksRestDTO> statisticsForWeeks(String beginWeek, String endWeek, Integer accountBookId) {
        List<StatisticsWeeksRestDTO> list = warterOrderRestDao.statisticsForWeeks(beginWeek,endWeek,accountBookId);
        return list;
    }
}
class MapKeyComparator implements Comparator<Date>{

    @Override
    public int compare(Date str1, Date str2) {

        return str2.compareTo(str1);
    }
}