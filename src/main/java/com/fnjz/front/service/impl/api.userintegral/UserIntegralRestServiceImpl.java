package com.fnjz.front.service.impl.api.userintegral;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserInfoRestDao;
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
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.RedisLockUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RedisLockUtils redisLock;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    @Override
    public void signInIntegral(String userInfoId, String shareCode, Map<String, String> map) {
        //??????cycle ????????????
        String cycle = map.get("cycle");
        if (StringUtils.isNotEmpty(cycle)) {
            //??????
            redisLock.lock(userInfoId);
            //????????????????????????
            UserSignInAwardRestEntity entity = userSignInAwardRestDao.getGetTimesAndAwardStatus(userInfoId, cycle);
            if (entity.getGetTimes() != null) {
                if (entity.getGetTimes() > 0) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), Integer.valueOf(cycle));
                    //????????????????????????????????? times-1     ????????????????????????0 ???????????????1?????????????????????2
                    UserSignInAwardRestEntity bean;
                    if ((entity.getGetTimes() - 1) == 0 && entity.getCycleAwardStatus() == 1) {
                        bean = new UserSignInAwardRestEntity(Integer.valueOf(userInfoId), CategoryOfBehaviorEnum.SignIn.getName(), Integer.valueOf(cycle), 2, -1, 0);
                    } else {
                        bean = new UserSignInAwardRestEntity(Integer.valueOf(userInfoId), CategoryOfBehaviorEnum.SignIn.getName(), Integer.valueOf(cycle), null, -1, 0);
                    }
                    userSignInAwardRestDao.update(bean);
                    //????????????????????????
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), Integer.valueOf(cycle), CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(ff.getBehaviorTicketValue() + ""));
                    //??????????????????
                    userIntegralRestDao.updateForTotalIntegral(userInfoId, ff.getBehaviorTicketValue(), new BigDecimal(ff.getBehaviorTicketValue() + ""));
                    //??????
                    createTokenUtils.addIntegralByInvitedUser(userInfoId, ff, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.BONUS);
                }
            }
            //??????
            redisLock.unlock(userInfoId);
        }
    }

    @Override
    public PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize, Integer type) {
        PageRest pageRest = new PageRest();
        if (curPage != null) {
            pageRest.setCurPage(curPage);
        }
        if (pageSize != null) {
            pageRest.setPageSize(pageSize);
        }
        if (type != null) {
            if (type == AcquisitionModeEnum.BONUS.getIndex()) {
                List<UserIntegralRestDTO> listForPage = userIntegralRestDao.listForPage(userInfoId, pageRest.getStartIndex(), pageRest.getPageSize(), type);
                //???????????????
                Integer count = userIntegralRestDao.getCount(userInfoId, type);
                //??????????????????
                pageRest.setTotalCount(count);
                //??????????????????
                pageRest.setContent(listForPage);
                return pageRest;
            }
        }
        List<UserIntegralRestDTO> listForPage = userIntegralRestDao.listForPage(userInfoId, pageRest.getStartIndex(), pageRest.getPageSize());
        //???????????????
        Integer count = userIntegralRestDao.getCount(userInfoId);
        //??????????????????
        pageRest.setTotalCount(count);
        //??????????????????
        pageRest.setContent(listForPage);
        return pageRest;
    }

    /**
     * ??????????????????/????????????????????????
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
        //???????????????????????????---->??????????????????
        Map<?, ?> cacheSysNewbieTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
        Map<?, ?> cacheSysTodayTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK);
        if (cacheNewbieTask == null) {
            //???????????????????????? ????????????????????????????????????
            List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
            JSONArray jsonArrayForUser = new JSONArray();
            JSONArray cacheJsonArrayForUser = new JSONArray();
            Period period1 = null;
            if (cacheSysNewbieTask.size() == 0) {
                //???????????????????????????---->??????
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                if (newBieTaskAware.size() > 0) {
                    //??????????????????
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
                        //???????????????/??????
                        jsonObject.put("bindPhoneOrWXAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        //TODO jsonObject????????????   jsonObjectForUser????????????---->??????????????????  ??????????????????
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.binding_phone_or_wx);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //????????????
                        jsonObject.put("budgetAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Setting_up_budget);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //??????????????????
                        jsonObject.put("savingEfficiencyAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Setting_up_savings_efficiency);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //??????????????????
                        jsonObject.put("userInfoAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Perfecting_personal_data);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    }
                    //??????user
                    if (jsonObjectForUser.size() > 0) {
                        jsonArrayForUser.add(jsonObjectForUser);
                    }
                    //cache
                    if (cacheJsonObjectForUser.size() > 0) {
                        cacheJsonArrayForUser.add(cacheJsonObjectForUser);
                    }
                }
                //????????????
                if (period1 != null) {
                    //??????????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                    //??????
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                    //??????
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), RedisPrefix.USER_VALID_TIME);
                }
                newbieTask = jsonArrayForUser;
            } else {
                //??????????????????---->????????????
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //???????????????
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
            //???????????????????????????
            Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
            //??????
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), expire, TimeUnit.SECONDS);
            newbieTask = jsonArrayForUser;
        } else {
            //??????????????????---->????????????
            Period period1 = null;
            if (cacheSysNewbieTask.size() == 0) {
                //???????????????????????????---->??????
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                if (newBieTaskAware.size() > 0) {
                    //??????????????????
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
                        //???????????????/??????
                        jsonObject.put("bindPhoneOrWXAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //????????????
                        jsonObject.put("budgetAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //??????????????????
                        jsonObject.put("savingEfficiencyAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //??????????????????
                        jsonObject.put("userInfoAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    }
                }
                //????????????
                if (period1 != null) {
                    //??????????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                cacheSysNewbieTask = jsonObject;
            }
            //????????????????????????null  ??????????????????null ??????size???????????????
            if (newbieTask.size() != cacheSysNewbieTask.size()) {
                redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
                //???????????????????????? ????????????????????????????????????
                List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
                JSONArray cacheJsonArrayForUser = new JSONArray();
                //??????????????????---->????????????
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObjectForUser = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //???????????????
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
                //???????????????????????????
                Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                //??????
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), expire, TimeUnit.SECONDS);
                newbieTask = cacheJsonArrayForUser;
            } else {
                for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                    if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                        //???????????????
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
            //???????????????????????????---->??????????????????
            List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
            JSONArray jsonArrayForUser = new JSONArray();
            JSONArray cacheJsonArrayForUser = new JSONArray();
            Period period1 = null;
            if (cacheSysTodayTask.size() == 0) {
                //???????????????????????????---->??????
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                if (todayTaskAware.size() > 0) {
                    //??????????????????
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
                        //????????????
                        jsonObject.put("inviteFriendsAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser.put("integralAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                        jsonObjectForUser = patchDate(taskComplete, jsonObjectForUser, AcquisitionModeEnum.Inviting_friends);
                        cacheJsonObjectForUser = (JSONObject) jsonObjectForUser.clone();
                        cacheJsonObjectForUser.remove("integralAware");
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //????????????
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
                //????????????
                if (period1 != null) {
                    //??????????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                    //??????
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //???????????????-???????????????
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                } else {
                    //????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                    //??????
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //???????????????-???????????????
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                }
                todayTask = jsonArrayForUser;
            } else {
                //??????????????????---->????????????
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObject = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //?????????????????????
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
            //???????????????-???????????????
            long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            //??????
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
            todayTask = jsonArrayForUser;
        } else {
            //??????????????????---->????????????
            if (cacheSysTodayTask.size() == 0) {
                Period period1 = null;
                //???????????????????????????---->??????
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                if (todayTaskAware.size() > 0) {
                    //??????????????????
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
                        //????????????
                        jsonObject.put("inviteFriendsAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //????????????
                        jsonObject.put("toChargeAware", Integer.valueOf(map.get("integraltaskaware") + ""));
                    }
                }
                if (period1 != null) {
                    //????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period1.getDays() + 1));
                } else {
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                cacheSysTodayTask = jsonObject;
            }
            //???????????????????????????????????????
            if (cacheSysTodayTask.size() != todayTask.size()) {
                redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
                //???????????????????????????---->??????????????????
                List<UserIntegralRestEntity> taskComplete = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
                JSONArray jsonArrayForUser = new JSONArray();
                JSONArray cacheJsonArrayForUser = new JSONArray();
                //??????????????????---->????????????
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject cacheJsonObject = new JSONObject();
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //?????????????????????
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
                //???????????????-???????????????
                long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                //??????
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(), cacheTime, TimeUnit.SECONDS);
                todayTask = jsonArrayForUser;
            } else {
                for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                    if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                        //???????????????
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
     * taskComplete--->mysql????????????????????????  jsonObject-->?????????????????????????????? acquisitionModeEnum-->???????????????
     *
     * @param taskComplete
     * @param jsonObject
     * @return
     */
    private JSONObject patchDate(List<UserIntegralRestEntity> taskComplete, JSONObject jsonObject, AcquisitionModeEnum acquisitionModeEnum) {
        //?????? ----> ??????????????????
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
        //???????????? ???????????? ---->??????????????????
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
     * taskComplete--->cache????????????????????????  jsonObject-->?????????????????????????????? acquisitionModeEnum-->???????????????
     *
     * @param taskComplete
     * @return
     */
    private JSONArray patchDate2(JSONArray taskComplete, int integral, AcquisitionModeEnum acquisitionModeEnum) {
        boolean flag = false;
        //?????? ----> ??????????????????
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
                //????????? todo ?????????  ??????????????????????????????
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
        //??????top3
        List<UserIntegralTopRestDTO> top = userIntegralRestDao.integralTop(3);
        //UserIntegralTopRestDTO mySelf = userIntegralRestDao.integralForMySelf(userInfoId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("top3", top);
        //jsonObject.put("mySelf", mySelf);
        return jsonObject;
    }

    @Override
    public JSONObject integralTop10(String userInfoId) {
        //??????top10
        List<UserIntegralTopRestDTO> top = userIntegralRestDao.integralTop(10);
        for (int i = 0; i < top.size(); i++) {
            //????????????
            top.get(i).setRank(i + 1);
        }
        //??????????????????
        UserIntegralTopRestDTO mySelf = userIntegralRestDao.integralForMySelf(userInfoId);
        //??????????????????
        String totalIntegral = userIntegralRestDao.getTotalIntegral(userInfoId);
        mySelf.setIntegralNum(new BigDecimal(totalIntegral==null?"0":totalIntegral));
        //??????????????????  ??????
        Map<String, Object> map = userInfoRestDao.getNKAndAUById(Integer.valueOf(userInfoId));
        mySelf.setNickName((String) map.get("nickname"));
        mySelf.setAvatarUrl((String) map.get("avatarurl"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("top10", top);
        jsonObject.put("mySelf", mySelf);
        return jsonObject;
    }

    /**
     * ????????????????????????
     *
     * @param userInfoId
     * @return
     */
    @Override
    public double getUserTotalIntegral(String userInfoId) {
        String totalIntegral = userIntegralRestDao.getTotalIntegral(userInfoId);
        if (totalIntegral == null) {
            double sum = userIntegralRestDao.getTotalIntegralBySum(userInfoId);
            //??????????????????
            userIntegralRestDao.insertForTotalIntegral(userInfoId, new BigDecimal(sum), 1);
            return sum;
        } else {
            return Double.parseDouble(totalIntegral);
        }
    }

    /**
     * ??????????????????/????????????????????????  ?????????????????????   ???????????????
     *
     * @param userInfoId
     * @return
     */
    @Override
    public JSONObject integralTask2(String userInfoId, String shareCode) {
        //???????????????????????????---->??????????????????
        Map<?, ?> cacheSysNewbieTask = getSysNewbieTask();
        Map<?, ?> cacheSysTodayTask = getSysTodayTask();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newbieTask", getNewbieTask(userInfoId, shareCode, cacheSysNewbieTask));
        jsonObject.put("todayTask", getTodayTask(userInfoId, shareCode, cacheSysTodayTask));
        return jsonObject;
    }

    /**
     * ?????????????????????   ????????????
     *
     * @return
     */
    private Map<?, ?> getSysNewbieTask() {
        Map<?, ?> cacheSysNewbieTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
        //???????????? ?????????????????????????????????---->mysql????????????
        if (cacheSysNewbieTask != null) {
            if (cacheSysNewbieTask.size() < 1) {
                //???????????????????????????---->??????
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                Period period = null;
                if (newBieTaskAware.size() > 0) {
                    //??????????????????
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
                        //???????????????/??????
                        jsonObject.put("bindPhoneOrWXAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_budget.getName())) {
                        //????????????
                        jsonObject.put("budgetAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getName())) {
                        //??????????????????
                        jsonObject.put("savingEfficiencyAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Perfecting_personal_data.getName())) {
                        //??????????????????
                        jsonObject.put("userInfoAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Add_to_my_applet.getName())) {
                        //????????????????????????
                        jsonObject.put("add2wxappletAware", map.get("integraltaskaware"));
                    }/*else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Become_hbird_user.getName())) {
                        //????????????????????????
                        jsonObject.put("beUserAware", map.get("integraltaskaware"));
                    }*/
                }
                //????????????
                if (period != null) {
                    //??????????????????
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
     * ?????????????????????   ???????????? cacheSysTodayTask
     *
     * @return
     */
    private Map<?, ?> getSysTodayTask() {
        Map<?, ?> cacheSysTodayTask = redisTemplateUtils.getForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK);
        //???????????? ?????????????????????????????????---->mysql????????????
        if (cacheSysTodayTask != null) {
            if (cacheSysTodayTask.size() < 1) {
                //???????????????????????????---->??????
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                Period period = null;
                if (todayTaskAware.size() > 0) {
                    //??????????????????
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
                        //????????????
                        jsonObject.put("inviteFriendsAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Write_down_an_account.getName())) {
                        //????????????
                        jsonObject.put("toChargeAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.Get_a_new_Badge.getName())) {
                        //???????????????
                        jsonObject.put("getBadgeAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.The_invitation_came_to_five.getName())) {
                        //?????????5???
                        jsonObject.put("invite2fiveAware", map.get("integraltaskaware"));
                    } else if (StringUtils.equals(map.get("acquisitionmode") + "", AcquisitionModeEnum.The_bookkeeping_came_to_three.getName())) {
                        //?????????3???
                        jsonObject.put("charge2threeAware", map.get("integraltaskaware"));
                    }
                }
                //????????????
                if (period != null) {
                    //??????????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, Long.valueOf(period.getDays() + 1));
                } else {
                    //????????????
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject, RedisPrefix.USER_VALID_TIME);
                }
                return jsonObject;
            }
        }
        return cacheSysTodayTask;
    }

    /**
     * ????????????   ????????????---->???????????????
     *
     * @return
     */
    private JSONArray getNewbieTask(String userInfoId, String shareCode, Map<?, ?> cacheSysNewbieTask) {
        String cacheNewbieTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode);
        //???????????????????????????
        Map mapAdd = new HashMap();
        if (StringUtils.isNotEmpty(cacheNewbieTask)) {
            JSONArray newbieTask = JSONArray.parseArray(cacheNewbieTask);
            //??????????????????
            JSONArray newbieTask2 = new JSONArray();
            newbieTask.forEach(v1 -> {
                JSONObject jsonObject = JSONObject.parseObject(v1 + "");
                String key = jsonObject.getString("name") + "Aware";

                if (cacheSysNewbieTask.get(key) != null) {
                    Integer integral = Integer.valueOf(cacheSysNewbieTask.get(key) + "");
                    jsonObject.put("integralAware", integral);
                    newbieTask2.add(jsonObject);
                    mapAdd.put(key, null);
                }
            });
            return compareMap(cacheSysNewbieTask, mapAdd, shareCode, newbieTask2, 2, 1);
        } else {
            //???????????????????????????---->??????????????????
            List<UserIntegralRestEntity> newbieTask = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.NewbieTask.getIndex(), userInfoId);
            JSONArray newbieTaskArray = new JSONArray();
            newbieTask.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                //??????type ????????????
                if (v.getType() == AcquisitionModeEnum.binding_phone_or_wx.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Setting_up_budget.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Setting_up_savings_efficiency.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Perfecting_personal_data.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Add_to_my_applet.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Add_to_my_applet.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Add_to_my_applet.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware", null);
                }/*else if (v.getType() == AcquisitionModeEnum.Become_hbird_user.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Become_hbird_user.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Become_hbird_user.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    newbieTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware", null);
                }*/
            });
            //???????????????????????????????????????
            return compareMap(cacheSysNewbieTask, mapAdd, shareCode, newbieTaskArray, 1, 1);
        }
    }

    /**
     * ????????????   ????????????---->???????????????
     *
     * @return
     */
    private JSONArray getTodayTask(String userInfoId, String shareCode, Map<?, ?> cacheSysTodayTask) {
        String cacheTodayTask = redisTemplateUtils.getForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode);
        //???????????????????????????
        Map mapAdd = new HashMap();
        if (StringUtils.isNotEmpty(cacheTodayTask)) {
            JSONArray todayTask = JSONArray.parseArray(cacheTodayTask);
            //??????????????????
            JSONArray todayTask2 = new JSONArray();
            todayTask.forEach(v1 -> {
                JSONObject jsonObject = JSONObject.parseObject(v1 + "");
                String key = jsonObject.getString("name") + "Aware";
                if (cacheSysTodayTask.get(key) != null) {
                    Integer integral = Integer.valueOf(cacheSysTodayTask.get(key) + "");
                    jsonObject.put("integralAware", integral);
                    todayTask2.add(jsonObject);
                    mapAdd.put(key, null);
                }
            });
            return compareMap(cacheSysTodayTask, mapAdd, shareCode, todayTask2, 2, 2);
        } else {
            //???????????????????????????---->??????????????????
            List<UserIntegralRestEntity> todayTask = userIntegralRestDao.getTaskComplete(CategoryOfBehaviorEnum.TodayTask.getIndex(), userInfoId);
            JSONArray todayTaskArray = new JSONArray();
            todayTask.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                //??????type ????????????
                if (v.getType() == AcquisitionModeEnum.Inviting_friends.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 1);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Write_down_an_account.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.Get_a_new_Badge.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.Get_a_new_Badge.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.Get_a_new_Badge.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.The_invitation_came_to_five.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.The_invitation_came_to_five.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.The_invitation_came_to_five.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware", null);
                } else if (v.getType() == AcquisitionModeEnum.The_bookkeeping_came_to_three.getIndex()) {
                    //?????????????????????
                    jsonObject.put("integralAware", v.getIntegralNum());
                    jsonObject.put("name", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser());
                    jsonObject.put("description", AcquisitionModeEnum.The_bookkeeping_came_to_three.getDescription());
                    //???????????? 1:????????? 2:?????????
                    jsonObject.put("status", 2);
                    todayTaskArray.add(jsonObject);
                    mapAdd.put(AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware", null);
                }
            });
            //???????????????????????????????????????
            return compareMap(cacheSysTodayTask, mapAdd, shareCode, todayTaskArray, 1, 2);
        }
    }

    @Test
    public void run2() {
        LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
        //???????????????-???????????????
        long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        System.out.println(cacheTime);
    }

    /**
     * ??????map??????
     *
     * @param cacheSysNewbieTask
     * @param mapAdd
     * @param newbieTask
     * @param flag               1 cache  2 no cache
     * @param task               ??????????????????   1 ??????   2 ??????
     * @return
     */
    private JSONArray compareMap(Map<?, ?> cacheSysNewbieTask, Map mapAdd, String shareCode, JSONArray newbieTask, int flag, int task) {
        //?????? ??????????????????map??????????????????
        if (mapAdd.size() == cacheSysNewbieTask.size()) {
            if (flag == 1) {
                if (task == 1) {
                    //????????????  ???????????????????????????
                    Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, newbieTask.toJSONString(), expire, TimeUnit.SECONDS);
                } else {
                    LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                    //???????????????-???????????????
                    long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                    logger.info("????????????????????????s:" + cacheTime);
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, newbieTask.toJSONString(), cacheTime, TimeUnit.SECONDS);
                }
            }
            return newbieTask;
        } else {
            MapDifference difference = Maps.difference(mapAdd, cacheSysNewbieTask);
            //?????????????????????????????? ???????????????????????????map  ?????????????????????
            Map map = difference.entriesOnlyOnRight();
            if (task == 1) {
                map.forEach((k, v) -> {
                    if (StringUtils.equals(k + "", AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.binding_phone_or_wx.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.binding_phone_or_wx.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Setting_up_budget.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Setting_up_budget.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Setting_up_budget.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Setting_up_savings_efficiency.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Perfecting_personal_data.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Perfecting_personal_data.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Add_to_my_applet.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Add_to_my_applet.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Add_to_my_applet.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }/*else if (StringUtils.equals(k + "", AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Become_hbird_user.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Become_hbird_user.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Become_hbird_user.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }*/
                });
                //????????????  ???????????????????????????
                Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK);
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, newbieTask.toJSONString(), expire, TimeUnit.SECONDS);
                return newbieTask;
            } else {
                map.forEach((k, v) -> {
                    if (StringUtils.equals(k + "", AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Inviting_friends.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Inviting_friends.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Inviting_friends.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Write_down_an_account.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Write_down_an_account.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Write_down_an_account.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.Get_a_new_Badge.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.Get_a_new_Badge.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.Get_a_new_Badge.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.The_invitation_came_to_five.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.The_invitation_came_to_five.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.The_invitation_came_to_five.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    } else if (StringUtils.equals(k + "", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware")) {
                        JSONObject jsonObject = new JSONObject();
                        Integer integral = Integer.valueOf(cacheSysNewbieTask.get(AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser() + "Aware") + "");
                        //?????????????????????
                        jsonObject.put("integralAware", integral);
                        jsonObject.put("name", AcquisitionModeEnum.The_bookkeeping_came_to_three.getForUser());
                        jsonObject.put("description", AcquisitionModeEnum.The_bookkeeping_came_to_three.getDescription());
                        //???????????? 1:????????? 2:?????????
                        jsonObject.put("status", 1);
                        newbieTask.add(jsonObject);
                    }
                });
                //????????????  ???????????????????????????
                LocalDateTime time = LocalDate.now().atTime(23, 59, 59);
                //???????????????-???????????????
                long cacheTime = time.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                logger.info("????????????????????????s:" + cacheTime);
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