package com.fnjz.front.service.impl.api.userbadge;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.IntegralsActivityRestDao;
import com.fnjz.front.dao.UserBadgeRestDao;
import com.fnjz.front.entity.api.message.MessageEntity;
import com.fnjz.front.entity.api.userbadge.BadgeLabelRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoCheckRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.enums.BadgeTypeEnum;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by yhang on 2018/12/17.
 */
@Service("userBadgeRestService")
@Transactional
public class UserBadgeRestServiceImpl implements UserBadgeRestService {

    @Autowired
    private UserBadgeRestDao userBadgeRestDao;

    //存钱效率
    @Autowired
    private AccountBookBudgetRestServiceI accountBookBudgetRestServiceI;

    //线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //插入消息列表
    @Autowired
    private IntegralsActivityRestDao integralsActivityRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    //todo 暂时业务中定义 徽章类型的图标和描述  后期徽章如果增加,这两个字段放到db中

    @Override
    public List<UserBadgeRestDTO> getMyBadges(String userInfoId, int status) {
        //获取所有类型徽章
        List<UserBadgeRestDTO> allBadges = userBadgeRestDao.getAllBadges();
        //匹配徽章类型描述
        allBadges.stream().filter(v -> {
            BadgeTypeEnum enum1 = Arrays.stream(BadgeTypeEnum.values()).filter(v2 -> (StringUtils.contains(v.getBadgeTypeName(), v2.getBadgeTypeName()))).findFirst().get();
            if (enum1 != null) {
                v.setBadgeTypeIcon(enum1.getBadgeTypeIcon());
                v.setBadgeTypeDesc(enum1.getBadgeTypeDesc());
                return true;
            } else {
                return false;
            }
        }).collect(toList());

        if (status == 1) {
            //获取已解锁数据  将这个list中的解锁数据替代allBadges中的图标字段
            List<UserBadgeRestDTO> myBadges = userBadgeRestDao.getMyBadges(userInfoId);
            myBadges.forEach(v -> {
                if (allBadges.contains(v)) {
                    //获取脚标
                    int i = allBadges.indexOf(v);
                    UserBadgeRestDTO userBadgeRestDTO = allBadges.get(i);
                    userBadgeRestDTO.setIcon(v.getIcon());
                    userBadgeRestDTO.setMyBadges(v.getMyBadges());
                    userBadgeRestDTO.setBadgeName(v.getBadgeName());
                    userBadgeRestDTO.setCreateDate(v.getCreateDate());
                    allBadges.set(i, userBadgeRestDTO);
                }
            });
            //排序
            Collections.sort(allBadges, Comparator.comparing(UserBadgeRestDTO::getPriority).reversed());
            //攒钱徽章单独处理  计算上月预算完成率（根据创建时间） 现有规则攒钱徽章排在数组首位
            //判断是否解锁攒钱徽章   解锁日期是否为上月---->判断上月存钱效率是否已获取
            return dealSaveMoneyBadge(userInfoId, allBadges);
        } else {
            //未登录
            Collections.sort(allBadges, Comparator.comparing(UserBadgeRestDTO::getPriority).reversed());
            return allBadges;
        }
    }

