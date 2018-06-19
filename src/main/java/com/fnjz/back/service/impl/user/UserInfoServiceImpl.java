package com.fnjz.back.service.impl.user;

import com.fnjz.back.service.user.UserInfoServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userInfoService")
@Transactional
public class UserInfoServiceImpl extends CommonServiceImpl implements UserInfoServiceI {

    @Override
    public HashMap<String, Object> attributeStatistics(String startDate, String endDate) {

        String dateSql = " and register_date >= '" + startDate + " ' and register_date <= '" + endDate + "'";

        //总记录条数
        Long countTatalNumber = this.getCountForJdbc("select count(*) from hbird_user_info where 1=1 and register_date >= '" + startDate + " ' and register_date <= '" + endDate + "'");

        //每个性别
        String sexSql = "SELECT COALESCE (SUM(sex = '男' OR sex = '女'), 0)   sexTotalNumber, COALESCE (SUM(sex = '男'), 0)  manNumber, COALESCE (SUM(sex = '女'), 0)  womanNumber" +
                " FROM   hbird_user_info where 1=1 " + dateSql;
        List<Map<String, Object>> sexlist = this.findForJdbc(sexSql);
        //每个职位
        String positionSql = "SELECT COALESCE (SUM(position != '' AND position is NOT NULL), 0) positionTotalNumber, COALESCE (SUM(position = '高'), 0) highPosition, COALESCE (SUM(position = '中'), 0) centrePosition,COALESCE (SUM(position = '低'), 0) lowPosition  " +
                " FROM   hbird_user_info where 1=1 " + dateSql;
        List<Map<String, Object>> positionlist = this.findForJdbc(positionSql);
        //每个年龄阶段
        String ageSql = "SELECT  COALESCE (SUM(age != '' AND age IS NOT NULL), 0) ageTotalNumber, COALESCE (SUM(age >= 10 AND age <= 20), 0) oneToTwoNumber," +
                " COALESCE (SUM(age > 20 AND age <= 30), 0) twoToThreeNumber,COALESCE (SUM(age > 30 AND age <= 40), 0) threeToFourNumber, " +
                "COALESCE (SUM(age > 40), 0) afterFourNumber" +
                " FROM   hbird_user_info where 1=1 " + dateSql;
        List<Map<String, Object>> agelist = this.findForJdbc(ageSql);

        //每个省份统计
        String ProviceCountsql = "SELECT   province_name,  COALESCE (SUM(province_name != '' and province_name is not null), 0)  AS ProviceCount " +
                "  FROM  hbird_user_info  where 1=1  and province_name != ''" +
                "  AND province_name IS NOT NULL" + dateSql +
                "  GROUP BY " +
                " province_name " +
                "ORDER BY " +
                " ProviceCount DESC" +
                " LIMIT 0, " +
                " 10";
        //省份总记录总数
        Long ProviceTotalNumber = this.getCountForJdbc("SELECT COUNT(province_name) AS ProviceTotalNumber FROM" +
                "  hbird_user_info WHERE 1=1  and " +
                "  province_name is NOT NULL AND province_name !='' " + dateSql);

        List<Map<String, Object>> everyProviceList = this.findForJdbc(ProviceCountsql);

        //每个星座
        String constellationCountsql = "SELECT " +
                "constellation,COALESCE (SUM(constellation != '' AND constellation IS NOT NULL), 0)  AS constellationCount " +
                "FROM " +
                " hbird_user_info where 1=1 and constellation != '' AND constellation IS NOT NULL " + dateSql +
                " GROUP BY " +
                " constellation " +
                "ORDER BY " +
                " constellationCount DESC ";
        //星座有记录总数
        Long constellationCount = this.getCountForJdbc("SELECT COUNT(constellation) AS constellationCount FROM" +
                "  hbird_user_info WHERE 1=1 and " +
                "  constellation is NOT NULL AND constellation !='' " + dateSql);
        List<Map<String, Object>> everyConstellList = this.findForJdbc(constellationCountsql);
        //返回
        HashMap<String, Object> sexMap = listToMap(sexlist, countTatalNumber, Long.parseLong(sexlist.get(0).get("sexTotalNumber").toString()));
        HashMap<String, Object> positionMap = listToMap(positionlist, countTatalNumber, Long.parseLong(positionlist.get(0).get("positionTotalNumber").toString()));
        HashMap<String, Object> ageMap = listToMap(agelist, countTatalNumber, Long.parseLong(agelist.get(0).get("ageTotalNumber").toString()));
        /*HashMap<String, Object> proviceMap = listToMap(everyProviceList, countTatalNumber, ProviceTotalNumber);
        HashMap<String, Object> constellListMap = listToMap(everyConstellList, countTatalNumber,constellationCount);*/

        sexMap.putAll(ageMap);
        sexMap.putAll(positionMap);
        sexMap.put("countTatalNumber", countTatalNumber);
        sexMap.put("ProviceTotalNumber", ProviceTotalNumber);
        sexMap.put("constellationTotalCount", constellationCount);
        sexMap.put("everyProviceList", everyProviceList);
        sexMap.put("everyConstellList", everyConstellList);


        System.out.println(sexMap.toString());

        return sexMap;
    }

    public HashMap<String, Object> listToMap2(String tag, List<Map<String, Object>> list, Long countTatalNumber, Long attributeNumber) {

        /*Long countTatalNumber = countMap.get("countTatalNumber");
        Long attributeNumber = Long.valueOf(0);

        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> stringObjectMap = list.get(i);
            for (String key : stringObjectMap.keySet()) {
                Object value = stringObjectMap.get(key);
                //计算对应百分比
                if (tag.equalsIgnoreCase("sex")) {
                    attributeNumber = countMap.get("sexTotalNumber");

                }else if (tag.equalsIgnoreCase("position")) {
                    attributeNumber = countMap.get("ProviceTotalNumber");
                }
                else if (tag.equalsIgnoreCase("age")) {
                    attributeNumber = countMap.get("ProviceTotalNumber");
                }
                else if (tag.equalsIgnoreCase("Provice")) {
                    attributeNumber = countMap.get("ProviceTotalNumber");
                } else if (tag.equalsIgnoreCase("constell")) {
                    attributeNumber = countMap.get("constellationCount");
                }
                objectObjectHashMap.put(key, value);
                System.out.println(key + "  " + value.toString());
            }
        }*/
        return null;
    }

    public HashMap<String, Object> listToMap(List<Map<String, Object>> list, Long countTatalNumber, Long attributeNumber) {

        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> stringObjectMap = list.get(i);
            for (String key : stringObjectMap.keySet()) {
                Object value = stringObjectMap.get(key);
                /*if (attributeNumber==Long.valueOf("0")){
                    value =0;
                }else {
                    value=Long.parseLong(value.toString())/attributeNumber;
                }*/
                objectObjectHashMap.put(key, value);
                //System.out.println(key + "  " + value.toString());
            }
        }
        return objectObjectHashMap;
    }


}