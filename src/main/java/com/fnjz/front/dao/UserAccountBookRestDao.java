package com.fnjz.front.dao;

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import org.jeecgframework.minidao.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
public interface UserAccountBookRestDao {
    /**
     * 新增用户账本关系记录 返回主键
     * @param userAccountBookRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userAccountBookRestEntity") UserAccountBookRestEntity userAccountBookRestEntity);

    @Sql("select account_book_id from hbird_user_account_book where user_info_id=:userInfoId;")
    List<String> listForABIdSByUserInfoId(@Param("userInfoId") String userInfoId);

    @Sql("select user_info_id  from hbird_user_account_book where account_book_id=:ABId;")
    List<Integer> listForUserInfoIdSByaABId(@Param("ABId") Integer ABId);

    /**
     * 获取用户默认账本
     * @param userInfoId
     * @return
     */
    @ResultType(UserAccountBookRestEntity.class)
    @Sql("select * from hbird_user_account_book where user_info_id=:userInfoId and default_flag=1;")
    UserAccountBookRestEntity getUserAccountBookByUserInfoId(int userInfoId);

    /**
     * 根据用户id  账本id  查询当前邀请账本
     * @param userInfoId   accountBookId
     * @return
     */
    @ResultType(UserAccountBookRestEntity.class)
    @Sql("select * from hbird_user_account_book where user_info_id=:userInfoId and account_book_id=:accountBookId  and default_flag=1;")
    UserAccountBookRestEntity getUserAccountBookByUserInfoIdAndAccountBookId(int userInfoId,int accountBookId);

    /**
     * 获取用户类型
     * @param userInfoId
     * @param abId
     * @return
     */
    @Sql("select user_type from hbird_user_account_book where user_info_id=:userInfoId and account_book_id=:abId and delflag=0;")
    Integer getUserTypeByUserInfoIdAndABId(@Param("userInfoId") String userInfoId,@Param("abId") String abId);
}
