package com.fnjz.front.service.impl.api.usersignin;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.sharewords.ShareWordsRestDTO;
import com.fnjz.front.entity.api.usersignin.UserSignInRestDTO;
import com.fnjz.front.entity.api.usersignin.UserSignInRestEntity;
import com.fnjz.front.entity.api.usersigninaward.UserSignInAwardRestDTO;
import com.fnjz.front.entity.api.usersigninaward.UserSignInAwardRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.enums.SignInEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.service.api.usersignin.UserSignInRestServiceI;
import com.fnjz.front.service.api.userwxqrcode.UserWXQrCodeRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
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

    @Autowired
    private ShareWordsRestDao shareWordsRestDao;

    @Autowired
    private UserSignInAwardRestDao userSignInAwardRestDao;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private UserWXQrCodeRestServiceI userWXQrCodeRestServiceI;

    /**
     * 签到
     *
     * @param userInfoId
     */
    @Override
    @Deprecated
    public ShareWordsRestDTO signIn(String userInfoId, String shareCode) {
        Map map = signInForCache(userInfoId, shareCode);
        FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_1.getIndex());
        //未录入或该记录不可用
        ShareWordsRestDTO dto = new ShareWordsRestDTO();
        if (ff == null) {
            dto.setSignInAware(-1);
        }else{
            dto.setSignInAware(ff.getBehaviorTicketValue());
        }
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
                    userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_1.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(ff.getBehaviorTicketValue() + ""));
                    userIntegralRestDao.updateForTotalIntegral(userInfoId, ff.getBehaviorTicketValue(), new BigDecimal(ff.getBehaviorTicketValue()));
                }
            }
        }
        //更新redis缓存  去掉是否签到标识
        if (map.get("hasSigned") != null) {
            map.remove("hasSigned");
        }
        redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map);
        return getShareWords(dto,userInfoId);
    }

    /**
     * 获取签到分享话术
     *
     * @return
     */
    private ShareWordsRestDTO getShareWords(ShareWordsRestDTO shareWords,String userInfoId) {
        ShareWordsRestDTO shareWords2 = shareWordsRestDao.getShareWords();
        if(shareWords2!=null){
            BeanUtils.copyProperties(shareWords2, shareWords,new String[]{"signInAware"});
        }
        //获取昵称 头像 注册时间
        Map<String, Object> nkAndAUById = userInfoRestDao.getNKAndAUById(Integer.valueOf(userInfoId));
        shareWords.setNickName(nkAndAUById.get("nickname")+"");
        shareWords.setAvatarUrl(nkAndAUById.get("avatarurl")+"");
        shareWords.setRegisterDate((Date)nkAndAUById.get("registerdate"));
        //获取累计记账天数
        shareWords.setChargeDays(getChargeDays(RedisPrefix.PREFIX_MY_COUNT+ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)),userInfoId));
        //获取小程序分享二维码
        String inviteQrCode = userWXQrCodeRestServiceI.getInviteQrCode(userInfoId, "2");
        shareWords.setQrCodeUrl(inviteQrCode);
        return shareWords;
    }

    /**
     * 获取累计记账天数
     */
    private int getChargeDays(String shareCode, String userInfoId) {
        //统计记账总笔数+1
        Map s = redisTemplateUtils.getMyCount(shareCode);
        if (s.size() > 0) {
            //设置当前时间戳 为时间标识   累计记账天数
            if (s.containsKey("chargeDays")) {
                return Integer.valueOf(s.get("chargeDays") + "");
            } else {
                //查询累计记账天数
                int totalChargeDays = warterOrderRestDao.getTotalChargeDays(userInfoId);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeDays", totalChargeDays);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
                return totalChargeDays;
            }
        } else {
            //查询累计记账天数
            int totalChargeDays = warterOrderRestDao.getTotalChargeDays(userInfoId);
            redisTemplateUtils.updateForHashKey(shareCode, "chargeDays", totalChargeDays);
            redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
            return totalChargeDays;
        }
    }

    private Map signInForCache(String userInfoId, String shareCode) {
        Map map = redisTemplateUtils.getForHash(PREFIX_SIGN_IN + shareCode);
        //读取签到奖励规则
        List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        //判断打卡间隔 获取下一天凌晨时间间隔
        Date nextDay = DateUtils.getNextDay(new Date(Long.valueOf(map.get("signInDate") + "")));
        LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).atZone(ZoneOffset.systemDefault()).toEpochSecond();
        //获取当天凌晨范围
        Date dateOfBegin = DateUtils.fetchBeginOfDay(nextDay);
        Date dateOfEnd = DateUtils.fetchEndOfDay(nextDay);
        long now = System.currentTimeMillis();
        if (now > dateOfBegin.getTime() && now < dateOfEnd.getTime()) {
            //获取最大周数
            JSONObject jsonObject = JSONObject.parseObject(list.get(list.size() - 1));
            Iterator iterator = jsonObject.keySet().iterator();
            int value = Integer.valueOf(iterator.next() + "");
            if ((Integer.valueOf(map.get("signInDays") + "") + 1) % (value + 1) == 0) {
                //达到周期上限
                map.put("signInDays", 1);
            } else {
                map.put("signInDays", (Integer.valueOf(map.get("signInDays") + "") + 1));
            }
            map.put("signInDate", System.currentTimeMillis());
        } else if (now > dateOfEnd.getTime()) {
            //置空
            map.put("signInDays", 1);
            map.put("signInDate", System.currentTimeMillis());
        } else {
            //已签到情况下
            map.put("hasSigned", true);
        }
        //修改奖励领取状态
        resetSignInAward(Integer.valueOf(userInfoId), Integer.valueOf(map.get("signInDays") + ""), list);
        return map;
    }

    /**
     * 根据连签天数修改对应签到奖励的状态
     * list 有序
     */
    private void resetSignInAward(Integer userInfoId, Integer signInDays, List<String> list) {
        //判断是否达标
        for (String v : list) {
            JSONObject jsonObject = JSONObject.parseObject(v);
            Iterator iterator = jsonObject.keySet().iterator();
            int value = Integer.valueOf(iterator.next() + "");
            if (signInDays == value) {
                //解锁update
                UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), value, 1, 1, 0);
                userSignInAwardRestDao.update(bean);
            } else if (signInDays < value) {
                //判断连签天数是否为1  重置领取状态
                if (signInDays == 1) {
                    userSignInAwardRestDao.updateAllForReset(userInfoId, CategoryOfBehaviorEnum.SignIn.getName());
                }
                break;
            } else {
                continue;
            }
        }
    }

    /**
     * 补签----->根据连签天数修改对应签到奖励的状态
     *
     * @param userInfoId
     * @param beforeSignInDays 补签前连签天数
     * @param afterSignInDays  补签后连签天数
     * @param list
     */
    private void reSignResetSignInAward(Integer userInfoId, Integer beforeSignInDays, Integer afterSignInDays, List<String> list) {
        //判断是否达标
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject(list.get(i));
            Iterator iterator = jsonObject.keySet().iterator();
            int value = Integer.valueOf(iterator.next() + "");
            //在某个区间内才可以------------->  连签6天  后签2天  补签之后 连签9天  奖励7天解锁
            //                                 连签7天  后签6天  补签之后  连签14天  奖励14天解锁
            if (beforeSignInDays >= value) {
                Integer value2 = null;
                if (i < list.size() - 1) {
                    JSONObject jsonObject1 = JSONObject.parseObject(list.get(i + 1));
                    Iterator iterator1 = jsonObject1.keySet().iterator();
                    value2 = Integer.valueOf(iterator1.next() + "");
                }
                if (value2 != null) {
                    if (beforeSignInDays >= value2) {
                        continue;
                    } else {
                        //判断下一个周期后续天数
                        int signInDays = beforeSignInDays + afterSignInDays;
                        if (signInDays >= value2) {
                            //解锁update
                            UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), value2, 1, 1, 0);
                            userSignInAwardRestDao.update(bean);
                            break;
                        }
                    }
                }
            } else {
                int signInDays = beforeSignInDays + afterSignInDays;
                if (signInDays >= value) {
                    //解锁update
                    UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), value, 1, 1, 0);
                    userSignInAwardRestDao.update(bean);
                    break;
                } else {
                    break;
                }
            }
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

    @Test
    public void run2() {
        LocalDate localDate = LocalDate.now();
        LocalDate localDate1 = localDate.minusDays(6);
        System.out.println(localDate1.toString());
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
        LocalDate localDate = LocalDate.now();
        Map map = redisTemplateUtils.getForHash(PREFIX_SIGN_IN + shareCode);
        if (map.size() < 1) {
            //redis未缓存签到记录  mysql查询
            LocalDate localDate1 = localDate.plusDays(1);
            String signInForFisrtDesc = userSignInRestDao.getSignInForFisrtDesc(userInfoId, localDate1.toString());
            String beforeBySignInDate = userSignInRestDao.getBeforeBySignInDate(userInfoId, localDate1.toString());
            if (StringUtils.isNotEmpty(signInForFisrtDesc) && StringUtils.isNotEmpty(signInForFisrtDesc)) {
                Period period = Period.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate(), LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(beforeBySignInDate)), ZoneId.systemDefault()).toLocalDate());
                map.put("signInDays", period.getDays() + 1);
                map.put("signInDate", beforeBySignInDate);
            } else {
                map.put("signInDays", 0);
                map.put("signInDate", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
            //cache
            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map, RedisPrefix.USER_VALID_TIME);
        }
        //获取到连续签到天数---->获取当前往前递推6天签到情况
        LocalDate first = localDate.minusDays(6);
        List<UserSignInRestEntity> list = userSignInRestDao.getSignInByTime(first.toString(), localDate.toString(), userInfoId);
        int[] result = new int[7];
        //获取补签消耗积分数
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
        for (int i = 0; i < 7; i++) {
            boolean flag = false;
            //从第一天遍历到第七天
            for (UserSignInRestEntity userSignInRestEntity : list) {
                if (StringUtils.equals(first.toString(), DateUtils.convert2String(userSignInRestEntity.getSignInDate()))) {
                    //签到状态
                    result[i] = SignInEnum.HAS_SIGNED.getIndex();
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                //判断当天是否签到
                if (first.equals(localDate)) {
                    //置为未签到
                    result[i] = SignInEnum.NOT_SIGN.getIndex();
                } else {
                    //可补签状态
                    result[i] = SignInEnum.COMPLEMENT_SIGNED.getIndex();
                }
            }
            first = first.plusDays(1);
        }
        JSONObject jsonObject = new JSONObject();
        //当天签到状态
        jsonObject.put("signInStatus", result[result.length - 1]);
        //签到天数
        jsonObject.put("signInDays", map.get("signInDays") == null ? 0 : map.get("signInDays"));
        //七天签到情况
        jsonObject.put("signInWeek", result);
        //读取系统签到奖励规则
        Period period1 = null;
        List<String> cacheSysAward = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        if (cacheSysAward.size() < 1) {
            // 1天奖励  也会包含其中
            List<Map<String, Object>> sysAwardList = fengFengTicketRestDao.getSignInCycle(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription());
            if (sysAwardList.size() > 0) {
                //获取下线时间
                if (sysAwardList.get(0).get("downtime") != null) {
                    String downtime = sysAwardList.get(0).get("downtime") + "";
                    Instant instant = Instant.ofEpochMilli(Long.valueOf(downtime));
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
                    if (ldt.toLocalDate().isAfter(LocalDate.now())) {
                        period1 = Period.between(LocalDate.now(), ldt.toLocalDate());
                    }
                }
            }
            for (Map sysAwardMap : sysAwardList) {
                cacheSysAward.add("{" + sysAwardMap.get("cycle") + ":" + sysAwardMap.get("cycleaware") + "}");
            }
            //设置缓存时间
            if (period1 != null) {
                redisTemplateUtils.setListRight(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, cacheSysAward, 1, Long.valueOf(period1.getDays() + 1));
            } else {
                redisTemplateUtils.setListRight(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, cacheSysAward, 1, RedisPrefix.USER_VALID_TIME);
            }
        }
        //获取自己的签到奖励领取情况 mysql读取 根据系统缓存的key
        //取出有效的周数
        List<String> cycles = new ArrayList<>();
        Map<String, String> cycleAward = new HashMap<>();
        cacheSysAward.forEach(v -> {
            JSONObject jsonObject1 = JSONObject.parseObject(v);
            Iterator iterator = jsonObject1.keySet().iterator();
            String cycle = iterator.next() + "";
            cycles.add(cycle);
            cycleAward.put(cycle, jsonObject1.get(cycle) + "");
        });
        //mysql 查询  分两种情况判断
        List<UserSignInAwardRestDTO> listForSignInAward = userSignInAwardRestDao.listByUserInfoId(Integer.valueOf(userInfoId), cycles);
        if (listForSignInAward != null) {
            if (listForSignInAward.size() != cacheSysAward.size()) {
                //  1种  首次调用
                if (listForSignInAward.size() == 0) {
                    cycles.forEach(v -> {
                        insertUserSignInAward(Integer.valueOf(userInfoId), v, listForSignInAward);
                    });
                } else {
                    //可能出现后台新录入的周期数
                    List<String> cycles2 = new ArrayList<>();
                    listForSignInAward.forEach(v -> {
                        cycles2.add(v.getCycle() + "");
                    });
                    //两个list 取差集
                    cycles.removeAll(cycles2);
                    cycles.forEach(v -> {
                        insertUserSignInAward(Integer.valueOf(userInfoId), v, listForSignInAward);
                    });
                }
            }
        }
        listForSignInAward.forEach(v -> {
            v.setCycleAward(Integer.valueOf(cycleAward.get(v.getCycle() + "")));
        });
        Collections.sort(listForSignInAward, Comparator.comparing(UserSignInAwardRestDTO::getCycle));
        jsonObject.put("signInAward", listForSignInAward);
        //总积分数统计
        double total = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        jsonObject.put("reSignInAware", fengFengTicket == null ? null : fengFengTicket.getBehaviorTicketValue());
        jsonObject.put("totalIntegral", total);
        return jsonObject;
    }

    private void insertUserSignInAward(Integer userInfoId, String v, List<UserSignInAwardRestDTO> listForSignInAward) {
        UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), Integer.valueOf(v), 3, 0, 0);
        userSignInAwardRestDao.insert(bean);
        UserSignInAwardRestDTO bean2 = new UserSignInAwardRestDTO(Integer.valueOf(v), 3, 0, null);
        listForSignInAward.add(bean2);
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
     * 补签 分四种情况判断  每种情况中在判断是否超过最大周数
     *
     * @param userInfoId
     * @param shareCode
     */
    @Override
    public void reSignIn(String userInfoId, String shareCode, LocalDateTime signInDate) {
        //总积分数统计
        double total = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        //获取补签消耗积分数
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
        if (fengFengTicket != null) {
            if (fengFengTicket.getBehaviorTicketValue() != null) {
                if (total + fengFengTicket.getBehaviorTicketValue() >= 0) {
                    //判断补签日期前一天 是否存在签到记录
                    int before = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.minusDays(1).toLocalDate().toString());
                    //判断补签日期后一天 是否存在签到记录
                    int after = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.plusDays(1).toLocalDate().toString());
                    //存在前置连签  不存在后置连签（不存在  有间隔）
                    if (before > 0 && after < 1) {
                        boolean flag;
                        //判断后置连签情况
                        String afterBySignInDate = userSignInRestDao.getAfterBySignInDate(userInfoId, signInDate.toLocalDate().toString());
                        if (StringUtils.isEmpty(afterBySignInDate)) {
                            //不存在后置签到间隔
                            flag = true;
                        } else {
                            //存在  不改变当前的签到天数
                            flag = false;
                        }
                        if (flag) {
                            //更新cache
                            Map map = redisTemplateUtils.getForHash(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                            map.put("signInDate", (signInDate.toEpochSecond(ZoneOffset.of("+8"))) * 1000);
                            Integer signInDays = Integer.valueOf(map.get("signInDays") + "") + 1;
                            map.put("signInDays", signInDays);
                            //读取签到奖励规则
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //判断连签天数
                            if (signInDays > value) {
                                //达到周期上限
                                map.put("signInDays", signInDays % (value + 1) + 1);
                                //重置为新周期
                                userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            } else {
                                //需要追加上次连签记录 ---->直接追加签到记录
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                            }
                            //修改奖励领取状态
                            reSignResetSignInAward(Integer.valueOf(userInfoId), signInDays - 1, 1, list);
                            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map, RedisPrefix.USER_VALID_TIME);
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        } else {
                            //获取指定日期往前最近一次标记的时间
                            String signInForFisrtDesc = userSignInRestDao.getSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                            //获取开始标记日期 localdate
                            LocalDate first = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate();
                            //获取加上到补签日期的 连签天数
                            Period period = Period.between(first, signInDate.toLocalDate());
                            int days = period.getDays();
                            days = days + 1;
                            //读取签到奖励规则
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //判断连签天数
                            if (days > value) {
                                //达到周期上限 重置为新周期  不修改cache
                                userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            } else {
                                //需要追加上次连签记录 ---->直接追加签到记录
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                            }
                            //修改奖励领取状态
                            reSignResetSignInAward(Integer.valueOf(userInfoId), days - 1, 1, list);
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        }
                        //不存在前序连签(不考虑)  存在后续连签(需考虑  后续连签--->有几部分  1改变缓存  2 不能修改缓存)
                    } else if (after > 0 && before < 1) {
                        boolean flag = true;
                        Integer count = userSignInRestDao.getCountForAfterDate(userInfoId, signInDate.toLocalDate().toString());
                        if (count != null) {
                            boolean b = count == 1 ? (flag = true) : (flag = false);
                        }
                        if (flag) {
                            //需要追加下次连签记录
                            userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            //将下一连续签到标识置null
                            userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, signInDate.plusDays(1).toLocalDate().toString());
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                            //更新redis中连续签到天数
                            redisTemplateUtils.incrementForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", 1);
                            int signInDays = redisTemplateUtils.getForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays");
                            //读取签到奖励规则
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            //修改奖励领取状态
                            resetSignInAward(Integer.valueOf(userInfoId), signInDays, list);
                        } else {
                            //不需要修改cache  需要追加下次连签记录
                            userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            //将下一连续签到标识置null
                            userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, signInDate.plusDays(1).toLocalDate().toString());
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                            //后序连签存在多段时 不会触发因为补签解锁新奖励  后期如果补签范围大于7天  需要判断连签天数 todo
                        }
                        //存在前序连签   也存在后续连签(同上  分两种情况判断)
                    } else if (before > 0 && after > 0) {
                        boolean flag = true;
                        Integer count = userSignInRestDao.getCountForAfterDate(userInfoId, signInDate.toLocalDate().toString());
                        if (count != null) {
                            boolean b = count == 1 ? (flag = true) : (flag = false);
                        }
                        if (flag) {
                            //获取最新 周期内开始时间
                            List<UserSignInRestEntity> signInForSecondDesc = userSignInRestDao.getSignInForSecondDesc(userInfoId);
                            //判断前序连签天数
                            LocalDateTime first = LocalDateTime.ofInstant(signInForSecondDesc.get(1).getSignInDate().toInstant(), ZoneId.systemDefault());
                            LocalDateTime endFlag = LocalDateTime.ofInstant(signInForSecondDesc.get(0).getSignInDate().toInstant(), ZoneId.systemDefault());
                            Period period = Period.between(first.toLocalDate(), signInDate.toLocalDate());
                            int days = period.getDays();
                            //判断后续天数
                            //连签天数修改
                            Map map = redisTemplateUtils.getForHash(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                            LocalDate end = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(map.get("signInDate") + "")), ZoneId.systemDefault()).toLocalDate();
                            Period period2 = Period.between(signInDate.toLocalDate(), end);
                            int days2 = period2.getDays();
                            int interval;
                            //判断此刻的连签天数   ---->    校验
                            //读取签到奖励规则
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //判断连签天数
                            if ((days + days2 + 1) > value) {
                                //超过最大周期数情况
                                interval = value - days;
                                //补签
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //清掉原标记
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, endFlag.toString());
                                //重新设置标记
                                LocalDate localDate = signInDate.plusDays(interval).toLocalDate();
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, 1, localDate.toString());
                                Period period3 = Period.between(localDate, end);
                                redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", period3.getDays() + 1);
                                //修改奖励领取状态
                                reSignResetSignInAward(Integer.valueOf(userInfoId), days, days2 + 1, list);
                            } else {
                                //未超过最大周期数  补签
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //清掉原标记
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, endFlag.toString());
                                //连签天数修改
                                redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", days + days2 + 1);
                                //修改奖励领取状态
                                reSignResetSignInAward(Integer.valueOf(userInfoId), days, days2 + 1, list);
                            }
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        } else {
                            //后续连签存在多段 不需要修改cache
                            //获取后序连签天数
                            String signInForAfterEnd = userSignInRestDao.getSignInForAfterEnd(userInfoId, signInDate.toLocalDate().toString());
                            //判断上次连签天数
                            String signInForBefore = userSignInRestDao.getSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                            LocalDate first = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForBefore)), ZoneId.systemDefault()).toLocalDate();
                            //判断后续天数
                            LocalDate end = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForAfterEnd)), ZoneId.systemDefault()).toLocalDate();
                            Period period = Period.between(first, end);
                            int days = period.getDays();
                            int interval;
                            //判断此刻的连签天数   ---->    校验
                            //读取签到奖励规则
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            if ((days + 1) > value) {
                                //超过最大周期数情况
                                Period period1 = Period.between(first, signInDate.toLocalDate());
                                interval = value - period1.getDays();
                                //补签
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //获取补签日期 后序最近标记日期
                                String afterSignInForFisrtDesc = userSignInRestDao.getAfterSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                                //清掉原标记
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(afterSignInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate().toString());
                                //重新设置标记
                                LocalDate localDate = signInDate.plusDays(interval).toLocalDate();
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, 1, localDate.toString());
                                //修改奖励领取状态
                                Period period3 = Period.between(signInDate.toLocalDate(), end);
                                reSignResetSignInAward(Integer.valueOf(userInfoId), period1.getDays(), period3.getDays() + 1, list);
                            } else {
                                //未超过最大周期数
                                //补签
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //获取补签日期 后序最近标记日期
                                String afterSignInForFisrtDesc = userSignInRestDao.getAfterSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                                //清掉原标记
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(afterSignInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate().toString());
                                //修改奖励领取状态
                                Period period2 = Period.between(first, signInDate.toLocalDate());
                                Period period3 = Period.between(signInDate.toLocalDate(), end);
                                reSignResetSignInAward(Integer.valueOf(userInfoId), period2.getDays(), period3.getDays() + 1, list);
                            }
                            //补签积分记录
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        }
                    } else {
                        //不存在连续签到
                        userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                        //补签积分记录
                        if (fengFengTicket.getBehaviorTicketValue() != null) {
                            userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                            userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取连签周期cache
     *
     * @return
     */
    private Integer getMaxCycle(List<String> list) {
        //获取最大周数
        JSONObject jsonObject = JSONObject.parseObject(list.get(list.size() - 1));
        Iterator iterator = jsonObject.keySet().iterator();
        return Integer.valueOf(iterator.next() + "");
    }
}