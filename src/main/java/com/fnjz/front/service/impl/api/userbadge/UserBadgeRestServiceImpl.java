package com.fnjz.front.service.impl.api.userbadge;

import com.fnjz.front.dao.UserBadgeRestDao;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yhang on 2018/12/17.
 */
public class UserBadgeRestServiceImpl implements UserBadgeRestService {

    @Autowired
    private UserBadgeRestDao userBadgeRestDao;

    @Override
    public List<UserBadgeRestDTO> getMyBadges(String userInfoId) {
        //获取已解锁数据
        List<UserBadgeRestDTO> myBadges = userBadgeRestDao.getMyBadges(userInfoId);
        //获取所有类型徽章
        List<UserBadgeRestDTO> allBadges= userBadgeRestDao.getAllBadges();
        myBadges.removeAll(allBadges);
        //myBadges.contains()
        return null;
    }
}
