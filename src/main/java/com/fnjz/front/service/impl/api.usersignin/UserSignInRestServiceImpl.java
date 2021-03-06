package com.fnjz.front.service.impl.api.usersignin;

import com.alibaba.fastjson.JSON;
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
import com.fnjz.front.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static com.fnjz.constants.RedisPrefix.PREFIX_SIGN_IN;

@Service("userSignInRestService")
@Transactional
public class UserSignInRestServiceImpl extends CommonServiceImpl implements UserSignInRestServiceI {

    private static final Logger logger = Logger.getLogger(UserSignInRestServiceImpl.class);

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

    @Autowired
    private RedisLockUtils redisLock;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * ??????
     *
     * @param userInfoId
     */
    @Override
    public ShareWordsRestDTO signIn(String userInfoId, String shareCode) {
        //??????  ----->  ????????????
        redisLock.lock(userInfoId);
        Map map = signInForCache(userInfoId, shareCode);
        FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_1.getIndex());
        //??????????????????????????????
        ShareWordsRestDTO dto = new ShareWordsRestDTO();
        if (ff == null) {
            dto.setSignInAware(-1);
        }else{
            dto.setSignInAware(ff.getBehaviorTicketValue());
        }
        if (map != null) {
            //map???????????????--->??????????????????
            if (map.get("hasSigned") == null) {
                // ????????????????????? ???status??????1
                if (Integer.valueOf(map.get("signInDays") + "") == 1) {
                    userSignInRestDao.signIn(userInfoId, 1);
                } else {
                    userSignInRestDao.signIn(userInfoId, null);
                }
                //??????????????????
                if (ff.getBehaviorTicketValue() != null) {
                    taskExecutor.execute(() -> {
                        userIntegralRestDao.insertSignInIntegral(userInfoId, ff.getId() + "", ff.getBehaviorTicketValue(), AcquisitionModeEnum.SignIn.getDescription(), IntegralEnum.SIGNIN_1.getIndex(), CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(ff.getBehaviorTicketValue() + ""));
                        userIntegralRestDao.updateForTotalIntegral(userInfoId, ff.getBehaviorTicketValue(), new BigDecimal(ff.getBehaviorTicketValue()));
                        //??????
                        createTokenUtils.addIntegralByInvitedUser(userInfoId,ff,CategoryOfBehaviorEnum.TodayTask,AcquisitionModeEnum.BONUS);
                    });
                }
            }
        }
        //??????redis??????  ????????????????????????
        if (map.get("hasSigned") != null) {
           map.remove("hasSigned");
        }
        redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map);
        //?????????
        redisLock.unlock(userInfoId);
        return getShareWords(dto,userInfoId);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private ShareWordsRestDTO getShareWords(ShareWordsRestDTO shareWords,String userInfoId) {
        ShareWordsRestDTO shareWords2 = shareWordsRestDao.getShareWords();
        if(shareWords2!=null){
            BeanUtils.copyProperties(shareWords2, shareWords,new String[]{"signInAware"});
        }
        //???????????? ?????? ????????????
        Map<String, Object> nkAndAUById = userInfoRestDao.getNKAndAUById(Integer.valueOf(userInfoId));
        shareWords.setNickName(nkAndAUById.get("nickname")+"");
        shareWords.setAvatarUrl(nkAndAUById.get("avatarurl")+"");
        shareWords.setRegisterDate((Date)nkAndAUById.get("registerdate"));
        //????????????????????????
        shareWords.setChargeDays(getChargeDays(RedisPrefix.PREFIX_MY_COUNT+ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)),userInfoId));
        //??????????????????????????????
        String inviteQrCode = userWXQrCodeRestServiceI.getInviteQrCode(userInfoId, "2");
        shareWords.setQrCodeUrl(inviteQrCode);
        return shareWords;
    }

    /**
     * ????????????????????????
     */
    private int getChargeDays(String shareCode, String userInfoId) {
        //?????????????????????+1
        Map s = redisTemplateUtils.getMyCount(shareCode);
        if (s.size() > 0) {
            //????????????????????? ???????????????   ??????????????????
            if (s.containsKey("chargeDays")) {
                return Integer.valueOf(s.get("chargeDays") + "");
            } else {
                //????????????????????????
                int totalChargeDays = warterOrderRestDao.getTotalChargeDays(userInfoId);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeDays", totalChargeDays);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
                return totalChargeDays;
            }
        } else {
            //????????????????????????
            int totalChargeDays = warterOrderRestDao.getTotalChargeDays(userInfoId);
            redisTemplateUtils.updateForHashKey(shareCode, "chargeDays", totalChargeDays);
            redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
            return totalChargeDays;
        }
    }

    @Test
    public void run3(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime signInDate = LocalDateTime.ofEpochSecond(1546014714L, 0, ZoneOffset.ofHours(8));
        //???????????????
        LocalDateTime localDateTime = signInDate.plusDays(1);
        //????????????????????????
        LocalDateTime before = localDateTime.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime after = localDateTime.withHour(23).withMinute(59).withSecond(59);
        if (now.isAfter(before) && now.isBefore(after)) {
            System.out.println(1);

        }else if (now.isAfter(after)) {
            System.out.println(2);

        } else {
            System.out.println(3);
        }
    }

    private Map signInForCache(String userInfoId, String shareCode) {
        Map map = redisTemplateUtils.getForHash(PREFIX_SIGN_IN + shareCode);
        //????????????????????????
        List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        //?????????????????? ?????????????????????????????????
        //Date nextDay = DateUtils.getNextDay(new Date(Long.valueOf(map.get("signInDate") + "")));
        //LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).atZone(ZoneOffset.systemDefault()).toEpochSecond();
        LocalDateTime signInDate = LocalDateTime.ofEpochSecond(Long.valueOf(map.get("signInDate")+"") / 1000, 0, ZoneOffset.ofHours(8));
        //???????????????
        LocalDateTime localDateTime = signInDate.plusDays(1);
        //????????????????????????
        LocalDateTime before = localDateTime.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime after = localDateTime.withHour(23).withMinute(59).withSecond(59);
        logger.info("???????????????????????????:"+before.toString()+" "+after.toString());
        //Date dateOfBegin = DateUtils.fetchBeginOfDay(nextDay);
        //Date dateOfEnd = DateUtils.fetchEndOfDay(nextDay);
        //long now = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        logger.info("????????????:"+now.toString());
        if (now.isAfter(before) && now.isBefore(after)) {
            //??????????????????
            int value = getMaxCycle(list);
            logger.info("???????????????:"+value);
            logger.info("??????list:"+ JSON.toJSONString(list));
            if ((Integer.valueOf(map.get("signInDays") + "") + 1) % (value + 1) == 0) {
                logger.info("??????????????????28:"+value);
                //??????????????????
                map.put("signInDays", 1);
            } else {
                logger.info("???????????????1:"+value);
                map.put("signInDays", (Integer.valueOf(map.get("signInDays") + "") + 1));
            }
            map.put("signInDate", System.currentTimeMillis());
            //????????????????????????
            resetSignInAward(Integer.valueOf(userInfoId), Integer.valueOf(map.get("signInDays") + ""), list);
        } else if (now.isAfter(after)) {
            logger.info("???????????????:"+now.toString());
            //??????
            map.put("signInDays", 1);
            map.put("signInDate", System.currentTimeMillis());
        } else {
            //??????????????????
            map.put("hasSigned", true);
        }
        return map;
    }

    /**
     * ???????????????????????????????????????????????????
     * list ??????
     */
    private void resetSignInAward(Integer userInfoId, Integer signInDays, List<String> list) {
        //??????????????????
        for (String v : list) {
            JSONObject jsonObject = JSONObject.parseObject(v);
            Iterator iterator = jsonObject.keySet().iterator();
            int value = Integer.valueOf(iterator.next() + "");
            logger.info("???????????????????????????"+value);
            if (signInDays == value) {
                logger.info("?????????"+value);
                //??????update
                UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), value, 1, 1, 0);
                userSignInAwardRestDao.update(bean);
            } else if (signInDays < value) {
                //???????????????????????????1  ??????????????????
                if (signInDays == 1) {
                    userSignInAwardRestDao.updateAllForReset(userInfoId, CategoryOfBehaviorEnum.SignIn.getName());
                    break;
                }
            } else {
                continue;
            }
        }
    }

    /**
     * ??????----->???????????????????????????????????????????????????
     *
     * @param userInfoId
     * @param beforeSignInDays ?????????????????????
     * @param afterSignInDays  ?????????????????????
     * @param list
     */
    private void reSignResetSignInAward(Integer userInfoId, Integer beforeSignInDays, Integer afterSignInDays, List<String> list) {
        int[] list2 = new int[list.size()];
        for(int i = 0;i<list2.length;i++){
            JSONObject jsonObject = JSONObject.parseObject(list.get(i));
            Iterator iterator = jsonObject.keySet().iterator();
            list2[i]=Integer.valueOf(iterator.next() + "");
        }
        //list2 ?????????
        Arrays.sort(list2);
        //??????????????????
        for (int i = 0; i < list2.length; i++) {
            int value = list2[i];
            //???????????????????????????------------->  ??????6???  ??????2???  ???????????? ??????9???  ??????7?????????
            //                                 ??????7???  ??????6???  ????????????  ??????14???  ??????14?????????
            if (beforeSignInDays >= value) {
                Integer value2 = null;
                if (i < list2.length - 1) {
                    value2 = Integer.valueOf(list2[i + 1]);
                }
                if (value2 != null) {
                    if (beforeSignInDays >= value2) {
                        continue;
                    } else {
                        //?????????????????????????????????
                        int signInDays = beforeSignInDays + afterSignInDays;
                        if (signInDays >= value2) {
                            //??????update
                            UserSignInAwardRestEntity bean = new UserSignInAwardRestEntity(userInfoId, CategoryOfBehaviorEnum.SignIn.getName(), value2, 1, 1, 0);
                            userSignInAwardRestDao.update(bean);
                            break;
                        }
                    }
                }
            } else {
                int signInDays = beforeSignInDays + afterSignInDays;
                if (signInDays >= value) {
                    //??????update
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
     * ???????????????????????????
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
     * ??????????????????
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
            //redis?????????????????????  mysql??????
            LocalDate localDate1 = localDate.plusDays(1);
            String signInForFisrtDesc = userSignInRestDao.getSignInForFisrtDesc(userInfoId, localDate1.toString());
            String beforeBySignInDate = userSignInRestDao.getBeforeBySignInDate(userInfoId, localDate1.toString());
            if (StringUtils.isNotEmpty(signInForFisrtDesc) && StringUtils.isNotEmpty(signInForFisrtDesc)) {
                Period period = Period.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate(), LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(beforeBySignInDate)), ZoneId.systemDefault()).toLocalDate());
                map.put("signInDays", period.getDays() + 1);
                map.put("signInDate", beforeBySignInDate);
            } else {
                map.put("signInDays", 0);
                map.put("signInDate", LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
            //cache
            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map, RedisPrefix.USER_VALID_TIME);
        }
        //???????????????????????????---->????????????????????????6???????????????
        LocalDate first = localDate.minusDays(6);
        List<UserSignInRestEntity> list = userSignInRestDao.getSignInByTime(first.toString(), localDate.toString(), userInfoId);
        int[] result = new int[7];
        //???????????????????????????
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
        for (int i = 0; i < 7; i++) {
            boolean flag = false;
            //??????????????????????????????
            for (UserSignInRestEntity userSignInRestEntity : list) {
                if (StringUtils.equals(first.toString(), DateUtils.convert2String(userSignInRestEntity.getSignInDate()))) {
                    //????????????
                    result[i] = SignInEnum.HAS_SIGNED.getIndex();
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                //????????????????????????
                if (first.equals(localDate)) {
                    //???????????????
                    result[i] = SignInEnum.NOT_SIGN.getIndex();
                } else {
                    //???????????????
                    result[i] = SignInEnum.COMPLEMENT_SIGNED.getIndex();
                }
            }
            first = first.plusDays(1);
        }
        JSONObject jsonObject = new JSONObject();
        //??????????????????
        jsonObject.put("signInStatus", result[result.length - 1]);
        //????????????
        jsonObject.put("signInDays", map.get("signInDays") == null ? 0 : map.get("signInDays"));
        //??????????????????
        jsonObject.put("signInWeek", result);
        //??????????????????????????????
        Period period1 = null;
        List<String> cacheSysAward = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
        if (cacheSysAward.size() < 1) {
            // 1?????????  ??????????????????
            List<Map<String, Object>> sysAwardList = fengFengTicketRestDao.getSignInCycle(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription());
            if (sysAwardList.size() > 0) {
                //??????????????????
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
            //??????????????????
            if (period1 != null) {
                redisTemplateUtils.setListRight(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, cacheSysAward, 1, Long.valueOf(period1.getDays() + 1));
            } else {
                redisTemplateUtils.setListRight(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE, cacheSysAward, 1, RedisPrefix.USER_VALID_TIME);
            }
        }
        //??????????????????????????????????????? mysql?????? ?????????????????????key
        //?????????????????????
        List<String> cycles = new ArrayList<>();
        Map<String, String> cycleAward = new HashMap<>();
        cacheSysAward.forEach(v -> {
            JSONObject jsonObject1 = JSONObject.parseObject(v);
            Iterator iterator = jsonObject1.keySet().iterator();
            String cycle = iterator.next() + "";
            cycles.add(cycle);
            cycleAward.put(cycle, jsonObject1.get(cycle) + "");
        });
        //mysql ??????  ?????????????????????
        List<UserSignInAwardRestDTO> listForSignInAward = userSignInAwardRestDao.listByUserInfoId(Integer.valueOf(userInfoId), cycles);
        if (listForSignInAward != null) {
            if (listForSignInAward.size() != cacheSysAward.size()) {
                //  1???  ????????????
                if (listForSignInAward.size() == 0) {
                    cycles.forEach(v -> {
                        insertUserSignInAward(Integer.valueOf(userInfoId), v, listForSignInAward);
                    });
                } else {
                    //???????????????????????????????????????
                    List<String> cycles2 = new ArrayList<>();
                    listForSignInAward.forEach(v -> {
                        cycles2.add(v.getCycle() + "");
                    });
                    //??????list ?????????
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
        //??????????????????
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
     * ??????????????????????????????
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
     * ?????? ?????????????????????  ????????????????????????????????????????????????
     *
     * @param userInfoId
     * @param shareCode
     */
    @Override
    public void reSignIn(String userInfoId, String shareCode, LocalDateTime signInDate) {
        //??????????????????
        double total = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        //???????????????????????????
        FengFengTicketRestEntity fengFengTicket = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), AcquisitionModeEnum.Check_in.getName(), null);
        if (fengFengTicket != null) {
            if (fengFengTicket.getBehaviorTicketValue() != null) {
                if (total + fengFengTicket.getBehaviorTicketValue() >= 0) {
                    //??????????????????????????? ????????????????????????
                    int before = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.minusDays(1).toLocalDate().toString());
                    //??????????????????????????? ????????????????????????
                    int after = userSignInRestDao.checkSignInForSignInDay(userInfoId, signInDate.plusDays(1).toLocalDate().toString());
                    //??????????????????  ?????????????????????????????????  ????????????
                    if (before > 0 && after < 1) {
                        boolean flag;
                        //????????????????????????
                        String afterBySignInDate = userSignInRestDao.getAfterBySignInDate(userInfoId, signInDate.toLocalDate().toString());
                        if (StringUtils.isEmpty(afterBySignInDate)) {
                            //???????????????????????????
                            flag = true;
                        } else {
                            //??????  ??????????????????????????????
                            flag = false;
                        }
                        if (flag) {
                            //??????cache
                            Map map = redisTemplateUtils.getForHash(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                            map.put("signInDate", (signInDate.toEpochSecond(ZoneOffset.of("+8"))) * 1000);
                            Integer signInDays = Integer.valueOf(map.get("signInDays") + "") + 1;
                            map.put("signInDays", signInDays);
                            //????????????????????????
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //??????????????????
                            if (signInDays > value) {
                                //??????????????????
                                map.put("signInDays", signInDays % (value + 1) + 1);
                                //??????????????????
                                userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            } else {
                                //?????????????????????????????? ---->????????????????????????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                            }
                            //????????????????????????
                            reSignResetSignInAward(Integer.valueOf(userInfoId), signInDays - 1, 1, list);
                            redisTemplateUtils.updateForHash(PREFIX_SIGN_IN + shareCode, map, RedisPrefix.USER_VALID_TIME);
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        } else {
                            //???????????????????????????????????????????????????
                            String signInForFisrtDesc = userSignInRestDao.getSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                            //???????????????????????? localdate
                            LocalDate first = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate();
                            //?????????????????????????????? ????????????
                            Period period = Period.between(first, signInDate.toLocalDate());
                            int days = period.getDays();
                            days = days + 1;
                            //????????????????????????
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //??????????????????
                            if (days > value) {
                                //?????????????????? ??????????????????  ?????????cache
                                userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            } else {
                                //?????????????????????????????? ---->????????????????????????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                            }
                            //????????????????????????
                            reSignResetSignInAward(Integer.valueOf(userInfoId), days - 1, 1, list);
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        }
                        //?????????????????????(?????????)  ??????????????????(?????????  ????????????--->????????????  1????????????  2 ??????????????????)
                    } else if (after > 0 && before < 1) {
                        boolean flag = true;
                        Integer count = userSignInRestDao.getCountForAfterDate(userInfoId, signInDate.toLocalDate().toString());
                        if (count != null) {
                            boolean b = count == 1 ? (flag = true) : (flag = false);
                        }
                        if (flag) {
                            //??????????????????????????????
                            userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            //??????????????????????????????null
                            userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, signInDate.plusDays(1).toLocalDate().toString());
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                            //??????redis?????????????????????
                            redisTemplateUtils.incrementForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", 1);
                            int signInDays = redisTemplateUtils.getForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays");
                            //????????????????????????
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            //????????????????????????
                            resetSignInAward(Integer.valueOf(userInfoId), signInDays, list);
                        } else {
                            //???????????????cache  ??????????????????????????????
                            userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                            //??????????????????????????????null
                            userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, signInDate.plusDays(1).toLocalDate().toString());
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                            //??????????????????????????? ???????????????????????????????????????  ??????????????????????????????7???  ???????????????????????? todo
                        }
                        //??????????????????   ?????????????????????(??????  ?????????????????????)
                    } else if (before > 0 && after > 0) {
                        boolean flag = true;
                        Integer count = userSignInRestDao.getCountForAfterDate(userInfoId, signInDate.toLocalDate().toString());
                        if (count != null) {
                            boolean b = count == 1 ? (flag = true) : (flag = false);
                        }
                        if (flag) {
                            //???????????? ?????????????????????
                            List<UserSignInRestEntity> signInForSecondDesc = userSignInRestDao.getSignInForSecondDesc(userInfoId);
                            //????????????????????????
                            LocalDateTime first = LocalDateTime.ofInstant(signInForSecondDesc.get(1).getSignInDate().toInstant(), ZoneId.systemDefault());
                            LocalDateTime endFlag = LocalDateTime.ofInstant(signInForSecondDesc.get(0).getSignInDate().toInstant(), ZoneId.systemDefault());
                            Period period = Period.between(first.toLocalDate(), signInDate.toLocalDate());
                            int days = period.getDays();
                            //??????????????????
                            //??????????????????
                            Map map = redisTemplateUtils.getForHash(RedisPrefix.PREFIX_SIGN_IN + shareCode);
                            LocalDate end = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(map.get("signInDate") + "")), ZoneId.systemDefault()).toLocalDate();
                            Period period2 = Period.between(signInDate.toLocalDate(), end);
                            int days2 = period2.getDays();
                            int interval;
                            //???????????????????????????   ---->    ??????
                            //????????????????????????
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            //??????????????????
                            if ((days + days2 + 1) > value) {
                                //???????????????????????????
                                interval = value - days;
                                //??????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //???????????????
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, endFlag.toString());
                                //??????????????????
                                LocalDate localDate = signInDate.plusDays(interval).toLocalDate();
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, 1, localDate.toString());
                                Period period3 = Period.between(localDate, end);
                                redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", period3.getDays() + 1);
                                //????????????????????????
                                reSignResetSignInAward(Integer.valueOf(userInfoId), days, days2 + 1, list);
                            } else {
                                //????????????????????????  ??????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //???????????????
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, endFlag.toString());
                                //??????????????????
                                redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays", days + days2 + 1);
                                //????????????????????????
                                reSignResetSignInAward(Integer.valueOf(userInfoId), days, days2 + 1, list);
                            }
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        } else {
                            //???????????????????????? ???????????????cache
                            //????????????????????????
                            String signInForAfterEnd = userSignInRestDao.getSignInForAfterEnd(userInfoId, signInDate.toLocalDate().toString());
                            //????????????????????????
                            String signInForBefore = userSignInRestDao.getSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                            LocalDate first = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForBefore)), ZoneId.systemDefault()).toLocalDate();
                            //??????????????????
                            LocalDate end = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(signInForAfterEnd)), ZoneId.systemDefault()).toLocalDate();
                            Period period = Period.between(first, end);
                            int days = period.getDays();
                            int interval;
                            //???????????????????????????   ---->    ??????
                            //????????????????????????
                            List<String> list = redisTemplateUtils.range(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE);
                            Integer value = getMaxCycle(list);
                            if ((days + 1) > value) {
                                //???????????????????????????
                                Period period1 = Period.between(first, signInDate.toLocalDate());
                                interval = value - period1.getDays();
                                //??????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //?????????????????? ????????????????????????
                                String afterSignInForFisrtDesc = userSignInRestDao.getAfterSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                                //???????????????
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(afterSignInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate().toString());
                                //??????????????????
                                LocalDate localDate = signInDate.plusDays(interval).toLocalDate();
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, 1, localDate.toString());
                                //????????????????????????
                                Period period3 = Period.between(signInDate.toLocalDate(), end);
                                reSignResetSignInAward(Integer.valueOf(userInfoId), period1.getDays(), period3.getDays() + 1, list);
                            } else {
                                //????????????????????????
                                //??????
                                userSignInRestDao.reSignIn(userInfoId, null, signInDate.toLocalDate().toString());
                                //?????????????????? ????????????????????????
                                String afterSignInForFisrtDesc = userSignInRestDao.getAfterSignInForFisrtDesc(userInfoId, signInDate.toLocalDate().toString());
                                //???????????????
                                userSignInRestDao.updateSignInStatusBySignInDate(userInfoId, null, LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(afterSignInForFisrtDesc)), ZoneId.systemDefault()).toLocalDate().toString());
                                //????????????????????????
                                Period period2 = Period.between(first, signInDate.toLocalDate());
                                Period period3 = Period.between(signInDate.toLocalDate(), end);
                                reSignResetSignInAward(Integer.valueOf(userInfoId), period2.getDays(), period3.getDays() + 1, list);
                            }
                            //??????????????????
                            if (fengFengTicket.getBehaviorTicketValue() != null) {
                                userIntegralRestDao.insertSignInIntegral(userInfoId, fengFengTicket.getId() + "", fengFengTicket.getBehaviorTicketValue(), AcquisitionModeEnum.Check_in.getDescription(), null, CategoryOfBehaviorEnum.SignIn.getIndex(), Double.parseDouble(fengFengTicket.getBehaviorTicketValue() + ""));
                                userIntegralRestDao.updateForTotalIntegral(userInfoId, fengFengTicket.getBehaviorTicketValue(), new BigDecimal(fengFengTicket.getBehaviorTicketValue()));
                            }
                        }
                    } else {
                        //?????????????????????
                        userSignInRestDao.reSignIn(userInfoId, 1, signInDate.toLocalDate().toString());
                        //??????????????????
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
     * ??????????????????cache
     *
     * @return
     */
    private Integer getMaxCycle(List<String> list) {
        Integer value = 0;
        for(int i=0;i<list.size();i++){
            //??????????????????
            JSONObject jsonObject = JSONObject.parseObject(list.get(i));
            Iterator iterator = jsonObject.keySet().iterator();
            Integer value1 = Integer.valueOf(iterator.next() + "");
            if(value1>value){
                value=value1;
            }
        }
        return value;
    }

}