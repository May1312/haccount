package com.fnjz.front.service.impl.api.useraccountbook;

import com.fnjz.front.dao.UserAccountBookRestDao;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userAccountBookRestService")
@Transactional
public class UserAccountBookRestServiceImpl extends CommonServiceImpl implements UserAccountBookRestServiceI {

    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;



    /**
     * 获取当前用户下的账本ids
     * @param userInfoId
     * @return
     */
    @Override
    public List<String> listForABIdSByUserInfoId(String userInfoId){
        return userAccountBookRestDao.listForABIdSByUserInfoId(userInfoId);
    }

    @Override
    public UserAccountBookRestEntity getUserAccountBookByUserInfoId(int userInfoId) {
        return userAccountBookRestDao.getUserAccountBookByUserInfoId(userInfoId);
    }

    @Override
    public UserAccountBookRestEntity checkUserisExistAccount(String userInfoId, String accountBookId) {
        UserAccountBookRestEntity userAccountBookByUserInfoIdAndAccountBookId = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(userInfoId), Integer.parseInt(accountBookId));
        return userAccountBookByUserInfoIdAndAccountBookId;
    }
}