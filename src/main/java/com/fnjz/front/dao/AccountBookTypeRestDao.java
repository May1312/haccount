package com.fnjz.front.dao;


import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
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
}
