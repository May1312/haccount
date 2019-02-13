package com.fnjz.front.service.api.userfestivaltags;

import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;

import java.util.List;

/**
 * Created by yhang on 2019/1/18.
 */
public interface UserFestivalTagsRestService {
    /**
     * 获取用户贴纸解锁情况
     * @param userInfoId
     * @return
     */
    List<FestivalTagsRestEntity> getFestivalTags(String userInfoId,String festivalType);
}
