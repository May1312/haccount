package com.fnjz.front.service.impl.api.warterorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.math.BigDecimal;
import java.util.*;

@Service("warterOrderRestService")
@Transactional
public class WarterOrderRestServiceImpl extends CommonServiceImpl implements WarterOrderRestServiceI {

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Override
    public Map<String,Object> findListForPage(String time, String accountBookId) {

        //List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(time,accountBookId,pageRest.getStartIndex(),pageRest.getPageSize());
        List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(time,accountBookId);
        //获取到当月所以记录  如何按天分组呢！！！
        Map<String,Object> map= new HashMap<>();
        for (Iterator<WarterOrderRestDTO> it = listForPage.iterator(); it.hasNext();)
        {
            WarterOrderRestDTO warter = it.next();
            //判断是否包含日期
            if(map.containsKey(DateUtils.convert2String(warter.getCreateDate()))){
                ((ArrayList)map.get(DateUtils.convert2String(warter.getCreateDate()))).add(warter);

            }else{
                List<WarterOrderRestDTO> list = new ArrayList<>();
                list.add(warter);
                map.put(DateUtils.convert2String(warter.getCreateDate()),list);
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
            Map<String, Object> resultMap = sortMapByKey(map);    //按Key进行排序

            JSONArray array = new JSONArray();
            JSONArray array2 = new JSONArray();
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                //封装成key value格式
                JSONObject obj = new JSONObject();
                if(!StringUtils.equals(entry.getKey(),"dayTime")){
                    obj.put("dayTime",entry.getKey());
                }
                if(!StringUtils.equals(entry.getKey(),"dayTime")){
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

    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<>(
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
        List<Map<String,BigDecimal>> listbySql = commonDao.findListMapbySql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END) AS spend,SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END) AS income FROM `hbird_water_order` WHERE create_date LIKE '" + time + "%' AND account_book_id = '" + accountBookId + "' AND delflag = 0;");
        return (Map)listbySql.get(0);
    }
}
class MapKeyComparator implements Comparator<String>{

    @Override
    public int compare(String str1, String str2) {

        return str2.compareTo(str1);
    }
}