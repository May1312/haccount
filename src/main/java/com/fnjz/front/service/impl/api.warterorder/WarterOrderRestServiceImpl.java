package com.fnjz.front.service.impl.api.warterorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.statistics.*;
import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;
import com.fnjz.front.entity.api.userfestivaltags.UserFestivalTagsRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.entity.api.warterorder.WXAppletWarterOrderRestBaseDTO;
import com.fnjz.front.entity.api.warterorder.WXAppletWarterOrderRestInfoDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.integralsactivity.IntegralsActivityService;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@Service("warterOrderRestService")
@Transactional
public class WarterOrderRestServiceImpl extends CommonServiceImpl implements WarterOrderRestServiceI {

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Autowired
    private AccountBookRestDao accountBookRestDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private IntegralsActivityService integralsActivityService;

    @Autowired
    private UserAssetsRestDao userAssetsRestDao;

    @Test
    public void run() {
        String[] args = StringUtils.split("2018-10", "-");
        LocalDate date = LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf("01"));
        LocalDate first = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(first.atTime(0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println(end.atTime(23, 59, 59).toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }

    @Override
    public Map<String, Object> findListForPage(String time, String accountBookId, Integer curPage, Integer pageSize) {
        //time  ???-????????? ??????????????????
        String[] args = StringUtils.split(time, "-");
        LocalDate date = LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf("01"));
        LocalDate first = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        List<WarterOrderRestDTO> listForPage = warterOrderRestDao.findListForPage(first.toString(), end.toString(), accountBookId, null, null);
        //???????????????????????????
        Map<Date, Object> map = new HashMap<>();
        for (Iterator<WarterOrderRestDTO> it = listForPage.iterator(); it.hasNext(); ) {
            WarterOrderRestDTO warter = it.next();
            //????????????????????????
            if (map.containsKey(warter.getChargeDate())) {
                ((ArrayList) map.get(warter.getChargeDate())).add(warter);
            } else {
                List<WarterOrderRestDTO> list = new ArrayList<>();
                list.add(warter);
                map.put(warter.getChargeDate(), list);
            }
        }
        Map<String, Object> ja = new HashMap();
        if (map.size() > 0) {
            Map<Date, Object> resultMap = sortMapByKey(map);
            JSONArray array = new JSONArray();
            JSONArray array2 = new JSONArray();
            for (Map.Entry<Date, Object> entry : resultMap.entrySet()) {
                //?????????key value??????
                JSONObject obj = new JSONObject();
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayTime", entry.getKey());
                }
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayArrays", entry.getValue());
                }
                array.add(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
            }
            //???????????????
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = JSON.parseObject((String) array.get(i));
                if (StringUtils.isNotEmpty(jsonObject.getString("dayArrays"))) {
                    BigDecimal dayIncome = new BigDecimal(0);
                    BigDecimal daySpend = new BigDecimal(0);
                    List list = JSONArray.parseArray(jsonObject.getString("dayArrays"));
                    for (int j = 0; j < list.size(); j++) {
                        //??????
                        WarterOrderRestDTO warter = JSONObject.parseObject(JSONObject.toJSONString(list.get(j)), WarterOrderRestDTO.class);
                        if (warter.getOrderType() == 1) {
                            daySpend = daySpend.add(warter.getMoney());
                        }
                        if (warter.getOrderType() == 2) {
                            dayIncome = dayIncome.add(warter.getMoney());
                        }
                    }
                    jsonObject.put("dayIncome", dayIncome);
                    jsonObject.put("daySpend", daySpend);
                    array2.add(jsonObject);
                    //jsonObject???json
                }
            }
            //????????????????????????
            Map<String, BigDecimal> account = getAccount(time, accountBookId);
            ja.put("arrays", array2);
            ja.put("monthSpend", account.get("spend"));
            ja.put("monthIncome", account.get("income"));
            return ja;
        }
        return ja;
    }

    @Override
    public Map<String, Object> findListForPagev2(String time, String accountBookId, Integer curPage, Integer pageSize, Integer abId, String userInfoId) {
        //time  ???-????????? ??????????????????
        String[] args = StringUtils.split(time, "-");
        LocalDate date = LocalDate.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf("01"));
        LocalDate first = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        PageRest pageRest = new PageRest();
        if (curPage != null) {
            pageRest.setCurPage(curPage);
        }
        if (pageSize != null) {
            pageRest.setPageSize(pageSize);
        }
        List<WXAppletWarterOrderRestBaseDTO> listForPage = new ArrayList<>();
        //???????????????
        Integer count;
        Map<String, Object> ja = new HashMap();
        //????????????????????????
        Map<String, BigDecimal> account2 = new HashMap<>(2);
        if (abId != null) {
            //??????????????????
            listForPage = warterOrderRestDao.findListForPagev2(first.toString(), end.toString(), abId + "", pageRest.getStartIndex(), pageRest.getPageSize());
            //???????????????
            count = warterOrderRestDao.getCount(first.toString(), end.toString(), abId + "");
            //??????????????????????????? 1:?????????????????? 2:????????????
            int status = accountBookRestDao.getABTypeByABId(abId);
            if (status == 2) {
                //2:????????????  ?????????????????????
                Map<String, BigDecimal> account1 = warterOrderRestDao.getAccountv2(abId, 1);
                Map<String, BigDecimal> account3 = warterOrderRestDao.getAccountv2(abId, 2);
                ja.put("sceneSpend", account1.get("spend") == null ? 0 : account1.get("spend"));
                ja.put("monthIncome", account3.get("spend") == null ? 0 : account3.get("spend"));
            } else {
                account2 = warterOrderRestDao.getAccount(first.toString(), end.toString(), abId + "");
                ja.put("monthSpend", account2.get("spend"));
                ja.put("monthIncome", account2.get("income"));
            }
        } else {
            //???????????????
            listForPage = warterOrderRestDao.findListForPagev2All(first.toString(), end.toString(), userInfoId, pageRest.getStartIndex(), pageRest.getPageSize());
            //???????????????
            count = warterOrderRestDao.getCountv2(first.toString(), end.toString(), userInfoId);
            account2 = warterOrderRestDao.getAccountForAll(first.toString(), end.toString(), userInfoId);
            ja.put("monthSpend", account2.get("spend"));
            ja.put("monthIncome", account2.get("income"));
        }
        //??????????????????
        pageRest.setTotalCount(count);
        //???????????????????????????
        Map<Date, Object> map = new HashMap<>();
        for (Iterator<WXAppletWarterOrderRestBaseDTO> it = listForPage.iterator(); it.hasNext(); ) {
            WXAppletWarterOrderRestBaseDTO warter = it.next();
            if (StringUtils.equals(warter.getIsYour() + "", userInfoId)) {
                warter.setIsYour(1);
            } else {
                warter.setIsYour(2);
            }
            //????????????????????????
            if (map.containsKey(warter.getChargeDate())) {
                ((ArrayList) map.get(warter.getChargeDate())).add(warter);
            } else {
                List<WXAppletWarterOrderRestBaseDTO> list = new ArrayList<>();
                list.add(warter);
                map.put(warter.getChargeDate(), list);
            }
        }

        if (map.size() > 0) {
            Map<Date, Object> resultMap = sortMapByKey(map);
            JSONArray array = new JSONArray();
            JSONArray array2 = new JSONArray();
            for (Map.Entry<Date, Object> entry : resultMap.entrySet()) {
                //?????????key value??????
                JSONObject obj = new JSONObject();
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayTime", entry.getKey());
                }
                if (!StringUtils.equals(entry.getKey() + "", "dayTime")) {
                    obj.put("dayArrays", entry.getValue());
                }
                array.add(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
            }
            //???????????????
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = JSON.parseObject((String) array.get(i));
                if (StringUtils.isNotEmpty(jsonObject.getString("dayArrays"))) {
                    BigDecimal dayIncome = new BigDecimal(0);
                    BigDecimal daySpend = new BigDecimal(0);
                    List list = JSONArray.parseArray(jsonObject.getString("dayArrays"));
                    for (int j = 0; j < list.size(); j++) {
                        //??????
                        WarterOrderRestDTO warter = JSONObject.parseObject(JSONObject.toJSONString(list.get(j)), WarterOrderRestDTO.class);
                        if (warter.getOrderType() == 1) {
                            daySpend = daySpend.add(warter.getMoney());
                        }
                        if (warter.getOrderType() == 2) {
                            dayIncome = dayIncome.add(warter.getMoney());
                        }
                    }
                    jsonObject.put("dayIncome", dayIncome);
                    jsonObject.put("daySpend", daySpend);
                    array2.add(jsonObject);
                }
            }
            ja.put("arrays", array2);
            ja.put("totalPage", pageRest.getTotalPage());
            return ja;
        }

        if (pageRest.getTotalPage() == 0) {
            return ja;
        } else {
            ja.put("totalPage", pageRest.getTotalPage());
            return ja;
        }
    }

    public static Map<Date, Object> sortMapByKey(Map<Date, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Date, Object> sortMap = new TreeMap<Date, Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    @Override
    public Integer update(WarterOrderRestNewLabel charge) {
        charge = addLabelInfo(charge);
        WarterOrderRestNewLabel finalCharge = charge;
        //????????????
        taskExecutor.execute(()->{
            updateAssets(finalCharge);
        });
        //????????????????????????
        createTokenUtils.updateABtime(charge.getAccountBookId());
        return warterOrderRestDao.update(charge);
    }

    @Override
    public Integer deleteOrder(String orderId, String userInfoId, String code) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_water_order` SET `delflag` = " + 1 + " , `del_date` = NOW(), `update_date` = NOW(), `update_by` = " + userInfoId + ", `update_name` = '" + code + "' WHERE `id` = '" + orderId + "';");
        //????????????  ????????? ??????
        //???????????????
        WarterOrderRestNewLabel oldWater = warterOrderRestDao.findWaterOrderByIdForMoneyAndUpdateBy(orderId);
        if (oldWater.getOrderType() == 1) {
            //??????
            userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
        } else {
            //??????
            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(oldWater.getMoney()).doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
        }
        return i;
    }

    /**
     * ??????????????????--->????????????
     */
    private void updateAssets(WarterOrderRestNewLabel water) {
        //???????????????
        WXAppletWarterOrderRestInfoDTO oldWater = this.findByIdv2(water.getId(),null);
        water.setAssetsId(oldWater.getAssetsId());
        water.setAssetsName(oldWater.getAssetsName());
        //????????????(????????????10ms???)  ??????   ????????????  ????????????
       // if (abs < 10 && water.getDelflag() == 0) {
            //????????????
//            if (water.getOrderType() == 1) {
//                //??????
//                userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(water.getMoney()).doubleValue()), water.getUpdateBy(), water.getAssetsId());
//            } else {
//                //??????
//                userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
//            }
       // } else if (abs > 10 && water.getDelflag() == 0) {
            //???????????????  ??????????????????  or  ??????????????????
            if (water.getUpdateBy().intValue() == oldWater.getUpdateBy()) {
                //????????????   ??????????????????
                if (water.getOrderType().intValue() == oldWater.getOrderType().intValue()) {
                    //??????????????????
                    if (water.getOrderType().intValue() == 1) {
                        //??????????????????????????????
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            //??????
                            //?????????   ??????-??????
                            BigDecimal subtract = water.getMoney().subtract(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(subtract).doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //??????????????????
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), water.getUpdateBy(), oldWater.getAssetsId());
                        }
                    } else {
                        //??????????????????????????????
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            //?????????
                            BigDecimal subtract = water.getMoney().subtract(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(subtract, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //??????????????????
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    }
                } else {
                    //??????????????????  ???????????????  ???????????????
                    if (water.getOrderType().intValue() == 1) {
                        //??????????????????????????????
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            BigDecimal add = (new BigDecimal(-+(water.getMoney()).doubleValue())).add(new BigDecimal(-+(oldWater.getMoney()).doubleValue()));
                            userAssetsRestDao.updateMoneyv3(add, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    } else {
                        //???????????????   ???????????????
                        //??????????????????????????????
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            BigDecimal add = water.getMoney().add(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(add, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                            //??????----?????????
                            userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    }
                }
            } else {
                //??????????????????
                if (water.getOrderType().intValue() == oldWater.getOrderType().intValue()) {
                    //?????????   ??????????????????
                    //??????????????????
                    if (water.getOrderType().intValue() == 1) {
                        //??????--->??????
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        //??????--->?????? ??????
                        userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    } else {
                        //??????----?????????
                        userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                        //??????----?????????
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    }
                } else {
                    //??????????????????  ???????????????  ???????????????
                    if (water.getOrderType().intValue() == 1) {
                        //??????--->??????
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        //??????--->?????? ??????
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    } else {
                        //???????????????   ???????????????
                        //??????--->??????
                        userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                        //??????--->?????? ??????
                        userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    }
                }
            }
        /*} else if (water.getDelflag() == 1) {
            if (water.getAssetsId() != 0) {
                //????????????  ????????? ??????
                if (water.getOrderType() == 1) {
                    //??????
                    userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                } else {
                    //??????
                    userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(water.getMoney()).doubleValue()), water.getUpdateBy(), water.getAssetsId());
                }
            }
        }*/
    }

    @Override
    public Map<String, BigDecimal> getAccount(String time, String accountBookId) {
        List<Map<String, BigDecimal>> listbySql = commonDao.findListMapbySql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END) AS spend,SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END) AS income FROM `hbird_water_order` WHERE charge_date LIKE '" + time + "%' AND account_book_id = '" + accountBookId + "' AND delflag = 0;");
        return (Map) listbySql.get(0);
    }

    @Override
    public WarterOrderRestDTO findById(String id) {
        return warterOrderRestDao.findById(id);
    }

    @Override
    public WXAppletWarterOrderRestInfoDTO findByIdv2(String id, Integer memberFlag) {
        if (memberFlag == null) {
            //?????? 1??????  2????????????  ???null??????????????????
            memberFlag = 2;
        }
        if (memberFlag == 1) {
            return warterOrderRestDao.findByIdv2(id);
        } else {
            return warterOrderRestDao.findByIdv2NoAvatar(id);
        }
    }

    @Override
    public int countChargeDays(String currentYearMonth, Integer accountBookId) {
        List<Map<String, String>> maps = warterOrderRestDao.countChargeDays(currentYearMonth, accountBookId);
        return maps.size();
    }

    @Override
    public int countChargeDaysByChargeDays(String currentYearMonth, Integer accountBookId) {
        List<Map<String, String>> maps = warterOrderRestDao.countChargeDaysByChargeDays(currentYearMonth, accountBookId);
        return maps.size();
    }

    @Override
    public int chargeTotal(Integer accountBookId) {
        return warterOrderRestDao.chargeTotal(accountBookId);
    }

    @Override
    public void insert(WarterOrderRestNewLabel charge, String code, Integer accountBookId) {
        //?????????????????? ???????????????????????????
        createTokenUtils.integralTask(charge.getCreateBy() + "", ShareCodeUtil.id2sharecode(Integer.valueOf(charge.getCreateBy())), CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Write_down_an_account);
        commonDao.save(charge);
    }

    @Autowired
    private HibernateTransactionManager transactionManager;

    @Override
    public FestivalTagsRestEntity insertv2(WarterOrderRestNewLabel charge) {
        charge = addLabelInfo(charge);

        //??????????????????
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // ??????????????????????????????????????????????????????????????????
        TransactionStatus status = transactionManager.getTransaction(def); // ??????????????????
        try {
            warterOrderRestDao.insert(charge);
            //??????????????????  ---->?????????????????????????????????????????????
            transactionManager.commit(status);
        } catch (TransactionException e) {
            //??????
            e.printStackTrace();
            transactionManager.rollback(status);
        }
        String userInfoId = charge.getCreateBy() + "";
        String sharecode = ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId));
        Integer accountBookId = charge.getAccountBookId();
        taskExecutor.execute(() -> {
            //????????????????????????
            createTokenUtils.updateABtime(accountBookId);
            //?????????????????? ---->????????????
            createTokenUtils.integralTask(userInfoId, null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Write_down_an_account);
            //?????????????????? ---->?????????3???
            createTokenUtils.integralTask(userInfoId, null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.The_bookkeeping_came_to_three);
            //????????????
            myCount(sharecode, userInfoId);
            //?????????????????????
            integralsActivityService.chargeToIntegralsActivity(userInfoId);
        });
        return unlockTags(userInfoId);
    }

    /**
     * ??????????????????
     */
    private static LocalDateTime springBegin = LocalDateTime.of(2019,1,28,0,0,0);
    private static LocalDateTime springEnd = LocalDateTime.of(2019,2,11,23,59,59);

    /**
     * ?????????????????????
     */
    private static LocalDateTime dogBegin = LocalDateTime.of(2019,2,12,0,0,0);
    private static LocalDateTime dogEnd = LocalDateTime.of(2019,2,14,23,59,59);
    @Autowired
    private UserIntegralRestDao userIntegralRestDao;
    @Autowired
    private UserFestivalTagsRestDao userFestivalTagsRestDao;
    /**
     * 1-28   2-11   2-12------2-14
     * ??????/???????????????????????????  ?????? ????????????????????????????????????
     * @return
     */
    private FestivalTagsRestEntity unlockTags(String userInfoId){
        LocalDateTime now = LocalDateTime.now();
        //??????????????????
        if(now.isAfter(springBegin)&&now.isBefore(springEnd)){
            //????????????  ??????+?????? ???16
           return unlockTags2(userInfoId,1,now);
        }else if(now.isAfter(dogBegin)&&now.isBefore(dogEnd)){
            //???????????????  ??????+?????? ???8
            return unlockTags2(userInfoId,2,now);
        }
        return null;
    }

    private FestivalTagsRestEntity unlockTags2(String userInfoId, int festivalType,LocalDateTime now){
        //??????????????????????????????????????????
        int count = userIntegralRestDao.checkTaskCompleteByTime(CategoryOfBehaviorEnum.TodayTask.getIndex(), AcquisitionModeEnum.Write_down_an_account.getIndex(), userInfoId,now.toLocalDate().toString());
        if (count > 0) {
            //?????????
            return null;
        } else {
            //?????????
            //????????????????????????????????????
            UserFestivalTagsRestEntity entity = userFestivalTagsRestDao.getLatest(userInfoId);
            if(entity!=null){
                if(entity.getId()!=null){
                    //??????id???  ????????????(???????????????4?????????????????????4?????????)  ??????+?????? ???16   ???????????????  ??????+?????? ???8
                    if(festivalType==1){
                        if(entity.getTagsId()<16){
                            int id = entity.getTagsId()+ 1;
                            userFestivalTagsRestDao.insert(userInfoId,id);
                            return userFestivalTagsRestDao.getTagsById(id);
                        }else{
                            return null;
                        }
                    }else{
                        if(entity.getTagsId()<=16){
                            //?????? ???????????????
                            userFestivalTagsRestDao.insert(userInfoId,21);
                            return userFestivalTagsRestDao.getTagsById(21);
                        }else if(entity.getTagsId()>=21&&entity.getTagsId()<24){
                            int id = entity.getTagsId()+ 1;
                            userFestivalTagsRestDao.insert(userInfoId,id);
                            return userFestivalTagsRestDao.getTagsById(id);
                        }else {
                            return null;
                        }
                    }
                }
            }
            //?????? ????????????
            userFestivalTagsRestDao.insert(userInfoId,9);
            return userFestivalTagsRestDao.getTagsById(9);
        }
    }

    /**
     * ?????????????????? ??????????????????
     */
    private void myCount(String shareCode, String userInfoId) {
        //?????????????????????+1
        Map s = redisTemplateUtils.getMyCount(shareCode);
        if (s.size() > 0) {
            if (s.containsKey("chargeTotal")) {
                //????????????
                redisTemplateUtils.incrementMyCountTotal(shareCode, "chargeTotal", 1);
            }
            //????????????????????? ???????????????   ??????????????????
            if (s.containsKey("chargeDays")) {
                if (s.containsKey("chargeTime")) {
                    String chargeTime = s.get("chargeTime") + "";
                    LocalDateTime signInDate = LocalDateTime.ofEpochSecond(Long.valueOf(chargeTime), 0, ZoneOffset.ofHours(8));
                    //??????????????????
                    LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
                    LocalDateTime begin = LocalDate.now().atTime(0, 0, 0);
                    if (!(signInDate.isAfter(begin) && begin.isBefore(end))) {
                        redisTemplateUtils.incrementMyCountTotal(shareCode, "chargeDays", 1);
                        redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
                    }
                }
            } else {
                //????????????????????????
                int totalChargeDays = warterOrderRestDao.getTotalChargeDays(userInfoId);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeDays", totalChargeDays);
                redisTemplateUtils.updateForHashKey(shareCode, "chargeTime", System.currentTimeMillis());
            }
        }
    }

    //??????????????????
    private WarterOrderRestNewLabel addLabelInfo(WarterOrderRestNewLabel charge) {
        //??????????????????
        if (charge.getUserPrivateLabelId() != null) {
            //??????????????????
            UserPrivateLabelRestEntity userPrivateLabelRestEntity = userPrivateLabelRestDao.selectInfoByLabelId(charge.getUserPrivateLabelId());
            if (userPrivateLabelRestEntity != null) {
                charge.setTypePid(userPrivateLabelRestEntity.getTypePid());
                charge.setTypePname(userPrivateLabelRestEntity.getTypePname());
                charge.setTypeId(userPrivateLabelRestEntity.getTypeId());
                charge.setTypeName(userPrivateLabelRestEntity.getTypeName());
                charge.setIcon(userPrivateLabelRestEntity.getIcon());
            }
        }
        return charge;
    }

    @Override
    public Map<String, Object> statisticsForDays(Date beginTime, Date endTime, Integer accountBookId, int orderType) {
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForDays(beginTime, endTime, accountBookId, orderType);
        //????????????--->???????????????
        String max = warterOrderRestDao.findMaxDayMoneyOfYear(accountBookId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    @Override
    public Map<String, Object> statisticsForWeeks(String beginWeek, String endWeek, Integer accountBookId, int orderType) {
        //???????????????
        List<StatisticsWeeksRestDTO> list = warterOrderRestDao.statisticsForWeeks(beginWeek, endWeek, accountBookId, orderType);
        //????????????--->???????????????
        String max = warterOrderRestDao.findMaxWeekMoneyOfYear(accountBookId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    @Override
    public Map<String, Object> statisticsForMonths(Integer accountBookId, int orderType) {
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForMonths(accountBookId, orderType);
        //????????????--->???????????????
        String max = warterOrderRestDao.findMaxMonthMoneyOfYear(accountBookId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappiness(Date time, Integer accountBookId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTime(date, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForDaysTop(Date time, Integer accountBookId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTimeOfIncome(date, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappiness(String time, Integer accountBookId) {
        Map<String, String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        String beginTime = map.get("beginTime");
        String endTime = map.get("endTime");
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTime(beginTime, endTime, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForWeeksTop(String time, Integer accountBookId) {
        Map<String, String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        String beginTime = map.get("beginTime");
        String endTime = map.get("endTime");
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTimeOfIncome(beginTime, endTime, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappiness(String time, Integer accountBookId) {
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTime(time, accountBookId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForMonthsTop(String time, Integer accountBookId) {
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTimeOfIncome(time, accountBookId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    public StatisticsSpendTopAndHappinessDTO statisticsForAllTopAndHappiness(List<Map<String, Object>> list) {
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = new StatisticsSpendTopAndHappinessDTO();
        //?????????
        BigDecimal trueTotalMoney = new BigDecimal(0);
        BigDecimal falseTotalMoney = new BigDecimal(0);
        //???????????????
        Integer totalCount = 0;
        //??????????????????
        List<StatisticsTopDTO> top = new ArrayList<>();
        //???????????????
        List<StatisticsSpendHappinessDTO> happiness = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //??????????????????map
            Map<String, StatisticsSpendHappinessDTO> map = new HashMap<>();
            //???????????????
            Map<String, StatisticsTopDTO> mapTop = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                //???????????????
                BigDecimal bd = new BigDecimal(list.get(i).get("money") + "");
                trueTotalMoney = trueTotalMoney.add(bd);
                BigDecimal falseMoney = new BigDecimal(list.get(i).get("money") + "");
                BigDecimal abs = falseMoney.abs();
                falseTotalMoney = falseTotalMoney.add(abs);
                //??????????????????????????????
                StatisticsTopDTO statisticsSpendTopDTO = new StatisticsTopDTO();
                //????????????
                statisticsSpendTopDTO.setMoney((BigDecimal) list.get(i).get("money"));
                //??????????????????
                statisticsSpendTopDTO.setTypeName(list.get(i).get("type_name") + "");
                //????????????
                statisticsSpendTopDTO.setIcon(list.get(i).get("icon") + "");
                if (mapTop.containsKey(list.get(i).get("type_name") + "")) {
                    //?????? ????????????
                    BigDecimal money = mapTop.get(list.get(i).get("type_name") + "").getMoney().add(statisticsSpendTopDTO.getMoney());
                    statisticsSpendTopDTO.setMoney(money);
                    mapTop.put(list.get(i).get("type_name") + "", statisticsSpendTopDTO);
                } else {
                    mapTop.put(list.get(i).get("type_name") + "", statisticsSpendTopDTO);
                }
                //??????????????? moneytimes-->??????????????????????????????  count--->????????????
                totalCount += Integer.valueOf(list.get(i).get("count") + "");
                //??????????????????????????????
                if ((list.get(i).get("spend_happiness")) != null) {
                    //map??????
                    if (map.containsKey(list.get(i).get("spend_happiness") + "")) {
                        StatisticsSpendHappinessDTO statisticsSpendHappinessDTO = map.get(list.get(i).get("spend_happiness") + "");
                        statisticsSpendHappinessDTO.setCount(statisticsSpendHappinessDTO.getCount() + Integer.valueOf(list.get(i).get("count") + ""));
                        map.put(list.get(i).get("spend_happiness") + "", statisticsSpendHappinessDTO);
                    } else {
                        StatisticsSpendHappinessDTO statisticsSpendHappinessDTO = new StatisticsSpendHappinessDTO();
                        statisticsSpendHappinessDTO.setCount(Integer.valueOf(list.get(i).get("count") + ""));
                        //???????????????
                        statisticsSpendHappinessDTO.setSpendHappiness(Integer.valueOf(list.get(i).get("spend_happiness") + ""));
                        map.put(list.get(i).get("spend_happiness") + "", statisticsSpendHappinessDTO);
                    }
                }
            }
            for (Map.Entry<String, StatisticsSpendHappinessDTO> entry : map.entrySet()) {
                happiness.add(entry.getValue());
            }
            for (Map.Entry<String, StatisticsTopDTO> entry : mapTop.entrySet()) {
                top.add(entry.getValue());
            }
            //????????????????????????
            //??????
            Collections.sort(happiness, Comparator.comparing(StatisticsSpendHappinessDTO::getCount).reversed());
            //?????????????????????
            Collections.sort(top, Comparator.comparing(StatisticsTopDTO::getMoney).reversed());
            //top ????????????
            List<StatisticsTopDTO> returnTop = new ArrayList<>();
            for (int i = 0; i < top.size() && top.size() > 5; i++) {
                if (i >= 5) {
                    break;
                }
                returnTop.add(top.get(i));
            }
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendHappinessArrays(happiness);
            if (returnTop.size() > 0) {
                statisticsSpendTopAndHappinessDTO.setStatisticsSpendTopArrays(returnTop);
            } else {
                statisticsSpendTopAndHappinessDTO.setStatisticsSpendTopArrays(top);
            }
            statisticsSpendTopAndHappinessDTO.setTotalCount(totalCount);
            statisticsSpendTopAndHappinessDTO.setTrueTotalMoney(trueTotalMoney);
            statisticsSpendTopAndHappinessDTO.setFalseTotalMoney(falseTotalMoney);
        } else {
            //?????????????????????
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendHappinessArrays(happiness);
            statisticsSpendTopAndHappinessDTO.setStatisticsSpendTopArrays(top);
            statisticsSpendTopAndHappinessDTO.setTotalCount(totalCount);
            statisticsSpendTopAndHappinessDTO.setTrueTotalMoney(trueTotalMoney);
            statisticsSpendTopAndHappinessDTO.setFalseTotalMoney(falseTotalMoney);
        }
        return statisticsSpendTopAndHappinessDTO;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param list
     * @return
     */
    public StatisticsIncomeTopDTO statisticsForAllTop(List<Map<String, Object>> list) {
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = new StatisticsIncomeTopDTO();
        //?????????
        BigDecimal trueTotalMoney = new BigDecimal(0);
        BigDecimal falseTotalMoney = new BigDecimal(0);
        //??????????????????
        List<StatisticsTopDTO> top = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //???????????????
                BigDecimal bd = new BigDecimal(list.get(i).get("money") + "");
                trueTotalMoney = trueTotalMoney.add(bd);
                BigDecimal falseMoney = new BigDecimal(Double.valueOf(list.get(i).get("money") + ""));
                BigDecimal abs = falseMoney.abs();
                falseTotalMoney = falseTotalMoney.add(abs);
                //???????????????5
                if (i < 5) {
                    //??????????????????????????????
                    StatisticsTopDTO statisticsTopDTO = new StatisticsTopDTO();
                    //????????????
                    statisticsTopDTO.setMoney((BigDecimal) list.get(i).get("money"));
                    //??????????????????
                    statisticsTopDTO.setTypeName(list.get(i).get("type_name") + "");
                    //????????????
                    statisticsTopDTO.setIcon(list.get(i).get("icon") + "");
                    //????????????????????????
                    top.add(statisticsTopDTO);
                }
            }
            statisticsIncomeTopDTO.setStatisticsIncomeTopArrays(top);
            statisticsIncomeTopDTO.setTrueTotalMoney(trueTotalMoney);
            statisticsIncomeTopDTO.setFalseTotalMoney(falseTotalMoney);
        } else {
            //?????????????????????
            statisticsIncomeTopDTO.setStatisticsIncomeTopArrays(top);
            statisticsIncomeTopDTO.setTrueTotalMoney(trueTotalMoney);
            statisticsIncomeTopDTO.setFalseTotalMoney(falseTotalMoney);
        }
        return statisticsIncomeTopDTO;
    }

    @Test
    public void run2() {
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        System.out.println(localDate.toString());
    }

    /**
     * v2 ?????????
     *
     * @param beginTime
     * @param endTime
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> statisticsForDaysv2(Date beginTime, Date endTime, String userInfoId, int orderType) {
        //???????????????????????????????????????
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForDaysv2(beginTime, endTime, userInfoId, orderType);
        //??????????????????????????????
        String max = warterOrderRestDao.findMaxDayMoneyOfYearv2(beginTime, endTime, userInfoId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    /**
     * v2 ?????????
     *
     * @param beginWeek
     * @param endWeek
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> statisticsForWeeksv2(String beginWeek, String endWeek, String userInfoId, int orderType) {
        //???????????????
        List<StatisticsWeeksRestDTO> list = warterOrderRestDao.statisticsForWeeksv2(beginWeek, endWeek, userInfoId, orderType);
        //????????????--->???????????????  ????????????
        String max = warterOrderRestDao.findMaxWeekMoneyOfYearv2(beginWeek, endWeek, userInfoId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    /**
     * v2 ?????????
     *
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> statisticsForMonthsv2(String userInfoId, int orderType) {
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        List<StatisticsDaysRestDTO> list = warterOrderRestDao.statisticsForMonthsv2(localDate.toString(), LocalDate.now().toString(), userInfoId, orderType);
        //????????????--->???????????????
        String max = warterOrderRestDao.findMaxMonthMoneyOfYearv2(localDate.toString(), LocalDate.now().toString(), userInfoId, orderType);
        BigDecimal maxValue;
        if (StringUtils.isNotEmpty(max)) {
            maxValue = new BigDecimal(max);
        } else {
            maxValue = new BigDecimal(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("maxMoney", maxValue);
        map.put("arrays", list);
        return map;
    }

    /**
     * v2 ???????????????
     *
     * @param time
     * @param userInfoId
     * @return
     */
    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForDaysTopAndHappinessv2(Date time, String userInfoId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTimev2(date, userInfoId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    /**
     * v2 ???????????????
     *
     * @param time
     * @param userInfoId
     * @return
     */
    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForWeeksTopAndHappinessv2(String time, String userInfoId) {
        Map<String, String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTimev2(map.get("beginTime"), map.get("endTime"), userInfoId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    /**
     * v2 ???????????????
     *
     * @param time
     * @param userInfoId
     * @return
     */
    @Override
    public StatisticsSpendTopAndHappinessDTO statisticsForMonthsTopAndHappinessv2(String time, String userInfoId) {
        LocalDate now = LocalDate.now();
        //????????????  ????????????
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTimev2(LocalDate.of(now.getYear(), Integer.valueOf(time), 1).toString(), now.with(TemporalAdjusters.lastDayOfMonth()).toString(), userInfoId);
        StatisticsSpendTopAndHappinessDTO statisticsSpendTopAndHappinessDTO = statisticsForAllTopAndHappiness(list);
        return statisticsSpendTopAndHappinessDTO;
    }

    /**
     * v2 ????????? ???????????????
     *
     * @param time
     * @param userInfoId
     * @return
     */
    @Override
    public StatisticsIncomeTopDTO statisticsForDaysTopv2(Date time, String userInfoId) {
        String date = DateUtils.convert2String(time);
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForDaysByTimeOfIncomev2(date, userInfoId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForWeeksTopv2(String time, String userInfoId) {
        Map<String, String> map = DateUtils.getDateByWeeks(Integer.valueOf(time));
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForWeeksByTimeOfIncomev2(map.get("beginTime"), map.get("endTime"), userInfoId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public StatisticsIncomeTopDTO statisticsForMonthsTopv2(String time, String userInfoId) {
        LocalDate now = LocalDate.now();
        //????????????  ????????????
        List<Map<String, Object>> list = warterOrderRestDao.statisticsForMonthsByTimeOfIncomev2(LocalDate.of(now.getYear(), Integer.valueOf(time), 1).toString(), now.with(TemporalAdjusters.lastDayOfMonth()).toString(), userInfoId);
        StatisticsIncomeTopDTO statisticsIncomeTopDTO = statisticsForAllTop(list);
        return statisticsIncomeTopDTO;
    }

    @Override
    public int countChargeDaysv2(String userInfoId) {
        //????????????
        LocalDate localDate1 = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        //??????????????????
        LocalDate localDate2 = localDate1.with(TemporalAdjusters.lastDayOfMonth());
        return warterOrderRestDao.countChargeDaysByChargeDaysv2(localDate1.toString(), localDate2.toString(), userInfoId);
    }

    @Override
    public int chargeTotalv2(String userInfoId) {
        return warterOrderRestDao.chargeTotalv2(userInfoId);
    }
}

class MapKeyComparator implements Comparator<Date> {

    @Override
    public int compare(Date str1, Date str2) {
        return str2.compareTo(str1);
    }
}