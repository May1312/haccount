package com.fnjz.front.service.api.userbadge;

import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;

import java.util.List;

/**
 * Created by yhang on 2018/12/17.
 */
public interface UserBadgeRestService {

    /**
     * 获取我的徽章
     * @param userInfoId
     * @return
     */
    List<UserBadgeRestDTO> getMyBadges(String userInfoId);
}
