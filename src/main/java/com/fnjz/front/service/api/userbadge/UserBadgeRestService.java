package com.fnjz.front.service.api.userbadge;

import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
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

    /**
     * 获取某一具体徽章类型完成情况
     * @param btId
     * @return
     */
    List<UserBadgeInfoRestDTO> getMyBadgeInfo(String userInfoId,Integer btId);
}
