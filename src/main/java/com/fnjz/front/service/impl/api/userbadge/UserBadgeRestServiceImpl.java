package com.fnjz.front.service.impl.api.userbadge;

import com.fnjz.front.dao.UserBadgeRestDao;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yhang on 2018/12/17.
 */
@Service("userBadgeRestService")
@Transactional
public class UserBadgeRestServiceImpl implements UserBadgeRestService {

    @Autowired
    private UserBadgeRestDao userBadgeRestDao;

    @Override
    public List<UserBadgeRestDTO> getMyBadges(String userInfoId, int status) {
        //获取所有类型徽章
        List<UserBadgeRestDTO> allBadges = userBadgeRestDao.getAllBadges();
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
}
