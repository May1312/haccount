package com.fnjz.front.service.impl.api.userinfoaddfield;

import com.fnjz.front.dao.UserInfoAddFieldRestDao;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yhang on 2018/11/28.
 */
@Service("userInfoAddFieldServiceImpl")
@Transactional
public class UserInfoAddFieldServiceImpl implements UserInfoAddFieldRestService {

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;

    @Override
    public void checkExists(String userInfoId, String openId) {
        String openId2= userInfoAddFieldRestDao.getByUserInfoId(userInfoId);
        if(StringUtils.isEmpty(openId2)){
            userInfoAddFieldRestDao.insert(userInfoId,openId);
        }
    }
}
