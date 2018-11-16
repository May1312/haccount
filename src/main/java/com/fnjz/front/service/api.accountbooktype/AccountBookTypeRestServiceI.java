package com.fnjz.front.service.api.accountbooktype;

import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

public interface AccountBookTypeRestServiceI extends CommonService{

    /**
     * 获取所有账本类型
     * @return
     */
    List<AccountBookTypeRestEntity> getABTypeAll();
}
