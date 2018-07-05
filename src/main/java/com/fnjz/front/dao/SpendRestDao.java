package com.fnjz.front.dao;

import com.fnjz.front.entity.api.spend.SpendRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/6/13.
 */
@MiniDao
public interface SpendRestDao {

    /**
     * 分页查询支出
     * @param accountBookId
     * @param curpage
     * @param itemPerPage
     * @return
     */
    @ResultType(SpendRestEntity.class)
    @Sql("SELECT * FROM hbird_spend where account_book_id=:accountBookId AND delflag = 0 ORDER BY create_date LIMIT 0,3")
    List<SpendRestEntity> findListForPage(@Param("accountBookId") String accountBookId,@Param("curpage") Integer curpage,@Param("itemPerPage") Integer itemPerPage);

    /**
     * 统计总记录数
     * @param accountBookId
     * @return
     */
    @Sql("select count(*) from hbird_spend where account_book_id=:accountBookId AND delflag = 0")
    Integer getCount(@Param("accountBookId") String accountBookId);
}
