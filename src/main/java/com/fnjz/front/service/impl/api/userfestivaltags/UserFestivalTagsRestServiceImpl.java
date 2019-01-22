package com.fnjz.front.service.impl.api.userfestivaltags;

import com.fnjz.front.dao.UserFestivalTagsRestDao;
import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;
import com.fnjz.front.entity.api.userfestivaltags.UserFestivalTagsRestEntity;
import com.fnjz.front.service.api.userfestivaltags.UserFestivalTagsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yhang on 2019/1/18.
 */
@Service("userFestivalTagsRestService")
public class UserFestivalTagsRestServiceImpl implements UserFestivalTagsRestService {

    @Autowired
    private UserFestivalTagsRestDao userFestivalTagsRestDao;

    @Override
    public List<FestivalTagsRestEntity> getFestivalTags(String userInfoId,String festivalType) {
        //获取系统贴纸
        List<FestivalTagsRestEntity> systemList = userFestivalTagsRestDao.getSystemFestivalTags(festivalType);
        //获取个人解锁贴纸
        List<UserFestivalTagsRestEntity> userList = userFestivalTagsRestDao.getUserFestivalTags(userInfoId);
        return getList(userList,systemList);
    }

    /**
     * list1 个人
     * list2 系统
     *
     * @param list1
     * @param list2
     * @return
     */
    private List<FestivalTagsRestEntity> getList(List<UserFestivalTagsRestEntity> list1, List<FestivalTagsRestEntity> list2) {
        list1.forEach(v -> {
            if (list2.contains(v)) {
                //获取脚标
                int i = list2.indexOf(v);
                FestivalTagsRestEntity festivalTagsRestEntity = list2.get(i);
                festivalTagsRestEntity.setStatus(1);
                list2.set(i, festivalTagsRestEntity);
            }
        });
        return list2;
    }
}
