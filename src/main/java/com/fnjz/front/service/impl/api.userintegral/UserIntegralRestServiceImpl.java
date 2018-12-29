package com.fnjz.front.service.impl.api.userintegral;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.dao.UserSignInAwardRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralTopRestDTO;
import com.fnjz.front.entity.api.usersigninaward.UserSignInAwardRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userIntegralRestService")
@Transactional
public class UserIntegralRestServiceImpl extends CommonServiceImpl implements UserIntegralRestServiceI {

    private static final Logger logger = Logger.getLogger(UserIntegralRestServiceImpl.class);


    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserSignInAwardRestDao userSignInAwardRestDao;

    @Override
    public void signInIntegral(String userInfoId, String shareCode, Map<String, String> map) {
        //根据cycle 判断周数
        String cycle = map.get("cycle");
        if (StringUtils.isNotEmpty(cycle)) {
            //判断是否取到资格
            UserSignInAwardRestEntity entity = userSignInAwardRestDao.getGetTimesAndAwardStatus(userInfoId, cycle);
            if (entity.getGetTimes() != null) {
                if (entity.getGetTimes() > 0) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), Integer.valueOf(cycle));
                    //用户连签奖励领取情况表 times-1     只有当领取次数为0 并且状态为1的时候才修改为2
                    UserSignInAwardRestEntity bean;
                    if ((entity.getGetTimes() - 1) == 0 && entity.getCycleAwardStatus() == 1) {
                        bean = new UserSignInAwardRestEntity(Integer.valueOf(userInfoId), CategoryOfBehaviorEnum.SignIn.getName(), Integer.valueOf(cycle), 2, -1, 0);
                    } else {
                        bean = new UserSignInAwardRestEntity(Integer.valueOf(userInfoId), CategoryOfBehaviorEnum.SignIn.getName(), Integer.valueOf(cycle), null, -1, 0);
                    }
                    userSignInAwardRestDao.update(bean);
                    //添加到积分记录表
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), Integer.valueOf(cycle), CategoryOfBehaviorEnum.SignIn.getIndex(),Double.parseDouble(ff.getBehaviorTicketValue()+""));
                    //修改总积分数
                    userIntegralRestDao.updateForTotalIntegral(userInfoId,ff.getBehaviorTicketValue() ,new BigDecimal(ff.getBehaviorTicketValue()+""));
                }
            }
        }
    }

    @Override
    public PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if (curPage != null) {
            pageRest.setCurPage(curPage);
        }
        if (pageSize != null) {
            pageRest.setPageSize(pageSize);
        }
        List<UserIntegralRestDTO> listForPage = userIntegralRestDao.listForPage(userInfoId, pageRest.getStartIndex(), pageRest.getPageSize());
        //获取总条数
        Integer count = userIntegralRestDao.getCount(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }

    /**
     * 获取今日任务/新手任务完成情况
     *
     * @param userInfoId
     * @return
     */
    @Override
    public JSONObject integralTask(String userInfoId, String shareCode) {
        String cacheNewbieTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
        JSONArray newbieTask = JSONArray.parseArray(cacheNewbieTask);
        String cacheTodayTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
        JSONArray todayTask = JSONArray.parseArray(cacheTodayTask);
        //查询任务对应积分数---->系统缓存数据
        Map<?, ?> cacheSysNewbieTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
        Map<?, ?> cacheSysTodayTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK);
        if (cacheNewbieTask == null) {
            //未获取到新手任务 获取用户已完成的新手任务
            List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
            JSONArray jsonArrayForUser = new JSONArray();
            JSONArray cacheJsonArrayForUser = new JSONArray();
            Period period1 = null;
            if (cacheSysNewbieTask.size() == 0) {
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                if (newBieTaskAware.size() > 0) {
                    //获取下线时间
                    if (newBieTaskAware.get(0).get("downtime") != null) {
                        Instant instant = Instant.ofEpochMilli(Long.valueOf(newBieTaskAware.get(0).get("downtime") + ""));
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                        if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                            period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : newBieTaskAware) {
                    JSONObject jsonObjectForUser = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.binding_phone_or_wx.getName())) {
                        //绑定手机号/微信
                        jsonObject.put("bindPhoneOrWXAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        //TODO jsonObject缓存系统   jsonObjectForUser缓存个人---->去掉系统属性  每次拼装数据
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.binding_phone_or_wx);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //设置预算
                        jsonObject.put("budgetAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Setting_up_budget);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //设置存钱效率
                        jsonObject.put("savingEfficiencyAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Setting_up_savings_efficiency);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //完善个人资料
                        jsonObject.put("userInfoAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Perfecting_personal_data);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    }
                    //返给user
                    if (jsonObjectForUser.size() > 0) {
                        jsonArrayForUser.add(jsonObjectForUser);
                    }
                    //cache
                    if (cacheJsonObjectForUser.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObjectForUser);
                    }
                }
                //缓存系统
                if (period1 != null) {
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), RedisPrefix.USER_VALID_TIME);
                }
                newbieTask = jsonArrayForUser;
            } else {
                //拿到系统缓存---->整合数据
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //设置积分数
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.binding_phone_or_wx);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "budgetAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Setting_up_budget);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "savingEfficiencyAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Setting_up_savings_efficiency);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "userInfoAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Perfecting_personal_data);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    }
                    if (jsonObject.size() > 0) {
                        jsonArrayForUser.add(jsonObject);
                    }
                    if (cacheJsonObjectForUser.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObjectForUser);
                    }
                }
            }
            //获取系统缓存有效期
            Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
            //缓存
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), expire, TimeUnit.SECONDS);
            newbieTask = jsonArrayForUser;
        } else {
            //判断系统缓存---->整合数据
            Period period1 = null;
            if (cacheSysNewbieTask.size() == 0) {
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                if (newBieTaskAware.size() > 0) {
                    //获取下线时间
                    if (newBieTaskAware.get(0).get("downtime") != null) {
                        Instant instant = Instant.ofEpochMilli(Long.valueOf(newBieTaskAware.get(0).get("downtime") + ""));
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                        if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                            period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : newBieTaskAware) {
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.binding_phone_or_wx.getName())) {
                        //绑定手机号/微信
                        jsonObject.put("bindPhoneOrWXAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //设置预算
                        jsonObject.put("budgetAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //设置存钱效率
                        jsonObject.put("savingEfficiencyAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //完善个人资料
                        jsonObject.put("userInfoAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    }
                }
                //缓存系统
                if (period1 != null) {
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                cacheSysNewbieTask = jsonObject;
            }
            //判断个人缓存不为null  系统缓存不为null 但是size不匹配情况
            if (newbieTask.size() != cacheSysNewbieTask.size()) {
                redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
                //未获取到新手任务 获取用户已完成的新手任务
                List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
                JSONArray cacheJsonArrayForUser = new JSONArray();
                //拿到系统缓存---->整合数据
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //设置积分数
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.binding_phone_or_wx);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "budgetAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Setting_up_budget);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "savingEfficiencyAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Setting_up_savings_efficiency);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "userInfoAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Perfecting_personal_data);
                        cacheJsonObjectForUser = (JSONObject) jsonObject.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    }
                    if (jsonObject.size() > 0) {
                        newbieTask.add(jsonObject);
                    }
                    if (cacheJsonObjectForUser.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObjectForUser);
                    }
                }
                //获取系统缓存有效期
                Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                //缓存
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), expire, TimeUnit.SECONDS);
                newbieTask = cacheJsonArrayForUser;
            } else {
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //设置积分数
                        if (patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.binding_phone_or_wx) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                            jsonObject.put("status", 1);
                            newbieTask.add(jsonObject);
                        } else {
                            newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.binding_phone_or_wx);
                        }
                    } else if (StringUtils.equals(entry.getKey() + "", "budgetAware")) {
                        if (patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_budget) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                            jsonObject.put("status", 1);
                            newbieTask.add(jsonObject);
                        } else {
                            newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_budget);
                        }
                    } else if (StringUtils.equals(entry.getKey() + "", "savingEfficiencyAware")) {
                        if (patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_savings_efficiency) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                            jsonObject.put("status", 1);
                            newbieTask.add(jsonObject);
                        } else {
                            newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_savings_efficiency);
                        }
                    } else if (StringUtils.equals(entry.getKey() + "", "userInfoAware")) {
                        if (patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Perfecting_personal_data) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                            jsonObject.put("status", 1);
                            newbieTask.add(jsonObject);
                        } else {
                            newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Perfecting_personal_data);
                        }
                    }
                }
            }
        }
        if (cacheTodayTask == null) {
            //查询任务对应积分数---->系统缓存数据
            List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
            JSONArray jsonArrayForUser = new JSONArray();
            JSONArray cacheJsonArrayForUser = new JSONArray();
            Period period1 = null;
            if (cacheSysTodayTask.size() == 0) {
                //未缓存今日任务缓存---->重查
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                if (todayTaskAware.size() > 0) {
                    //获取下线时间
                    if (todayTaskAware.get(0).get("downtime") != null) {
                        Instant instant = Instant.ofEpochMilli(Long.valueOf(todayTaskAware.get(0).get("downtime") + ""));
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                        if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                            period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : todayTaskAware) {
                    JSONObject jsonObjectForUser = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Inviting_friends.getName())) {
                        //邀请好友
                        jsonObject.put("inviteFriendsAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Inviting_friends);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //记一笔账
                        jsonObject.put("toChargeAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Write_down_an_account);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    }
                    if (jsonObject.size() > 0) {
                        jsonArrayForUser.add(jsonObjectForUser);
                    }
                    if (cacheJsonObjectForUser.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObjectForUser);
                    }
                }
                //缓存系统
                if (period1 != null) {
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                    //个人
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //凌晨时间戳-当前时间戳
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                } else {
                    //缓存系统
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                    //个人
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //凌晨时间戳-当前时间戳
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                }
                todayTask = jsonArrayForUser;
            } else {
                //拿到系统缓存---->整合数据
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObject = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //设置设置积分数
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Inviting_friends);
                        cacheJsonObject = (JSONObject) jsonObject.clone();
                        cacheJsonObject.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "toChargeAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Write_down_an_account);
                        cacheJsonObject = (JSONObject) jsonObject.clone();
                        cacheJsonObject.remove("integralAware");
                    }
                    if (jsonObject.size() > 0) {
                        jsonArrayForUser.add(jsonObject);
                    }
                    if (cacheJsonObject.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObject);
                    }
                }
            }
            LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
            //凌晨时间戳-当前时间戳
            long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            //缓存
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
            todayTask = jsonArrayForUser;
        } else {
            //判断系统缓存---->整合数据
            if (cacheSysTodayTask.size() == 0) {
                Period period1 = null;
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                if (todayTaskAware.size() > 0) {
                    //获取下线时间
                    if (todayTaskAware.get(0).get("downtime") != null) {
                        Instant instant = Instant.ofEpochMilli(Long.valueOf(todayTaskAware.get(0).get("downtime") + ""));
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                        if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                            period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : todayTaskAware) {
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Inviting_friends.getName())) {
                        //邀请好友
                        jsonObject.put("inviteFriendsAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //记一笔账
                        jsonObject.put("toChargeAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    }
                }
                if (period1 != null) {
                    //缓存系统
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                cacheSysTodayTask = jsonObject;
            }
            //用户信息缓存与系统不一致时
            if (cacheSysTodayTask.size() != todayTask.size()) {
                redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
                //查询任务对应积分数---->系统缓存数据
                List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
                JSONArray jsonArrayForUser = new JSONArray();
                JSONArray cacheJsonArrayForUser = new JSONArray();
                //拿到系统缓存---->整合数据
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObject = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //设置设置积分数
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Inviting_friends);
                        cacheJsonObject = (JSONObject) jsonObject.clone();
                        cacheJsonObject.remove("integralAware");
                    } else if (StringUtils.equals(entry.getKey() + "", "toChargeAware")) {
                        jsonObject.put("integralAware", entry.getValue());
                        jsonObject = patchDate(taskComplete, jsonObject, AcquisitionModeEnum.Write_down_an_account);
                        cacheJsonObject = (JSONObject) jsonObject.clone();
                        cacheJsonObject.remove("integralAware");
                    }
                    if (jsonObject.size() > 0) {
                        jsonArrayForUser.add(jsonObject);
                    }
                    if (cacheJsonObject.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObject);
                    }
                }
                LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                //凌晨时间戳-当前时间戳
                long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                //缓存
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                todayTask = jsonArrayForUser;
            } else {
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //设置积分数
                        if (patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Inviting_friends) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                            jsonObject.put("status", 1);
                            todayTask.add(jsonObject);
                        } else {
                            todayTask = patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Inviting_friends);
                        }
                    } else if (StringUtils.equals(entry.getKey() + "", "toChargeAware")) {
                        if (patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Write_down_an_account) == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", entry.getKey() + "");
                            jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                            jsonObject.put("status", 1);
                            todayTask.add(jsonObject);
                        } else {
                            todayTask = patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Write_down_an_account);
                        }
                    }
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newbieTask", newbieTask);
        jsonObject.put("todayTask", todayTask);
        return jsonObject;
    }

    /**
     * taskComplete--->mysql中的任务完成情况  jsonObject-->传入的需要返回的对象 acquisitionModeEnum-->对应的行为
     *
     * @param taskComplete
     * @param jsonObject
     * @return
     */
    private JSONObject patchDate(List<UserIntegralRestEntity> taskComplete, JSONObject jsonObject, AcquisitionModeEnum acquisitionModeEnum) {
        //遍历 ----> 追加奖励积分
        for (UserIntegralRestEntity userIntegralRestEntity : taskComplete) {
            if (userIntegralRestEntity.getType() == AcquisitionModeEnum.binding_phone_or_wx.getIndex()) {
                if (AcquisitionModeEnum.binding_phone_or_wx == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                    jsonObject.put("status", 2);
                }
            } else if (userIntegralRestEntity.getType() == AcquisitionModeEnum.Setting_up_budget.getIndex()) {
                if (AcquisitionModeEnum.Setting_up_budget == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                    jsonObject.put("status", 2);
                }
            } else if (userIntegralRestEntity.getType() == AcquisitionModeEnum.Setting_up_savings_efficiency.getIndex()) {
                if (AcquisitionModeEnum.Setting_up_savings_efficiency == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                    jsonObject.put("status", 2);
                }
            } else if (userIntegralRestEntity.getType() == AcquisitionModeEnum.Perfecting_personal_data.getIndex()) {
                if (AcquisitionModeEnum.Perfecting_personal_data == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                    jsonObject.put("status", 2);
                }
            } else if (userIntegralRestEntity.getType() == AcquisitionModeEnum.Write_down_an_account.getIndex()) {
                if (AcquisitionModeEnum.Write_down_an_account == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                    jsonObject.put("status", 2);
                }
            } else if (userIntegralRestEntity.getType() == AcquisitionModeEnum.Inviting_friends.getIndex()) {
                if (AcquisitionModeEnum.Inviting_friends == acquisitionModeEnum) {
                    jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                    jsonObject.put("status", 1);
                }
            }
        }
        //当只存在 积分数时 ---->追加当前类型
        if (jsonObject.size() == 1) {
            if (AcquisitionModeEnum.binding_phone_or_wx == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                jsonObject.put("status", 1);
            } else if (AcquisitionModeEnum.Setting_up_budget == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                jsonObject.put("status", 1);
            } else if (AcquisitionModeEnum.Setting_up_savings_efficiency == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                jsonObject.put("status", 1);
            } else if (AcquisitionModeEnum.Perfecting_personal_data == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                jsonObject.put("status", 1);
            } else if (AcquisitionModeEnum.Write_down_an_account == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                jsonObject.put("status", 1);
            } else if (AcquisitionModeEnum.Inviting_friends == acquisitionModeEnum) {
                jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                jsonObject.put("status", 1);
            }
        }
        return jsonObject;
    }

    /**
     * taskComplete--->cache中的任务完成情况  jsonObject-->传入的需要返回的对象 acquisitionModeEnum-->对应的行为
     *
     * @param taskComplete
     * @return
     */
    private JSONArray patchDate2(JSONArray taskComplete, int integral, AcquisitionModeEnum acquisitionModeEnum) {
        boolean flag = false;
        //遍历 ----> 追加奖励积分
        List<JSONObject> jsonObjects = JSONArray.parseArray(taskComplete.toJSONString(), JSONObject.class);
        for (JSONObject obj : jsonObjects) {
            if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.binding_phone_or_wx.getForUser()) && AcquisitionModeEnum.binding_phone_or_wx == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } else if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.Setting_up_budget.getForUser()) && AcquisitionModeEnum.Setting_up_budget == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } else if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser()) && AcquisitionModeEnum.Setting_up_savings_efficiency == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } else if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.Perfecting_personal_data.getForUser()) && AcquisitionModeEnum.Perfecting_personal_data == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } else if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.Write_down_an_account.getForUser()) && AcquisitionModeEnum.Write_down_an_account == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } else if (StringUtils.equals(obj.get("name") + "", AcquisitionModeEnum.Inviting_friends.getForUser()) && AcquisitionModeEnum.Inviting_friends == acquisitionModeEnum) {
                obj.put("integralAware", integral);
                flag = true;
                break;
            } /*else {
                //不相等 todo 有问题  已缓存个人信息！！！
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("integralAware", entry.getValue());
                jsonObject.put("name", acquisitionModeEnum.getForUser());
                jsonObject.put("description", acquisitionModeEnum.getDescription());
                jsonObject.put("status", 1);
                jsonObjects.add(jsonObject);
                break;
            }*/

        }
        if (flag) {
            return JSONArray.parseArray(JSON.toJSONString(jsonObjects));
        }
        return null;
    }

    @Override
    public JSONObject integralTop(String userInfoId) {
        //获取top3
        List<UserIntegralTopRestDTO> top = userIntegralRestDao.integralTop(3);
        UserIntegralTopRestDTO mySelf = userIntegralRestDao.integralForMySelf(userInfoId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("top3", top);
        jsonObject.put("mySelf", mySelf);
        return jsonObject;
    }

    @Override
    public JSONObject integralTop10(String userInfoId) {
        //获取top10
        List<UserIntegralTopRestDTO> top = userIntegralRestDao.integralTop(10);
        UserIntegralTopRestDTO mySelf = userIntegralRestDao.integralForMySelf(userInfoId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("top10", top);
        jsonObject.put("mySelf", mySelf);
        return jsonObject;
    }

    /**
     * 获取用户总积分数
     *
     * @param userInfoId
     * @return
     */
    @Override
    public double getUserTotalIntegral(String userInfoId) {
        String totalIntegral = userIntegralRestDao.getTotalIntegral(userInfoId);
        if (totalIntegral == null) {
            double sum = userIntegralRestDao.getTotalIntegralBySum(userInfoId);
            //赋值总积分表
            userIntegralRestDao.insertForTotalIntegral(userInfoId, new BigDecimal(sum), 1);
            return sum;
        } else {
            return Double.parseDouble(totalIntegral);
        }
    }

    /**
     * 获取今日任务/新手任务完成情况  处理新任务上架   下架的情况
     *
     * @param userInfoId
     * @return
     */
    @Override
    public JSONObject integralTask2(String userInfoId, String shareCode) {
        //查询任务对应积分数---->系统缓存数据
        Map<?, ?> cacheSysNewbieTask = getSysNewbieTask();
        Map<?, ?> cacheSysTodayTask = getSysTodayTask();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newbieTask", getNewbieTask(userInfoId, shareCode, cacheSysNewbieTask));
        jsonObject.put("todayTask", getTodayTask(userInfoId, shareCode, cacheSysTodayTask));
        return jsonObject;
    }

    /**
     * 获取系统配置的   新手任务
     *
     * @return
     */
    private Map<?, ?> getSysNewbieTask() {
        Map<?, ?> cacheSysNewbieTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
        //情况判断 系统新手任务缓存被删掉---->mysql重新查询
        if (cacheSysNewbieTask != null) {
            if (cacheSysNewbieTask.size() < 1) {
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                Period period = null;
                if (newBieTaskAware.size() > 0) {
                    //获取下线时间
                    if (newBieTaskAware.get(0).get("downtime") != null) {
                        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(newBieTaskAware.get(0).get("downtime") + "")), ZoneId.systemDefault());
                        LocalDate now = LocalDate.now();
                        if (ldt.toLocalDate().isAfter(now)) {
                            period = Period.between(now, ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : newBieTaskAware) {
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.binding_phone_or_wx.getName())) {
                        //绑定手机号/微信
                        jsonObject.put("bindPhoneOrWXAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //设置预算
                        jsonObject.put("budgetAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //设置存钱效率
                        jsonObject.put("savingEfficiencyAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //完善个人资料
                        jsonObject.put("userInfoAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Add_to_my_applet.getName())) {
                        //添加到我的小程序
                        jsonObject.put("add2wxappletAware", map.get("integraltaskaware"));
                    }/*else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Become_hbird_user.getName())) {
                        //成为蜂鸟记账用户
                        jsonObject.put("beUserAware", map.get("integraltaskaware"));
                    }*/
                }
                //缓存系统
                if (period != null) {
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, Long.valueOf(period.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                return jsonObject;
            }
        }
        return cacheSysNewbieTask;
    }

    /**
     * 获取系统配置的   每日任务 cacheSysTodayTask
     *
     * @return
     */
    private Map<?, ?> getSysTodayTask() {
        Map<?, ?> cacheSysTodayTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK);
        //情况判断 系统新手任务缓存被删掉---->mysql重新查询
        if (cacheSysTodayTask != null) {
            if (cacheSysTodayTask.size() < 1) {
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                Period period = null;
                if (todayTaskAware.size() > 0) {
                    //获取下线时间
                    if (todayTaskAware.get(0).get("downtime") != null) {
                        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(todayTaskAware.get(0).get("downtime") + "")), ZoneId.systemDefault());
                        LocalDate now = LocalDate.now();
                        if (ldt.toLocalDate().isAfter(now)) {
                            period = Period.between(now, ldt.toLocalDate());
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                for (Map<String, Object> map : todayTaskAware) {
                    if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Inviting_friends.getName())) {
                        //邀请好友
                        jsonObject.put("inviteFriendsAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //记一笔账
                        jsonObject.put("toChargeAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Get_a_new_Badge.getName())) {
                        //获得新徽章
                        jsonObject.put("getBadgeAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.The_invitation_came_to_five.getName())) {
                        //邀请达5人
                        jsonObject.put("invite2fiveAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.The_bookkeeping_came_to_three.getName())) {
                        //记账达3笔
                        jsonObject.put("charge2threeAware", map.get("integraltaskaware"));
                    }
                }
                //缓存系统
                if (period != null) {
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period.getDays() + 1));
                } else {
                    //缓存系统
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                return jsonObject;
            }
        }
        return cacheSysTodayTask;
    }

    /**
     * 获取个人   新手任务---->封装好数据
     *
     * @return
     */
    private JSONArray getNewbieTask(String userInfoId, String shareCode, Map<?, ?> cacheSysNewbieTask) {
        String cacheNewbieTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
        //添加用到的任务类型
        Map mapAdd = new HashMap();
        if (StringUtils.isNotEmpty(cacheNewbieTask)) {
            JSONArray newbieTask = JSONArray.parseArray(cacheNewbieTask);
            //处理新手任务
            JSONArray newbieTask2 = new JSONArray();
            newbieTask.forEach(v1 -> {
                JSONObject jsonObject = JSONObject.parseObject(v1 + "");
                String key = jsonObject.getString("name") + "Aware";
                Integer integral = Integer.valueOf(cacheSysNewbieTask.get(key) + "");
                jsonObject.put("integralAware", integral);
                newbieTask2.add(jsonObject);
                mapAdd.put(key, null);
            });
            return compareMap(cacheSysNewbieTask, mapAdd, shareCode, newbieTask2, 2, 1);
        } else {
            //查询任务对应积分数---->系统缓存数据
            List<UserIntegralRestEntity> newbieTask = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
            JSONArray newbieTaskArray = new JSONArray();
            newbieTask.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                //根据type 判断类型
                if (v.getType() == AcquisitionModeEnum.binding_phone_or_wx.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Setting_up_budget.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Setting_up_savings_efficiency.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Perfecting_personal_data.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Add_to_my_applet.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Add_to_my_applet.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Add_to_my_applet.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware", null);
                }/*else if (v.getType() == AcquisitionModeEnum.Become_hbird_user.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Become_hbird_user.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Become_hbird_user.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware", null);
                }*/
            });
            //上述是获取到的已完成的任务
            return compareMap(cacheSysNewbieTask, mapAdd, shareCode, newbieTaskArray, 1, 1);
        }
    }

    /**
     * 获取个人   每日任务---->封装好数据
     *
     * @return
     */
    private JSONArray getTodayTask(String userInfoId, String shareCode, Map<?, ?> cacheSysTodayTask) {
        String cacheTodayTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
        //添加用到的任务类型
        Map mapAdd = new HashMap();
        if (StringUtils.isNotEmpty(cacheTodayTask)) {
            JSONArray todayTask = JSONArray.parseArray(cacheTodayTask);
            //处理新手任务
            JSONArray todayTask2 = new JSONArray();
            todayTask.forEach(v1 -> {
                JSONObject jsonObject = JSONObject.parseObject(v1 + "");
                String key = jsonObject.getString("name") + "Aware";
                Integer integral = Integer.valueOf(cacheSysTodayTask.get(key) + "");
                jsonObject.put("integralAware", integral);
                todayTask2.add(jsonObject);
                mapAdd.put(key, null);
            });
            return compareMap(cacheSysTodayTask, mapAdd, shareCode, todayTask2, 2, 2);
        } else {
            //查询任务对应积分数---->系统缓存数据
            List<UserIntegralRestEntity> todayTask = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
            JSONArray todayTaskArray = new JSONArray();
            todayTask.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                //根据type 判断类型
                if (v.getType() == AcquisitionModeEnum.Inviting_friends.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 1);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Write_down_an_account.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Get_a_new_Badge.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Get_a_new_Badge.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Get_a_new_Badge.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.The_invitation_came_to_five.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.The_invitation_came_to_five.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.The_invitation_came_to_five.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.The_bookkeeping_came_to_three.getIndex()) {
                    //设置奖励积分数
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.The_bookkeeping_came_to_three.getDescription());
                    //领取状态 1:未领取 2:已领取
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware", null);
                }
            });
            //上述是获取到的已完成的任务
            return compareMap(cacheSysTodayTask, mapAdd, shareCode, todayTaskArray, 1, 2);
        }
    }

    @Test
    public void run2(){
        LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
        //凌晨时间戳-当前时间戳
        long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        System.out.println(cacheTime);
    }
    /**
     * 校验map长度
     *
     * @param cacheSysNewbieTask
     * @param mapAdd
     * @param newbieTask
     * @param flag               1 cache  2 no cache
     * @param task               区分任务类型   1 新手   2 今日
     * @return
     */
    private JSONArray compareMap(Map<?, ?> cacheSysNewbieTask, Map mapAdd, String shareCode, JSONArray newbieTask, int flag, int task) {
        //同理 需要判断两个map是否存在差集
        if (mapAdd.size() == cacheSysNewbieTask.size()) {
            if (flag == 1) {
                if (task == 1) {
                    //重新缓存  获取系统缓存有效期
                    Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, newbieTask.toJSONString(), expire, TimeUnit.SECONDS);
                } else {
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //凌晨时间戳-当前时间戳
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    logger.info("每日任务换成时间s:"+cacheTime);
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, newbieTask.toJSONString(), cacheTime, TimeUnit.SECONDS);
                }
            }
            return newbieTask;
        } else {
            MapDifference difference = Maps.difference(mapAdd, cacheSysNewbieTask);
            //取出系统任务中存在而 个人任务重不存在的map  置为未解锁状态
            Map map = difference.entriesOnlyOnRight();
            if (task == 1) {
                map.forEach((k, v) -> {
                    if (StringUtils.equals(k + "", AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Add_to_my_applet.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Add_to_my_applet.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }/*else if (StringUtils.equals(k + "", AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Become_hbird_user.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Become_hbird_user.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }*/
                });
                //重新缓存  获取系统缓存有效期
                Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, newbieTask.toJSONString(), expire, TimeUnit.SECONDS);
                return newbieTask;
            } else {
                map.forEach((k, v) -> {
                    if (StringUtils.equals(k + "", AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Get_a_new_Badge.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Get_a_new_Badge.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.The_invitation_came_to_five.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.The_invitation_came_to_five.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware") + "");
                        //设置奖励积分数
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.The_bookkeeping_came_to_three.getDescription());
                        //领取状态 1:未领取 2:已领取
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }
                });
                //重新缓存  获取系统缓存有效期
                LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                //凌晨时间戳-当前时间戳
                long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                logger.info("每日任务换成时间s:"+cacheTime);
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, newbieTask.toJSONString(), cacheTime, TimeUnit.SECONDS);
                return newbieTask;
            }
        }
    }

    @Test
    public void run() {
        Map mapAdd = new HashMap();
        mapAdd.put("a", "a");
        mapAdd.put("b", "b");
        Map cacheSysNewbieTask = new HashMap();
        cacheSysNewbieTask.put("a", "a");
        cacheSysNewbieTask.put("b", "b");
        cacheSysNewbieTask.put("1", "c");
        MapDifference difference = Maps.difference(mapAdd, cacheSysNewbieTask);
        Map map = difference.entriesOnlyOnRight();
        map.forEach((v, i) -> {
            System.out.println(v + ":" + i);
        });
    }
}