    /**
     * 攒钱徽章处理
     *
     * @param userInfoId
     * @param allBadges
     * @return
     */
    private List<UserBadgeRestDTO> dealSaveMoneyBadge(String userInfoId, List<UserBadgeRestDTO> allBadges) {
        if (allBadges.get(0).getCreateDate() != null) {
            //判断解锁日期是否为当月
            LocalDate localDate = LocalDate.now();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(allBadges.get(0).getCreateDate().toInstant(), ZoneId.systemDefault());
            if (StringUtils.contains(localDateTime.toString(), localDate.toString())) {
                //上月存钱效率已获取
                return allBadges;
            } else {
                //调用获取存钱效率接口
                JSONObject savingEfficiency = accountBookBudgetRestServiceI.getSavingEfficiencyv2(userInfoId, LocalDate.now().minusMonths(1).getMonthValue() + "", "1", null);
                //固定支出不存在时不统计
                BigDecimal fixedLargeExpenditure = savingEfficiency.getJSONObject("fixedSpend").getBigDecimal("fixedLargeExpenditure");
                BigDecimal fixedLifeExpenditure = savingEfficiency.getJSONObject("fixedSpend").getBigDecimal("fixedLifeExpenditure");
                if (fixedLargeExpenditure == null && fixedLifeExpenditure == null) {
                    //获得徽章数置为0
                    allBadges.get(0).setMyBadges(0);
                    return allBadges;
                } else {
                    //存钱效率 = (单月收入-当月总支出)/(当月总支出-当月固定支出)  左闭右闭
                    JSONArray arrays = savingEfficiency.getJSONArray("arrays");
                    BigDecimal monthSpend = arrays.getJSONObject(0).getBigDecimal("monthSpend");
                    BigDecimal monthIncome = arrays.getJSONObject(0).getBigDecimal("monthIncome");
                    BigDecimal result = monthIncome.subtract(monthSpend).divide(monthSpend.subtract((fixedLargeExpenditure == null ? BigDecimal.ZERO : fixedLargeExpenditure).add(fixedLifeExpenditure == null ? BigDecimal.ZERO : fixedLifeExpenditure)), BigDecimal.ROUND_HALF_DOWN);
                    //获取范围区间
                    List<UserBadgeInfoRestDTO> myBadgeInfo = getMyBadgeInfo(userInfoId, allBadges.get(0).getBadgeTypeId(), 1);
                    //按百分比顺序
                    myBadgeInfo.sort(Comparator.comparing(UserBadgeInfoRestDTO::getPercentage));
                    //查询所在区间  左闭右闭
                    UserBadgeInfoRestDTO dto;
                    Optional<UserBadgeInfoRestDTO> first = myBadgeInfo.stream().filter(v -> (v.getPercentage() <= result.doubleValue())).skip(0).filter(v2 -> (v2.getPercentage() > result.doubleValue())).findFirst();
                    if (!first.isPresent()) {
                        //不存在符合值  有可能超过0.8 按0.8处理
                        if ((myBadgeInfo.get(myBadgeInfo.size() - 1).getPercentage() < result.doubleValue())) {
                            dto = myBadgeInfo.get(myBadgeInfo.size() - 1);
                        } else {
                            //按不达标处理
                            return allBadges;
                        }
                    } else {
                        dto = first.get();
                    }
                    //获取已解锁icon
                    String icon = userBadgeRestDao.getUnlockIcon(dto.getBadgeId());
                    allBadges.get(0).setIcon(icon);
                    allBadges.get(0).setBadgeName(dto.getBadgeName());
                    taskExecutor.execute(() -> {
                        //攒钱徽章类型 只保留一条记录吧   执行更新
                        userBadgeRestDao.updateBadgeId(userInfoId, allBadges.get(0).getBadgeTypeId(), dto.getBadgeId());
                        //消息
                        MessageEntity messageEntity = new MessageEntity();
                        messageEntity.setUserInfoId(Integer.valueOf(userInfoId));
                        messageEntity.setContent(msg + "攒钱徽章" + "\"" + dto.getBadgeName() + "\"。");
                        messageEntity.setCreateBy(Integer.valueOf(userInfoId));
                        messageEntity.setStatus(2);
                        integralsActivityRestDao.insertMessage(messageEntity);
                        //此条消息放入redis
                        List<String> list = new ArrayList(1);
                        UserBadgeInfoCheckRestDTO latestBadge = userBadgeRestDao.getLatestBadge(allBadges.get(0).getBadgeTypeId(), Integer.valueOf(userInfoId));
                        //设置徽章类型id
                        latestBadge.setBadgeTypeId(allBadges.get(0).getBadgeTypeId());
                        //匹配徽章类型描述
                        BadgeTypeEnum enum1 = Arrays.stream(BadgeTypeEnum.values()).filter(v -> (StringUtils.contains(latestBadge.getBadgeTypeName(), v.getBadgeTypeName()))).findFirst().get();
                        if (enum1 != null) {
                            latestBadge.setBadgeTypeIcon(enum1.getBadgeTypeIcon());
                            latestBadge.setBadgeTypeDesc(enum1.getBadgeTypeDesc());
                        }
                        //设置总徽章数+已获得徽章数
                        Map<String, Object> map = userBadgeRestDao.getMyBadgesAndTotalBadges(Integer.valueOf(userInfoId), latestBadge.getBadgeTypeId());
                        if (map != null) {
                            latestBadge.setMyBadges(Integer.valueOf(map.get("mybadges")+""));
                            latestBadge.setTotalBadges(Integer.valueOf(map.get("totalBadges")+""));
                        }
                        list.add(JSONObject.toJSONString(latestBadge));
                        redisTemplateUtils.setListRight(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), list, 1, 30L);
                    });
                    return allBadges;
                }
            }
        } else {
            //首次调用
            //调用获取存钱效率接口
            JSONObject savingEfficiency = accountBookBudgetRestServiceI.getSavingEfficiencyv2(userInfoId, LocalDate.now().minusMonths(1).getMonthValue() + "", "1", null);
            //固定支出不存在时不统计
            BigDecimal fixedLargeExpenditure = savingEfficiency.getJSONObject("fixedSpend").getBigDecimal("fixedLargeExpenditure");
            BigDecimal fixedLifeExpenditure = savingEfficiency.getJSONObject("fixedSpend").getBigDecimal("fixedLifeExpenditure");
            if (fixedLargeExpenditure == null && fixedLifeExpenditure == null) {
                allBadges.get(0).setMyBadges(0);
                return allBadges;
            } else {
                //存钱效率 = (当月总收入-当月总支出)/(当月总支出-当月固定支出)  左闭右闭
                JSONArray arrays = savingEfficiency.getJSONArray("arrays");
                BigDecimal monthSpend = arrays.getJSONObject(0).getBigDecimal("monthSpend");
                BigDecimal monthIncome = arrays.getJSONObject(0).getBigDecimal("monthIncome");
                BigDecimal result = (monthIncome.subtract(monthSpend)).divide(monthSpend.subtract((fixedLargeExpenditure == null ? BigDecimal.ZERO : fixedLargeExpenditure).add(fixedLifeExpenditure == null ? BigDecimal.ZERO : fixedLifeExpenditure)), BigDecimal.ROUND_HALF_DOWN);
                //获取徽章百分比范围区间
                List<UserBadgeInfoRestDTO> myBadgeInfo = getMyBadgeInfo(userInfoId, allBadges.get(0).getBadgeTypeId(), 1);
                //按百分比顺序
                myBadgeInfo.sort(Comparator.comparing(UserBadgeInfoRestDTO::getPercentage));
                //查询所在区间  左闭右闭
                UserBadgeInfoRestDTO dto;
                //final double percentage = result.doubleValue();
                final double percentage=0.41;
                Optional<UserBadgeInfoRestDTO> first = myBadgeInfo.stream().filter(v -> (percentage>=v.getPercentage())).skip(0).filter(v2 -> (percentage<v2.getPercentage())).findFirst();
                 if (!first.isPresent()) {
                    //不存在符合值  有可能超过0.8 按0.8处理
                    if ((myBadgeInfo.get(myBadgeInfo.size() - 1).getPercentage() < result.doubleValue())) {
                        dto = myBadgeInfo.get(myBadgeInfo.size() - 1);
                    } else {
                        //按不达标处理
                        return allBadges;
                    }
                } else {
                    dto = first.get();
                }
                //获取已解锁icon
                String icon = userBadgeRestDao.getUnlockIcon(dto.getBadgeId());
                allBadges.get(0).setIcon(icon);
                allBadges.get(0).setMyBadges(1);
                allBadges.get(0).setBadgeName(dto.getBadgeName());
                taskExecutor.execute(() -> {
                    //插入徽章
                    userBadgeRestDao.insert(Integer.valueOf(userInfoId), dto.getBadgeId(), allBadges.get(0).getBadgeTypeId(), result, 1);
                    //消息
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUserInfoId(Integer.valueOf(userInfoId));
                    messageEntity.setContent(msg + "攒钱徽章" + "\"" + dto.getBadgeName() + "\"。");
                    messageEntity.setCreateBy(Integer.valueOf(userInfoId));
                    messageEntity.setStatus(2);
                    integralsActivityRestDao.insertMessage(messageEntity);
                    //此条消息放入redis
                    List<String> list = new ArrayList(1);
                    UserBadgeInfoCheckRestDTO latestBadge = userBadgeRestDao.getLatestBadge(allBadges.get(0).getBadgeTypeId(), Integer.valueOf(userInfoId));
                    //设置徽章类型id
                    latestBadge.setBadgeTypeId(allBadges.get(0).getBadgeTypeId());
                    //匹配徽章类型描述
                    BadgeTypeEnum enum1 = Arrays.stream(BadgeTypeEnum.values()).filter(v -> (StringUtils.contains(latestBadge.getBadgeTypeName(), v.getBadgeTypeName()))).findFirst().get();
                    if (enum1 != null) {
                        latestBadge.setBadgeTypeIcon(enum1.getBadgeTypeIcon());
                        latestBadge.setBadgeTypeDesc(enum1.getBadgeTypeDesc());
                    }
                    //设置总徽章数+已获得徽章数
                    Map<String, Object> map = userBadgeRestDao.getMyBadgesAndTotalBadges(Integer.valueOf(userInfoId), latestBadge.getBadgeTypeId());
                    if (map != null) {
                        latestBadge.setMyBadges(Integer.valueOf(map.get("mybadges")+""));
                        latestBadge.setTotalBadges(Integer.valueOf(map.get("totalBadges")+""));
                    }
                    list.add(JSONObject.toJSONString(latestBadge));
                    redisTemplateUtils.setListRight(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), list, 1, 30L);
                });
                return allBadges;
            }
        }
    }

    /**
     * 获取指定徽章类型下所有数据
     *
     * @param status     区分1为我的页面接口调用 其他为详情调用
     * @param userInfoId
     * @param btId
     * @return
     */
    @Override
    public List<UserBadgeInfoRestDTO> getMyBadgeInfo(String userInfoId, Integer btId, Integer status) {
        //获取所有类型徽章
        List<UserBadgeInfoRestDTO> allBadges = userBadgeRestDao.getMyBadgeInfoForAll(btId);
        //获取已解锁数据
        List<UserBadgeInfoRestDTO> myBadges = userBadgeRestDao.getMyBadgeInfoForUnlock(userInfoId, btId);
        myBadges.forEach(v -> {
            if (allBadges.contains(v)) {
                //获取脚标
                int i = allBadges.indexOf(v);
                UserBadgeInfoRestDTO userBadgeInfoRestDTO = allBadges.get(i);
                userBadgeInfoRestDTO.setIcon(v.getIcon());
                userBadgeInfoRestDTO.setCreateDate(v.getCreateDate());
                userBadgeInfoRestDTO.setSalary(v.getSalary());
                userBadgeInfoRestDTO.setRank(v.getRank());
                allBadges.set(i, userBadgeInfoRestDTO);
            }
        });
        //判断是否为攒钱徽章类型请求
        String btName = userBadgeRestDao.getBadgeTypeNameById(btId);
        if (StringUtils.contains(btName, BadgeTypeEnum.zanqhz.getBadgeTypeName())) {
            //攒钱徽章
            Optional<UserBadgeInfoRestDTO> first = allBadges.stream().filter(v -> v.getRank() != null).findFirst();
            if (first.isPresent()) {
                return allBadges.stream().filter(v -> v.getPercentage() >= first.get().getPercentage()).collect(toList());
            } else {
                if (status != null && status == 1) {
                    return allBadges;
                } else {
                    //不存在解锁
                    return null;
                }
            }
        } else {
            return allBadges;
        }
    }

    /**
     * 攒钱（根据预算）/工资/兼职/奖金/投资 徽章解锁公用方法
     *
     * @param water
     */
    private static String msg = "恭喜您获得新的";

    @Override
    public void unlockBadge(WarterOrderRestNewLabel water) {
        if (water.getOrderType() == 2) {
            //收入类型  获取徽章类型绑定的标签集合
            List<BadgeLabelRestDTO> list = userBadgeRestDao.getSysBadgeLabel();
            BadgeLabelRestDTO badgeLabelRestDTO = list.stream().filter(v -> (StringUtils.contains(v.getLabelName(), water.getTypeName()))).findFirst().get();
            //匹配
            if (badgeLabelRestDTO != null) {
                //查看用户当前徽章领取情况
                UserBadgeInfoRestDTO userBadgeInfoRestDTO = userBadgeRestDao.getLatestBadge(badgeLabelRestDTO.getBadgeTypeId(), water.getUpdateBy());
                if (userBadgeInfoRestDTO != null) {
                    //已解锁 判断是否解锁下一徽章
                    if ((water.getMoney().subtract(BigDecimal.valueOf(userBadgeInfoRestDTO.getSalary()))).doubleValue() >= userBadgeInfoRestDTO.getPercentage()) {
                        //解锁 获取下一徽章id
                        Map<String, Object> map = userBadgeRestDao.getNextBadgeId(badgeLabelRestDTO.getBadgeTypeId(), userBadgeInfoRestDTO.getBadgeId());
                        if (map.get("id") != null) {
                            //获取当前最新排名
                            Integer rank = userBadgeRestDao.getRankBybtid(badgeLabelRestDTO.getBadgeTypeId());
                            userBadgeRestDao.insert(water.getUpdateBy(), Integer.valueOf(map.get("id") + ""), badgeLabelRestDTO.getBadgeTypeId(), water.getMoney(), rank == null ? 1 : rank + 1);
                            MessageEntity messageEntity = new MessageEntity();
                            messageEntity.setUserInfoId(water.getUpdateBy());
                            messageEntity.setContent(msg + map.get("badge_type_name") + "\"" + map.get("badge_name") + "\"");
                            messageEntity.setCreateBy(water.getUpdateBy());
                            messageEntity.setStatus(2);
                            integralsActivityRestDao.insertMessage(messageEntity);
                            //此条消息放入redis
                            List<String> list2 = new ArrayList(1);
                            UserBadgeInfoCheckRestDTO latestBadge = userBadgeRestDao.getLatestBadge(badgeLabelRestDTO.getBadgeTypeId(), water.getUpdateBy());
                            //设置徽章类型id
                            latestBadge.setBadgeTypeId(badgeLabelRestDTO.getBadgeTypeId());
                            //匹配徽章类型描述
                            BadgeTypeEnum enum1 = Arrays.stream(BadgeTypeEnum.values()).filter(v -> (StringUtils.contains(latestBadge.getBadgeTypeName(), v.getBadgeTypeName()))).findFirst().get();
                            if (enum1 != null) {
                                latestBadge.setBadgeTypeIcon(enum1.getBadgeTypeIcon());
                                latestBadge.setBadgeTypeDesc(enum1.getBadgeTypeDesc());
                            }
                            //设置总徽章数+已获得徽章数
                            Map<String, Object> map2 = userBadgeRestDao.getMyBadgesAndTotalBadges(water.getUpdateBy(), badgeLabelRestDTO.getBadgeTypeId());
                            if (map2 != null) {
                                latestBadge.setMyBadges(Integer.valueOf(map2.get("mybadges")+""));
                                latestBadge.setTotalBadges(Integer.valueOf(map2.get("totalBadges")+""));
                            }
                            list2.add(JSONObject.toJSONString(latestBadge));
                            redisTemplateUtils.setListRight(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + ShareCodeUtil.id2sharecode(water.getUpdateBy()), list2, 1, 30L);
                        }
                    }
                } else {
                    //首次解锁该徽章
                    Map<String, Object> map = userBadgeRestDao.getNextBadgeId(badgeLabelRestDTO.getBadgeTypeId(), 0);
                    //获取当前最新排名
                    Integer rank = userBadgeRestDao.getRankBybtid(badgeLabelRestDTO.getBadgeTypeId());
                    userBadgeRestDao.insert(water.getUpdateBy(), Integer.valueOf(map.get("id") + ""), badgeLabelRestDTO.getBadgeTypeId(), water.getMoney(), rank == null ? 1 : rank + 1);
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUserInfoId(water.getUpdateBy());
                    messageEntity.setContent(msg + map.get("badge_type_name") + "\"" + map.get("badge_name") + "\"。");
                    messageEntity.setCreateBy(water.getUpdateBy());
                    messageEntity.setStatus(2);
                    integralsActivityRestDao.insertMessage(messageEntity);
                    //此条消息放入redis
                    List<String> list2 = new ArrayList(1);
                    UserBadgeInfoCheckRestDTO latestBadge = userBadgeRestDao.getLatestBadge(badgeLabelRestDTO.getBadgeTypeId(), water.getUpdateBy());
                    //设置徽章类型id
                    latestBadge.setBadgeTypeId(badgeLabelRestDTO.getBadgeTypeId());
                    //匹配徽章类型描述
                    BadgeTypeEnum enum1 = Arrays.stream(BadgeTypeEnum.values()).filter(v -> (StringUtils.contains(latestBadge.getBadgeTypeName(), v.getBadgeTypeName()))).findFirst().get();
                    if (enum1 != null) {
                        latestBadge.setBadgeTypeIcon(enum1.getBadgeTypeIcon());
                        latestBadge.setBadgeTypeDesc(enum1.getBadgeTypeDesc());
                    }
                    //设置总徽章数+已获得徽章数
                    Map<String, Object> map2 = userBadgeRestDao.getMyBadgesAndTotalBadges(water.getUpdateBy(), badgeLabelRestDTO.getBadgeTypeId());
                    if (map2 != null) {
                        latestBadge.setMyBadges(Integer.valueOf(map2.get("mybadges")+""));
                        latestBadge.setTotalBadges(Integer.valueOf(map2.get("totalBadges")+""));
                    }
                    list2.add(JSONObject.toJSONString(latestBadge));
                    redisTemplateUtils.setListRight(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + ShareCodeUtil.id2sharecode(water.getUpdateBy()), list2, 1, 30L);
                }
            }
        }
    }
}
