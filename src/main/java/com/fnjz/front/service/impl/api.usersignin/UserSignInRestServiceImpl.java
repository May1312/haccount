package com.fnjz.front.service.impl.api.usersignin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.dao.UserSignInRestDao;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import com.fnjz.front.entity.api.usersignin.UserSignInRestDTO;
import com.fnjz.front.entity.api.usersignin.UserSignInRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.enums.SignInEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.service.api.usersignin.UserSignInRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
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

    @Autowired
    private UserIntegralRestServiceI userIntegralRestServiceI;

    /**
     * 签到
     *
     * @param userInfoId
     */
    @Override
    public Integer signIn(String userInfoId, String shareCode) {
        Map map = signInForCache(shareCode);
        FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_1.getIndex());
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
                if (ff.getBehaviorTicketValue() != null) {
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_1.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                    userIntegralRestDao.updateForTotalIntegral(userInfoId,  ff.getBehaviorTicketValue());
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
                    if (ff.getBehaviorTicketValue() != null) {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_1.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                        userIntegralRestDao.updateForTotalIntegral(userInfoId,  ff.getBehaviorTicketValue());
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
                        if (ff.getBehaviorTicketValue() != null) {
                            userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_1.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                            userIntegralRestDao.updateForTotalIntegral(userInfoId,  ff.getBehaviorTicketValue());
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
                    //计算签到日期间隔 获取当天签到状态
                    count = userSignInRestDao.checkSignInForCurrentDay(userInfoId);
                    //当天已签到
                    String[] args = StringUtils.split(DateUtils.convert2String(userSignInRestEntity.getSignInDate()), "-");
                    Period period = Period.between(LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])), LocalDate.now());
                    int days = period.getDays();
                    if (count > 0) {
                        ++days;
                    }
                    // TODO days大于58情况下存在误差
                    days = (days) % 29;
                    map.put("signInDays", days);
                    map.put("signInDate", DateUtils.getBeforeDay(new Date()).getTime() + "");
                }
            } else {
                //昨日未签到
                //计算签到日期间隔 获取当天签到状态
                count = userSignInRestDao.checkSignInForCurrentDay(userInfoId);
                //当天已签到
                int days = 0;
                if (count > 0) {
                    ++days;
                }
                // TODO days大于58情况下存在误差
                days = (days) % 29;
                map.put("signInDays", days);
                map.put("signInDate", DateUtils.getBeforeDay(new Date()).getTime() + "");
            }
            //cache
            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map, RedisPrefix.USER_VALID_TIME);
        }
        //获取到连续签到天数---->获取当前周签到情况
        List<UserSignInRestEntity> list = userSignInRestDao.getSignInForCurrentWeek(userInfoId);
        int[] result = new int[7];
        //统计周签到情况
        Date monday = DateUtils.getMonday();
        String format = DateUtils.convert2String(monday);
        String[] args = StringUtils.split(format, "-");
        //获取补签消耗积分数
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
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
                    if (fengFengTicket != null) {
                        result[i] = SignInEnum.COMPLEMENT_SIGNED.getIndex();
                    } else {
                        result[i] = SignInEnum.NOT_SIGN.getIndex();
                    }
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
        //读取签到奖励规则
        Period period1 = null;
        Map<String, Integer> map3 = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        if (map3.size() < 1) {
            List<Map<String, Object>> list2 = fengFengTicketRestDao.getSignInCycle(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription());
            if (list2.size() > 0) {
                //获取下线时间
                if (list2.get(0).get("downtime") != null) {
                    String downtime = list2.get(0).get("downtime") + "";
                    Instant instant = Instant.ofEpochMilli(Long.valueOf(downtime));
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                    if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                        period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                    }
                }
            }
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
            //设置缓存时间
            if (period1 != null) {
                redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, map3, Long.valueOf(period1.getDays() + 1));
            } else {
                redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, map3, RedisPrefix.USER_VALID_TIME);
            }
        }
        //获取自己的签到领取情况
        Map<String, Integer> map4 = redisTemplateUtils.getForHash(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode);
        JSONArray jsonArray = new JSONArray();
        JSONObject cacheJson = new JSONObject();
        //未领取1  领取2  不可领3
        int status = Integer.valueOf(map.get("signInDays") + "") / 7;
        if (map4.size() < 1) {
            //TODO 置为未领取状态吧
            //读取积分流水表恢复最近一个周期历史数据  分情况 1.恢复  2.首次调用
            //获取签到表中开始周期标记时间
            UserSignInRestEntity userSignInRestEntity = userSignInRestDao.getSignInForFisrtDesc(userInfoId);
            List<UserIntegralRestEntity> currentCycleIntegralForRecover = new ArrayList<>();
            if (userSignInRestEntity != null) {
                currentCycleIntegralForRecover = userIntegralRestDao.getCurrentCycleIntegralForRecover(userInfoId, userSignInRestEntity.getSignInDate(), IntegralEnum.SIGNIN_7.getIndex(), IntegralEnum.SIGNIN_14.getIndex(), IntegralEnum.SIGNIN_21.getIndex(), IntegralEnum.SIGNIN_28.getIndex());
            }
            //定义 可能存在的连签历史接收参数
            Integer signIn7 = 0, signIn14 = 0, signIn21 = 0, signIn28 = 0;
            if (currentCycleIntegralForRecover.size() > 0) {
                for (UserIntegralRestEntity userIntegralRestEntity : currentCycleIntegralForRecover) {
                    if (userIntegralRestEntity.getType() == IntegralEnum.SIGNIN_7.getIndex()) {
                        signIn7 = IntegralEnum.SIGNIN_7.getIndex();
                    } else if (userIntegralRestEntity.getType() == IntegralEnum.SIGNIN_14.getIndex()) {
                        signIn14 = IntegralEnum.SIGNIN_14.getIndex();
                    } else if (userIntegralRestEntity.getType() == IntegralEnum.SIGNIN_21.getIndex()) {
                        signIn21 = IntegralEnum.SIGNIN_21.getIndex();
                    } else if (userIntegralRestEntity.getType() == IntegralEnum.SIGNIN_28.getIndex()) {
                        signIn28 = IntegralEnum.SIGNIN_28.getIndex();
                    }
                }
                //
                //返给前端数据
                for (Map.Entry<String, Integer> entry : map3.entrySet()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("cycle", StringUtils.substringAfterLast(entry.getKey(), "_"));
                    jsonObject1.put("cycleAware", entry.getValue());
                    int cycle = Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")) / 7;
                    //status小于7情况下  都不可领取
                    if (status < 1) {
                        jsonObject1.put("cycleAwareStatus", 3);
                        cacheJson.put(entry.getKey(), 3);
                    } else if (status >= 1 && status < 2) {
                        //大于7情况下  cycle为7情况下
                        if (cycle == 1) {
                            if (signIn7.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }
                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status >= 2 && status < 3) {
                        //大于14情况下  cycle为7情况下
                        if (cycle == 1) {
                            if (signIn7.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }
                        } else if (cycle == 2) {
                            if (signIn14.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }

                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status >= 3 && status < 4) {
                        //大于21情况下  cycle为7情况下
                        if (cycle == 1) {
                            if (signIn7.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }

                        } else if (cycle == 2) {
                            if (signIn14.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }

                        } else if (cycle == 3) {
                            if (signIn21.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                                jsonObject1.put("cycleAwareStatus", 2);
                                cacheJson.put(entry.getKey(), 2);
                            } else {
                                jsonObject1.put("cycleAwareStatus", 1);
                                cacheJson.put(entry.getKey(), 1);
                            }
                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status == 4) {
                        if (signIn28.equals(Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")))) {
                            jsonObject1.put("cycleAwareStatus", 2);
                            cacheJson.put(entry.getKey(), 2);
                        } else {
                            //等于28情况下  cycle为7情况下
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        }
                    }
                    jsonArray.add(jsonObject1);
                }
            } else {
                //首次调用
                //返给前端数据
                for (Map.Entry<String, Integer> entry : map3.entrySet()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("cycle", StringUtils.substringAfterLast(entry.getKey(), "_"));
                    jsonObject1.put("cycleAware", entry.getValue());
                    int cycle = Integer.valueOf(StringUtils.substringAfterLast(entry.getKey(), "_")) / 7;
                    //status小于7情况下  都不可领取
                    if (status < 1) {
                        jsonObject1.put("cycleAwareStatus", 3);
                        cacheJson.put(entry.getKey(), 3);
                    } else if (status >= 1 && status < 2) {
                        //大于7情况下  cycle为7情况下
                        if (cycle == 1) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status >= 2 && status < 3) {
                        //大于14情况下  cycle为7情况下
                        if (cycle == 1) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else if (cycle == 2) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status >= 3 && status < 4) {
                        //大于21情况下  cycle为7情况下
                        if (cycle == 1) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else if (cycle == 2) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else if (cycle == 3) {
                            jsonObject1.put("cycleAwareStatus", 1);
                            cacheJson.put(entry.getKey(), 1);
                        } else {
                            jsonObject1.put("cycleAwareStatus", 3);
                            cacheJson.put(entry.getKey(), 3);
                        }
                    } else if (status == 4) {
                        //等于28情况下  cycle为7情况下
                        jsonObject1.put("cycleAwareStatus", 1);
                        cacheJson.put(entry.getKey(), 1);
                    }
                    jsonArray.add(jsonObject1);
                }
            }
            //设置缓存时间
            if (period1 != null) {
                redisTemplateUtils.updateForHash(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, cacheJson, Long.valueOf(period1.getDays() + 1));
            } else {
                redisTemplateUtils.updateForHash(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, cacheJson, RedisPrefix.VALID_TIME_28);
            }
        } else {
            for (Map.Entry<String, Integer> entry : map3.entrySet()) {
                JSONObject jsonObject1 = new JSONObject();
                for (Map.Entry<String, Integer> entry2 : map4.entrySet()) {
                    if (StringUtils.equals(entry.getKey(), entry2.getKey())) {
                        jsonObject1.put("cycle", StringUtils.substringAfterLast(entry.getKey(), "_"));
                        jsonObject1.put("cycleAware", entry.getValue());
                        jsonObject1.put("cycleAwareStatus", entry2.getValue());
                    }
                }
                jsonArray.add(jsonObject1);
            }
        }
        //排序
        jsonArray.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("cycle")));
        jsonObject.put("signInAward", jsonArray);
        //总积分数统计
        int total = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        jsonObject.put("reSignInAware", fengFengTicket == null ? null : fengFengTicket.getBehaviorTicketValue());
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

    /**
     * 补签
     *
     * @param userInfoId
     * @param shareCode
     */
    @Override
    public void reSignIn(String userInfoId, String shareCode, LocalDateTime signInDate) {
        //总积分数统计
        int total = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        //获取补签消耗积分数
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
        if (fengFengTicket.getBehaviorTicketValue() != null) {
            if (total + fengFengTicket.getBehaviorTicketValue() >= 0) {
                //Map map1 = signInForCache(shareCode);
                //修改缓存中的连续签到天数  判断补齐日期 前一天连续打卡记录
                //获取最新 周期内开始时间
                List<UserSignInRestEntity> signInForSecondDesc = userSignInRestDao.getSignInForSecondDesc(userInfoId);
                //判断补签日期前一天 是否存在签到记录
                int before = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.minusDays(1).toLocalDate().toString());
                //判断补签日期后一天 是否存在签到记录
                int after = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.plusDays(1).toLocalDate().toString());
                if (before > 0 && after < 1) {
                    //分两种情况处理 1.不存在后续签到记录
                    LocalDateTime list1SignInDate = LocalDateTime.ofInstant(signInForSecondDesc.get(0).getSignInDate().toInstant(), ZoneId.systemDefault());
                    if (list1SignInDate.isBefore(signInDate)) {
                        //删掉cache
                        redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                    }
                    //需要追加上次连签记录 ---->直接追加签到记录
                    userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                    //补签积分记录
                    if (fengFengTicket.getBehaviorTicketValue() != null) {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex());
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue());
                    }
                } else if (after > 0 && before < 1) {
                    //需要追加下次连签记录
                    userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                    //将下一连续签到标识置null
                    userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, signInDate.plusDays(1).toLocalDate().toString());
                    //补签积分记录
                    if (fengFengTicket.getBehaviorTicketValue() != null) {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex());
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue());
                    }
                    //更新redis中连续签到天数
                    redisTemplateUtils.incrementForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", 1);
                } else if (before > 0 && after > 0) {
                    //中间情况  关联上下
                    //判断上次连签天数
                    LocalDateTime list1SignInDate = LocalDateTime.ofInstant(signInForSecondDesc.get(1).getSignInDate().toInstant(), ZoneId.systemDefault());
                    Period period = Period.between(list1SignInDate.toLocalDate(), signInDate.toLocalDate());
                    int days = period.getDays();
                    //判断后续天数
                    LocalDateTime list1SignInDate2 = LocalDateTime.ofInstant(signInForSecondDesc.get(0).getSignInDate().toInstant(), ZoneId.systemDefault());
                    Period period2 = Period.between(signInDate.toLocalDate(), list1SignInDate2.toLocalDate());
                    int days2 = period2.getDays();
                    int interval;
                    if ((days + days2 + 1) / 29 > 0) {
                        interval = 29 - (days + 1);
                        userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, 1, signInDate.plusDays(interval).toLocalDate().toString());
                    }
                    userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                    //补签积分记录
                    if (fengFengTicket.getBehaviorTicketValue() != null) {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex());
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue());
                    }
                    //调整status
                    userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, LocalDateTime.ofInstant(signInForSecondDesc.get(0).getSignInDate().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
                    //TODO 删除连签天数  领取记录 cache
                    redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                    redisTemplateUtils.deleteKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode);
                } else {
                    //不存在连续签到
                    userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                    //补签积分记录
                    if (fengFengTicket.getBehaviorTicketValue() != null) {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex());
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue());
                    }
                }
            }
        }
    }
}