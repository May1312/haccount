package com.fnjz.front.service.impl.api.userbadge;

import com.fnjz.front.dao.UserBadgeRestDao;
import com.fnjz.front.entity.api.userbadge.BadgeLabelRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.enums.BadgeTypeEnum;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by yhang on 2018/12/17.
 */
@Service("userBadgeRestService")
@Transactional
public class UserBadgeRestServiceImpl implements UserBadgeRestService {

    @Autowired
    private UserBadgeRestDao userBadgeRestDao;

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
            //获取已解锁数据
            List<UserBadgeRestDTO> myBadges = userBadgeRestDao.getMyBadges(userInfoId);
            myBadges.forEach(v -> {
                if (allBadges.contains(v)) {
                    //获取脚标
                    int i = allBadges.indexOf(v);
                    UserBadgeRestDTO userBadgeRestDTO = allBadges.get(i);
                    userBadgeRestDTO.setIcon(v.getIcon());
                    userBadgeRestDTO.setMyBadges(v.getMyBadges());
                    userBadgeRestDTO.setBadgeName(v.getBadgeName());
                    allBadges.set(i, userBadgeRestDTO);
                }
            });
            //排序
            Collections.sort(allBadges, Comparator.comparing(UserBadgeRestDTO::getPriority).reversed());
            return allBadges;
        } else {
            //未登录
            Collections.sort(allBadges, Comparator.comparing(UserBadgeRestDTO::getPriority).reversed());
            return allBadges;
        }
    }

    @Override
    public List<UserBadgeInfoRestDTO> getMyBadgeInfo(String userInfoId, Integer btId) {
        //获取已解锁数据
        List<UserBadgeInfoRestDTO> myBadges = userBadgeRestDao.getMyBadgeInfoForUnlock(userInfoId, btId);
        //获取所有类型徽章
        List<UserBadgeInfoRestDTO> allBadges = userBadgeRestDao.getMyBadgeInfoForAll(btId);
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
        return allBadges;
    }

    /**
     * 工资/兼职/奖金 徽章解锁公用方法
     * @param water
     */
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
                    if((water.getMoney().subtract(BigDecimal.valueOf(userBadgeInfoRestDTO.getSalary()))).doubleValue()>=userBadgeInfoRestDTO.getPercentage()) {
                        //解锁 获取下一徽章id
                        Integer badgeId = userBadgeRestDao.getNextBadgeId(badgeLabelRestDTO.getBadgeTypeId(),userBadgeInfoRestDTO.getBadgeId());
                        if(badgeId!=null){
                            //获取当前最新排名
                            Integer rank = userBadgeRestDao.getRankBybtid(badgeLabelRestDTO.getBadgeTypeId());
                            userBadgeRestDao.insert(water.getUpdateBy(),badgeId,badgeLabelRestDTO.getBadgeTypeId(),water.getMoney(),rank==null?1:rank+1);
                        }
                    }
                } else {
                    //首次解锁该徽章
                    Integer badgeId = userBadgeRestDao.getNextBadgeId(badgeLabelRestDTO.getBadgeTypeId(),0);
                    //获取当前最新排名
                    Integer rank = userBadgeRestDao.getRankBybtid(badgeLabelRestDTO.getBadgeTypeId());
                    userBadgeRestDao.insert(water.getUpdateBy(),badgeId,badgeLabelRestDTO.getBadgeTypeId(),water.getMoney(),rank==null?1:rank+1);
                }
            }
        }
    }
}
