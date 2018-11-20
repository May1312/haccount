package com.fnjz.front.service.impl.api.accountbooktype;

import com.fnjz.front.dao.AccountBookTypeRestDao;
import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import com.fnjz.front.service.api.accountbooktype.AccountBookTypeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("accountBookTypeRestService")
@Transactional
public class AccountBookTypeRestServiceImpl extends CommonServiceImpl implements AccountBookTypeRestServiceI {

    @Autowired
    private AccountBookTypeRestDao accountBookTypeRestDao;

    @Override
    public List<AccountBookTypeRestEntity> getABTypeAll() {
        return accountBookTypeRestDao.getABTypeAll();
    }

    @Override
    public List<AccountBookTypeRestEntity> getHadABType(String userInfoId) {
        return accountBookTypeRestDao.getHadABType(userInfoId);
    }
}