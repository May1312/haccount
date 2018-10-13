package com.fnjz.front.service.impl.api.usersignin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.dao.UserSignInRestDao;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.usersignin.UserSignInRestDTO;
import com.fnjz.front.entity.api.usersignin.UserSignInRestEntity;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.enums.SignInEnum;
import com.fnjz.front.service.api.usersignin.UserSignInRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.fnjz.constants.RedisPrefix.PREFIX_SIGN_IN;

@Service("userSignInRestService")
@Transactional
public class UserSignInRestServiceImpl extends CommonServiceImpl implements UserSignInRestServiceI {

    @Autowired
    private UserSignInRestDao userSignInRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    /**
     * 签到
     *
     * @param userInfoId
     */
    @Override
    public Integer signIn(String userInfoId, String shareCode) {

        Map map = signInForCache(shareCode);
        FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(),IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(),IntegralEnum.SIGNIN_1.getIndex());
        if (map != null) {
            //map为空情况下--->即当天未签到
            if (map.get("hasSigned") == null) {
                // 周期内首次打卡 将status置为1
                if (Integer.valueOf(map.get("signInDays") + "") == 1) {
                    userSignInRestDao.signIn(userInfoId, 1);
                } else {
                    userSignInRestDao.signIn(userInfoId, null);
                }
                //签到积分记录
                if(ff.getBehaviorTicketValue()!=null){
                    userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue());
                }
            }
        } else {
            //redis未缓存签到记录  mysql查询
            int count = userSignInRestDao.checkSignInForBeforeCurrentDay(userInfoId);
            boolean flag = checkSignInForCurrentDay(userInfoId);
            map = new HashMap(3);
            if (count == 0) {
                //本次签到置 为第一次签到
                map.put("signInDays", 1);
                map.put("signInDate", System.currentTimeMillis() + "");
                if (!flag) {
                    userSignInRestDao.signIn(userInfoId, 1);
                    //签到积分记录
                    if(ff.getBehaviorTicketValue()!=null){
                        userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue());
                    }
                }
            } else {
                //统计最近一次标记时间
                UserSignInRestEntity userSignInRestEntity = userSignInRestDao.getSignInForFisrtDesc(userInfoId);
                if (userSignInRestEntity != null) {
                    //计算签到日期间隔
                    String[] args = StringUtils.split(DateUtils.convert2String(userSignInRestEntity.getSignInDate()), "-");
                    Period period = Period.between(LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])), LocalDate.now());
                    // TODO days大于58情况下存在误差
                    int days = (period.getDays() + 1) % 29;
                    map.put("signInDays", days);
                    map.put("signInDate", System.currentTimeMillis() + "");
                    if (!flag) {
                        userSignInRestDao.signIn(userInfoId, 1);
                        //签到积分记录
                        if(ff.getBehaviorTicketValue()!=null){
                            userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue());
                        }
                    }
                }
            }
        }
        //更新redis缓存  去掉是否签到标识
        if (map.get("hasSigned") != null) {
            map.remove("hasSigned");
        }
        redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map);
        return ff.getBehaviorTicketValue();
    }

    private Map signInForCache(String shareCode) {
        Map map = redisTemplateUtils.getForHash(PREFIX_SIGN_IN + shareCode);
        if (map.size() > 0) {
            //判断打卡间隔 获取下一天凌晨时间间隔
            Date nextDay = DateUtils.getNextDay(new Date(Long.valueOf(map.get("signInDate") + "")));
            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).atZone(ZoneOffset.systemDefault()).toEpochSecond();
            //获取当天凌晨范围
            Date dateOfBegin = DateUtils.fetchBeginOfDay(nextDay);
            Date dateOfEnd = DateUtils.fetchEndOfDay(nextDay);
            long now = System.currentTimeMillis();
            if (now > dateOfBegin.getTime() && now < dateOfEnd.getTime()) {
                //签到成功  判断signInDays 是否达到上限28
                map.put("signInDays", ((Integer.valueOf(map.get("signInDays") + "") + 1)) % 29);
                map.put("signInDate", (System.currentTimeMillis() + ""));
            } else if (now > dateOfEnd.getTime()) {
                //置空
                map.put("signInDays", 1);
                map.put("signInDate", System.currentTimeMillis() + "");
            } else {
                //已签到情况下
                map.put("hasSigned", true);
            }
            return map;
        } else {
            //查询mysql确定是否签到
            return null;
        }
    }

    /**
     * 查看当天是否已签到
     *
     * @return
     */
    @Override
    public boolean checkSignInForCurrentDay(String userInfoId) {
        int count = userSignInRestDao.checkSignInForCurrentDay(userInfoId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取签到情况
     *
     * @param userInfoId
     * @param shareCode
     * @return
     */
    @Override
    public JSONObject getSignIn(String userInfoId, String shareCode) {
        Map map = redisTemplateUtils.getForHash(PREFIX_SIGN_IN + shareCode);
        if (map.size() < 1) {
            //redis未缓存签到记录  mysql查询
            int count = userSignInRestDao.checkSignInForBeforeCurrentDay(userInfoId);
            map = new HashMap(3);
            if (count > 0) {
                //统计最近一次标记时间
                UserSignInRestEntity userSignInRestEntity = userSignInRestDao.getSignInForFisrtDesc(userInfoId);
                if (userSignInRestEntity != null) {
                    //计算签到日期间隔
                    String[] args = StringUtils.split(DateUtils.convert2String(userSignInRestEntity.getSignInDate()), "-");
                    Period period = Period.between(LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])), LocalDate.now());
                    int days = period.getDays();
                    // TODO days大于58情况下存在误差
                    days = (days) % 29;
                    map.put("signInDays", days);
                    map.put("signInDate", DateUtils.getBeforeDay(new Date()).getTime() + "");
                }
            }
            //cache
            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map);
        }
        //获取到连续签到天数---->获取当前周签到情况
        List<UserSignInRestEntity> list = userSignInRestDao.getSignInForCurrentWeek(userInfoId);
        int[] result = new int[7];
        //统计周签到情况
        Date monday = DateUtils.getMonday();
        String format = DateUtils.convert2String(monday);
        String[] args = StringUtils.split(format, "-");
        //计算两个日期间间隔天数
        Period period = Period.between(LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])), LocalDate.now());
        for (int i = 0; i <= period.getDays(); i++) {
            boolean flag = false;
            //从周一遍历到当前日期
            for (UserSignInRestEntity userSignInRestEntity : list) {
                if (StringUtils.equals(DateUtils.convert2String(monday), LocalDate.now().toString())) {
                    if (StringUtils.equals(DateUtils.convert2String(monday), DateUtils.convert2String(userSignInRestEntity.getSignInDate()))) {
                        //签到状态
                        result[i] = SignInEnum.HAS_SIGNED.getIndex();
                        flag = true;
                        break;
                    } else {
                        //当天未签到情况
                        result[i] = SignInEnum.NOT_SIGN.getIndex();
                        flag = true;
                        break;
                    }
                } else {
                    if (StringUtils.equals(DateUtils.convert2String(monday), DateUtils.convert2String(userSignInRestEntity.getSignInDate()))) {
                        //签到状态
                        result[i] = SignInEnum.HAS_SIGNED.getIndex();
                        flag = true;
                        break;
                    }
                }
            }
            //跳出第一层循环  未签到  判断当天情况
            if (StringUtils.equals(DateUtils.convert2String(monday), LocalDate.now().toString())) {
                if (!flag) {
                    result[i] = SignInEnum.NOT_SIGN.getIndex();
                }
            } else {
                if (!flag) {
                    result[i] = SignInEnum.COMPLEMENT_SIGNED.getIndex();
                }
            }
            monday = DateUtils.getNextDay(monday);
        }
        for (int j = 6; j > period.getDays(); j--) {
            //后几天置为未签到状态
            result[j] = SignInEnum.NOT_SIGN.getIndex();
        }
        JSONObject jsonObject = new JSONObject();
        //判断当天是否签到
        if (map.get("signInDate") != null) {
            LocalDateTime signInDate = LocalDateTime.ofEpochSecond(Long.valueOf(map.get("signInDate") + "") / 1000, 0, ZoneOffset.ofHours(8));
            if (StringUtils.equals(signInDate.format(DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.now().toString())) {
                jsonObject.put("signInStatus", SignInEnum.HAS_SIGNED.getIndex());
            } else {
                jsonObject.put("signInStatus", SignInEnum.NOT_SIGN.getIndex());
            }
        } else {
            jsonObject.put("signInStatus", SignInEnum.NOT_SIGN.getIndex());
        }
        jsonObject.put("signInDays", map.get("signInDays") == null ? 0 : map.get("signInDays"));
        jsonObject.put("signInWeek", result);
        //测试 返回连续打卡奖励
        /*JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("cycle", 7);
        jsonObject1.put("behaviorTicketValue", 5);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("cycle", 14);
        jsonObject2.put("behaviorTicketValue", 12);

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("cycle", 21);
        jsonObject3.put("behaviorTicketValue", 18);


        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("cycle", 28);
        jsonObject4.put("behaviorTicketValue", 26);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject1);
        jsonArray.add(jsonObject2);
        jsonArray.add(jsonObject3);
        jsonArray.add(jsonObject4);*/
        //读取签到奖励规则
        Map<String, Integer> map3 = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        if (map3.size() < 1) {
            List<Map<String, String>> list2 = fengFengTicketRestDao.getSignInCycle(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription());
            for (Map map2 : list2) {
                if (StringUtils.equals(map2.get("cycle") + "", IntegralEnum.SIGNIN_7.getIndex() + "")) {
                    map3.put("signIn_" + IntegralEnum.SIGNIN_7.getIndex(), Integer.valueOf(map2.get("cycleaware") + ""));
                } else if (StringUtils.equals(map2.get("cycle") + "", IntegralEnum.SIGNIN_14.getIndex() + "")) {
                    map3.put("signIn_" + IntegralEnum.SIGNIN_14.getIndex(), Integer.valueOf(map2.get("cycleaware") + ""));
                } else if (StringUtils.equals(map2.get("cycle") + "", IntegralEnum.SIGNIN_21.getIndex() + "")) {
                    map3.put("signIn_" + IntegralEnum.SIGNIN_21.getIndex(), Integer.valueOf(map2.get("cycleaware") + ""));
                } else {
                    map3.put("signIn_" + IntegralEnum.SIGNIN_28.getIndex(), Integer.valueOf(map2.get("cycleaware") + ""));
                }
            }
            redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, map3);
        }
        //TODO   上述获取系统 若获取不到系统数据   --->自有数据要更新？？？？？
        //获取自己的签到领取情况
        Map<String, Integer> map4 = redisTemplateUtils.getForHash(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE+shareCode);
        JSONArray jsonArray = new JSONArray();
        JSONObject cacheJson = new JSONObject();
        if(map4.size()<1){
            //TODO 置为未领取状态吧
            //返给前端数据
            for (Map.Entry<String,Integer> entry : map3.entrySet()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("cycle",StringUtils.substringAfterLast(entry.getKey(),"_"));
                jsonObject1.put("cycleAware",entry.getValue());
                //未领取1  领取2
                jsonObject1.put("cycleAwareStatus",1);
                cacheJson.put(entry.getKey(),1);
                jsonArray.add(jsonObject1);
            }
            redisTemplateUtils.updateForHash(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE+shareCode, cacheJson);
        }else{
            for (Map.Entry<String,Integer> entry : map3.entrySet()) {
                JSONObject jsonObject1 = new JSONObject();
                for (Map.Entry<String,Integer> entry2 : map4.entrySet()) {
                    if(StringUtils.equals(entry.getKey(),entry2.getKey())){
                        jsonObject1.put("cycle",StringUtils.substringAfterLast(entry.getKey(),"_"));
                        jsonObject1.put("cycleAware",entry.getValue());
                        jsonObject1.put("cycleAwareStatus",entry2.getValue());
                    }
                }
                jsonArray.add(jsonObject1);
            }
        }
        //排序
        jsonArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("cycle")));
        jsonObject.put("signInAward", jsonArray);
        //T总积分数统计
        int total = userIntegralRestDao.getTotalIntegral(userInfoId);
        jsonObject.put("totalIntegral", total);
        return jsonObject;
    }

    /**
     * 根据月份查询签到数据
     *
     * @param userInfoId
     * @param time
     * @return
     */
    @Override
    public JSONObject getSignInForMonth(String userInfoId, String time) {
        List<UserSignInRestDTO> list = userSignInRestDao.getSignInForMonth(userInfoId, time);
        JSONObject json = new JSONObject();
        json.put("signInMonth", list);
        json.put("time", time);
        return json;
    }

}