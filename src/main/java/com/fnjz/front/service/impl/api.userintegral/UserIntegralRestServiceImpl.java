package com.fnjz.front.service.impl.api.userintegral;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralTopRestDTO;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userIntegralRestService")
@Transactional
public class UserIntegralRestServiceImpl extends CommonServiceImpl implements UserIntegralRestServiceI {

    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Override
    public void signInIntegral(String userInfoId, String shareCode, Map<String, String> map) {
        //根据cycle 判断周数
        String cycle = map.get("cycle");
        if (StringUtils.isNotEmpty(cycle)) {
            //判断签到天数是否达标
            int signInDays = redisTemplateUtils.getForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays");
            if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_7.getIndex() + "")) {
                //判断领取状态
                int signIn_7 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_7");
                if (signIn_7 != 2 && signInDays >= IntegralEnum.SIGNIN_7.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_7.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_7.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_7", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_14.getIndex() + "")) {
                int signIn_14 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_14");
                if (signIn_14 != 2 && signInDays >= IntegralEnum.SIGNIN_14.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_14.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_14.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_14", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_21.getIndex() + "")) {
                int signIn_21 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_21");
                if (signIn_21 != 2 && signInDays >= IntegralEnum.SIGNIN_21.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_21.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_21.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_21", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_28.getIndex() + "")) {
                int signIn_28 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_28");
                if (signIn_28 != 2 && signInDays >= IntegralEnum.SIGNIN_28.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_28.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_28.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_28", 2);
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
                if(newBieTaskAware.size()>0){
                    //获取下线时间
                    if(StringUtils.isNotEmpty(newBieTaskAware.get(0).get("downtime")+"")){
                        LocalDateTime ldt = LocalDateTime.parse(newBieTaskAware.get(0).get("downtime")+"", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if(ldt.toLocalDate().isAfter(LocalDate.now())){
                            period1 = Period.between(LocalDate.now(),ldt.toLocalDate());
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
                if(period1!=null){
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject,Long.valueOf(period1.getDays()+1));
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),Long.valueOf(period1.getDays()+1));
                }else{
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject,RedisPrefix.USER_VALID_TIME);
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),RedisPrefix.USER_VALID_TIME);
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
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_NEWBIE_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),expire,TimeUnit.MILLISECONDS);
            newbieTask = jsonArrayForUser;
        } else {
            //判断系统缓存---->整合数据
            Period period1 = null;
            if (cacheSysNewbieTask.size() == 0) {
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> newBieTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.NewbieTask.getName());
                if(newBieTaskAware.size()>0){
                    //获取下线时间
                    if(StringUtils.isNotEmpty(newBieTaskAware.get(0).get("downtime")+"")){
                        LocalDateTime ldt = LocalDateTime.parse(newBieTaskAware.get(0).get("downtime")+"", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if(ldt.toLocalDate().isAfter(LocalDate.now())){
                            period1 = Period.between(LocalDate.now(),ldt.toLocalDate());
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
                if(period1!=null){
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject,Long.valueOf(period1.getDays()+1));
                }else{
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_NEWBIE_TASK, jsonObject,RedisPrefix.USER_VALID_TIME);
                }
                cacheSysNewbieTask = jsonObject;
            }
            for (Map.Entry entry : cacheSysNewbieTask.entrySet()) {
                if (StringUtils.equals(entry.getKey() + "", "bindPhoneOrWXAware")) {
                    //设置积分数
                    if(patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.binding_phone_or_wx)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.binding_phone_or_wx.getDescription());
                        jsonObject.put("status",1);
                        newbieTask.add(jsonObject);
                    }else{
                        newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.binding_phone_or_wx);
                    }
                } else if (StringUtils.equals(entry.getKey() + "", "budgetAware")) {
                    if(patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_budget)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.Setting_up_budget.getDescription());
                        jsonObject.put("status",1);
                        newbieTask.add(jsonObject);
                    }else{
                        newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_budget);
                    }
                } else if (StringUtils.equals(entry.getKey() + "", "savingEfficiencyAware")) {
                    if(patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_savings_efficiency)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.Setting_up_savings_efficiency.getDescription());
                        jsonObject.put("status",1);
                        newbieTask.add(jsonObject);
                    }else{
                        newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Setting_up_savings_efficiency);
                    }
                } else if (StringUtils.equals(entry.getKey() + "", "userInfoAware")) {
                    if(patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Perfecting_personal_data)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.Perfecting_personal_data.getDescription());
                        jsonObject.put("status",1);
                        newbieTask.add(jsonObject);
                    }else{
                        newbieTask = patchDate2(newbieTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Perfecting_personal_data);
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
                if(todayTaskAware.size()>0){
                    //获取下线时间
                    if(StringUtils.isNotEmpty(todayTaskAware.get(0).get("downtime")+"")){
                        LocalDateTime ldt = LocalDateTime.parse(todayTaskAware.get(0).get("downtime")+"", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if(ldt.toLocalDate().isAfter(LocalDate.now())){
                            period1 = Period.between(LocalDate.now(),ldt.toLocalDate());
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
                if(period1!=null){
                    //设置缓存时间
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject,Long.valueOf(period1.getDays()+1));
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),Long.valueOf(period1.getDays()+1));
                }else{
                    //缓存系统
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject,RedisPrefix.USER_VALID_TIME);
                    //个人
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),RedisPrefix.USER_VALID_TIME);
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
            //获取系统缓存有效期
            Long expire = redisTemplateUtils.getExpireForSeconds(RedisPrefix.SYS_INTEGRAL_TODAY_TASK);
            //缓存
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_TODAY_TASK + shareCode, cacheJsonArrayForUser.toJSONString(),expire,TimeUnit.MILLISECONDS);
            todayTask = jsonArrayForUser;
        } else {
            //判断系统缓存---->整合数据
            if (cacheSysTodayTask.size() == 0) {
                Period period1 =null;
                //未缓存新手任务缓存---->重查
                List<Map<String, Object>> todayTaskAware = fengFengTicketRestDao.getIntegralTaskAware(CategoryOfBehaviorEnum.TodayTask.getName());
                if(todayTaskAware.size()>0){
                    //获取下线时间
                    if(StringUtils.isNotEmpty(todayTaskAware.get(0).get("downtime")+"")){
                        LocalDateTime ldt = LocalDateTime.parse(todayTaskAware.get(0).get("downtime")+"", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if(ldt.toLocalDate().isAfter(LocalDate.now())){
                            period1 = Period.between(LocalDate.now(),ldt.toLocalDate());
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
                if(period1!=null){
                    //缓存系统
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject,Long.valueOf(period1.getDays()+1));
                }else{
                    redisTemplateUtils.updateForHash(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, jsonObject,RedisPrefix.USER_VALID_TIME);
                }
                cacheSysTodayTask = jsonObject;
            }
            for (Map.Entry entry : cacheSysTodayTask.entrySet()) {
                if (StringUtils.equals(entry.getKey() + "", "inviteFriendsAware")) {
                    //设置积分数
                    if(patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Inviting_friends)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.Inviting_friends.getDescription());
                        jsonObject.put("status",1);
                        todayTask.add(jsonObject);
                    }else{
                        todayTask = patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Inviting_friends);
                    }
                } else if (StringUtils.equals(entry.getKey() + "", "toChargeAware")) {
                    if(patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Write_down_an_account)==null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",entry.getKey() + "");
                        jsonObject.put("description",AcquisitionModeEnum.Write_down_an_account.getDescription());
                        jsonObject.put("status",1);
                        todayTask.add(jsonObject);
                    }else{
                        todayTask = patchDate2(todayTask, Integer.valueOf(entry.getValue() + ""), AcquisitionModeEnum.Write_down_an_account);
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
                    jsonObject.put("status", 2);
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
        if(flag){
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
}