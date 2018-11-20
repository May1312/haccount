package com.fnjz.front.dao;


import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/11/10.
 */
@MiniDao
public interface AccountBookTypeRestDao {

    /**
     * 获取所有账本类型
     * @return
     */
    @ResultType(AccountBookTypeRestEntity.class)
    @Sql("select id,ab_type_name,icon_describe,type_budget from hbird_account_book_type where status=1 order by priority;")
    List<AccountBookTypeRestEntity> getABTypeAll();

    /**
     * 获取用户下已有的账本类型
     * @param userInfoId
     * @return
     */
    @Sql(" SELECT base3.id, base3.ab_type_name, base3.icon_describe FROM hbird_account_book_type AS base3, ( SELECT DISTINCT account_book_type_id FROM hbird_account_book AS base1, ( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id =:userInfoId AND delflag = 0 ) AS base2 WHERE base1.id = base2.account_book_id ) AS base4 WHERE base3.id = base4.account_book_type_id and base3.STATUS = 1 ORDER BY base3.priority;")
    List<AccountBookTypeRestEntity> getHadABType(@Param("userInfoId") String userInfoId);
}
