package com.fnjz.front.service.impl.api.offlineSynchronized;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.offlineSynchronized.SynDateRestDTO;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.entity.api.warterorder.APPWarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.integralsactivity.IntegralsActivityService;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service("offlinesynchronizedRestService")
@Transactional
public class OfflineSynchronizedRestServiceImpl extends CommonServiceImpl implements OfflineSynchronizedRestServiceI {

    @Autowired
    private OfflineSynchronizedRestDao offlineSynchronizedRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;

    @Autowired
    private IntegralsActivityService integralsActivityService;

    @Autowired
    private UserAssetsRestDao userAssetsRestDao;

    @Autowired
    private UserBadgeRestService userBadgeRestService;

    /**
     * 获取最新同步时间
     *
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    @Override
    public Date getLatelySynDate(String mobileDevice, String userInfoId) {
        SynDateRestDTO latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        return latelySynDate.getSynDate();
    }

    /**
     * 移动端pull接口
     *
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> offlinePull(String mobileDevice, String isFirst, String userInfoId) {
        SynDateRestDTO latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        //第一次同步  为null情况下 获取当前时间戳为同步时间
        Date date = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        if (latelySynDate.getSynDate() == null) {
            offlineSynchronizedRestDao.firstInsert(mobileDevice, userInfoId, date);
            map.put("synDate", date);
        } else {
            map.put("synDate", latelySynDate.getSynDate());
        }
        List<WarterOrderRestEntity> list;
        //判断 isFirst标识是否为true, true 获取所有
        if (Boolean.valueOf(isFirst)) {
            list = warterOrderRestDao.findAllWaterListOfNoDel(userInfoId, null);
        } else {
            list = warterOrderRestDao.findAllWaterList(userInfoId, latelySynDate.getSynDate());
        }
        map.put("synData", list);
        return map;
    }

    /**
     * 多账本同步接口
     *
     * @param mobileDevice
     * @param isFirst
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String, Object> offlinePullV2(String mobileDevice, String isFirst, String userInfoId) {
        SynDateRestDTO latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        //第一次同步  为null情况下 获取当前时间戳为同步时间
        Date date = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
<<<<<<< HEAD
        if (latelySynDate == null) {
            latelySynDate = new SynDateRestDTO();
            offlineSynchronizedRestDao.firstInsert(mobileDevice, userInfoId, date);
            latelySynDate.setSynDate(date);
            map.put("synDate", date);
        }
        if (latelySynDate != null && latelySynDate.getSynDate() == null) {
            offlineSynchronizedRestDao.firstInsert(mobileDevice, userInfoId, date);
            latelySynDate.setSynDate(date);
=======
        if(latelySynDate==null){
            latelySynDate = new SynDateRestDTO();
            offlineSynchronizedRestDao.firstInsert(mobileDevice, userInfoId, date);
            latelySynDate.setSynDate(date);
            map.put("synDate", date);
        }
        if (latelySynDate!=null&&latelySynDate.getSynDate() == null) {
            offlineSynchronizedRestDao.firstInsert(mobileDevice, userInfoId, date);
            latelySynDate.setSynDate(date);
>>>>>>> 4dc0f96d... 离线同步时间修改
            map.put("synDate", date);
        } else {
            map.put("synDate", latelySynDate.getSynDate());
        }
        List<APPWarterOrderRestDTO> list;
        //判断 isFirst标识是否为true, true 获取所有
        if (Boolean.valueOf(isFirst)) {
            list = warterOrderRestDao.findAllWaterListOfNoDelV2(userInfoId, null);
        } else {
            //判断当前用户是否加入其他账本并获取待同步记录
            Integer maps = userAccountBookRestDao.checkBindABFlag(userInfoId);
            List<APPWarterOrderRestDTO> list2 = new ArrayList<>();
            if (maps > 0) {
                list2 = userAccountBookRestDao.checkBindABFlagAndReturn(userInfoId);
                //更新记录状态
                userAccountBookRestDao.updateBindABFlag(userInfoId);
            }
            list = warterOrderRestDao.findAllWaterListV2(userInfoId, latelySynDate.getSynDate());
            if (list2 != null) {
                if (list2.size() > 0) {
                    list.addAll(list2);
                }
            }
        }
        map.put("synData", list);
        return map;
    }

    /**
     * 移动端push 插入记录
     *
     * @param list
     * @param mobileDevice
     * @param userInfoId
     */
    @Override
    public void offlinePush(List<WarterOrderRestNewLabel> list, String mobileDevice, String userInfoId, String clientId) {
        //生成本次同步记录
        offlineSynchronizedRestDao.insert(mobileDevice, userInfoId);
        if (list != null) {
            if (list.size() > 0) {
                String shareCode = ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId));
                //list排序  正序
                list.sort(Comparator.naturalOrder());
                for (WarterOrderRestNewLabel warter : list) {
                    warter = addLabelInfo(warter);
                    warter.setClientId(clientId);
                    //修改资产
                    updateAssets(warter);
                    //同步数据
                    warterOrderRestDao.saveOrUpdateOfflineData(warter);
                    //徽章解锁
                    long abs = Math.abs(warter.getCreateDate().getTime() - warter.getUpdateDate().getTime());
                    if (abs < 10 && warter.getDelflag() == 0) {
                        userBadgeRestService.unlockBadge(warter);
                    }
                }
                //修改账本更新时间
                createTokenUtils.updateABtime(list.get(0).getAccountBookId());
                //记账总笔数置
                int chargeTotal = warterOrderRestServiceI.chargeTotalv2(userInfoId);
                redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_MY_COUNT + shareCode, "chargeTotal", chargeTotal);
                if (list.get(list.size() - 1).getCreateDate() != null) {
                    if (LocalDateTime.ofInstant(list.get(list.size() - 1).getCreateDate().toInstant(), ZoneId.systemDefault()).toLocalDate().isEqual(LocalDate.now())) {
                        //引入当日任务
                        createTokenUtils.integralTask(userInfoId, null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Write_down_an_account);
                        //引入当日任务 ---->记账达3笔
                        createTokenUtils.integralTask(userInfoId, null, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.The_bookkeeping_came_to_three);
                        //记账挑战赛任务
                        integralsActivityService.chargeToIntegralsActivity(userInfoId);
                    }
                }
            }
        }
    }

    @Test
    public void run2() {
        System.out.println(Math.abs((new Date()).getTime() - (new Date()).getTime()) < 10);
    }

    /**
     * 修改资产
     */
    private void updateAssets(WarterOrderRestNewLabel water) {
        //判断新增(差值小于10ms内)  删除   更新情况  分别处理
        long abs = Math.abs(water.getCreateDate().getTime() - water.getUpdateDate().getTime());
        if (abs < 10 && water.getDelflag() == 0) {
            //新增情况
            if (water.getOrderType() == 1) {
                //支出
                userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(water.getMoney()).doubleValue()), water.getUpdateBy(), water.getAssetsId());
            } else {
                //收入
                userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
            }
        } else if (abs > 10 && water.getDelflag() == 0) {
            //更新情况  获取原纪录
            WarterOrderRestNewLabel oldWater = warterOrderRestDao.findWaterOrderByIdForMoneyAndUpdateBy(water.getId());
            //第一层判断  更新自己数据  or  更新他人数据
            if (water.getUpdateBy().intValue() == oldWater.getUpdateBy()) {
                //自有数据   判断订单类型
                if (water.getOrderType().intValue() == oldWater.getOrderType().intValue()) {
                    //判断订单类型
                    if (water.getOrderType().intValue() == 1) {
                        //判断账户类型是否修改
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            //支出
                            //同类型   新值-旧值
                            BigDecimal subtract = water.getMoney().subtract(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(subtract).doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //账户类型修改
                            //支出----新纪录
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                            //支出----旧纪录
                            userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), water.getUpdateBy(), oldWater.getAssetsId());
                        }
                    } else {
                        //判断账户类型是否修改
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            //同类型
                            BigDecimal subtract = water.getMoney().subtract(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(subtract, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //账户类型修改
                            //收入----新纪录
                            userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                            //收入----旧纪录
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    }
                } else {
                    //订单类型修改  新订单支出  旧订单收入
                    if (water.getOrderType().intValue() == 1) {
                        //判断账户类型是否修改
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            BigDecimal add = (new BigDecimal(-+(water.getMoney()).doubleValue())).add(new BigDecimal(-+(oldWater.getMoney()).doubleValue()));
                            userAssetsRestDao.updateMoneyv3(add, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //支出----新纪录
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                            //收入----旧记录
                            userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    } else {
                        //新订单收入   旧订单支出
                        //判断账户类型是否修改
                        if (water.getAssetsId().intValue() == oldWater.getAssetsId().intValue()) {
                            BigDecimal add = water.getMoney().add(oldWater.getMoney());
                            userAssetsRestDao.updateMoneyv3(add, water.getUpdateBy(), water.getAssetsId());
                        } else {
                            //收入----新纪录
                            userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                            //支出----旧记录
                            userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                        }
                    }
                }
            } else {
                //更新他人数据
                if (water.getOrderType().intValue() == oldWater.getOrderType().intValue()) {
                    //同类型   修改两人数据
                    //判断订单类型
                    if (water.getOrderType().intValue() == 1) {
                        //支出--->新人
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        //支出--->旧人 恢复
                        userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    } else {
                        //收入----新纪录
                        userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                        //收入----旧纪录
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    }
                } else {
                    //订单类型修改  新订单支出  旧订单收入
                    if (water.getOrderType().intValue() == 1) {
                        //支出--->新人
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+water.getMoney().doubleValue()), water.getUpdateBy(), water.getAssetsId());
                        //收入--->旧人 恢复
                        userAssetsRestDao.updateMoneyv3(new BigDecimal(-+oldWater.getMoney().doubleValue()), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    } else {
                        //新订单收入   旧订单支出
                        //收入--->新人
                        userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                        //支出--->旧人 恢复
                        userAssetsRestDao.updateMoneyv3(oldWater.getMoney(), oldWater.getUpdateBy(), oldWater.getAssetsId());
                    }
                }
            }
        } else if (water.getDelflag() == 1) {
            if (water.getAssetsId() != 0) {
                //删除情况  与新增 反之
                if (water.getOrderType() == 1) {
                    //支出
                    userAssetsRestDao.updateMoneyv3(water.getMoney(), water.getUpdateBy(), water.getAssetsId());
                } else {
                    //收入
                    userAssetsRestDao.updateMoneyv3(new BigDecimal(-+(water.getMoney()).doubleValue()), water.getUpdateBy(), water.getAssetsId());
                }
            }
        }
    }

    private WarterOrderRestNewLabel addLabelInfo(WarterOrderRestNewLabel charge) {
        //获取标签详情
        UserPrivateLabelRestEntity userPrivateLabelRestEntity = null;
        //追加标签信息
        if (charge.getUserPrivateLabelId() != null) {
            if (charge.getUserPrivateLabelId().intValue() == 0) {
                if (charge.getTypeId() != null) {
                    charge.setUserPrivateLabelId(Integer.valueOf(charge.getTypeId()));
                    userPrivateLabelRestEntity = userPrivateLabelRestDao.selectInfoByLabelId(Integer.valueOf(charge.getTypeId()));
                }
            } else {
                userPrivateLabelRestEntity = userPrivateLabelRestDao.selectInfoByLabelId(charge.getUserPrivateLabelId());
            }
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

    @Test
    public void run() {
        String a = "[{\"accountBookId\":3237,\"chargeDate\":1539598142000,\"createBy\":6145,\"createDate\":1539655319000,\"delflag\":0,\"id\":\"64082620-927e-4b0f-874b-3e1dd66b594d\",\"isStaged\":1,\"money\":5,\"orderType\":1,\"remark\":\"洗澡\",\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f8b9390163fc36f2010027\",\"typeName\":\"日用\",\"typePname\":\"购物\",\"updateDate\":1539598142000},{\"accountBookId\":3237,\"chargeDate\":1539598159000,\"createBy\":6145,\"createDate\":1539598159000,\"delflag\":0,\"id\":\"6c336608-b2b8-471f-85dc-0c88f6e09737\",\"isStaged\":1,\"money\":7,\"orderType\":1,\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83d33320016\",\"typeName\":\"饮食\",\"typePname\":\"饮食\",\"updateDate\":1539598159000},{\"accountBookId\":3237,\"chargeDate\":1539598175000,\"createBy\":6145,\"createDate\":1539598175000,\"delflag\":0,\"id\":\"6b749d45-7399-4e12-86fa-6c112429c1d2\",\"isStaged\":1,\"money\":7.5,\"orderType\":1,\"remark\":\"橘子\",\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83deeba0018\",\"typeName\":\"水果\",\"typePname\":\"饮食\",\"updateDate\":1539598175000},{\"accountBookId\":3237,\"chargeDate\":1539655319000,\"createBy\":6145,\"createDate\":1539598142000,\"delflag\":0,\"id\":\"fae70596-acc9-4e46-83b7-dd6de2514455\",\"isStaged\":1,\"money\":3,\"orderType\":1,\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83d33320016\",\"typeName\":\"饮食\",\"typePname\":\"饮食\",\"updateDate\":1539655319000}]";
        List<WarterOrderRestEntity> ts = com.alibaba.fastjson.JSONArray.parseArray(a, WarterOrderRestEntity.class);
        System.out.println(Arrays.toString(ts.toArray()));
        ts.sort(Comparator.naturalOrder());
        System.out.println(Arrays.toString(ts.toArray()));
    }
}