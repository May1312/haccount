package com.fnjz.front.dao;

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.warterorder.APPWarterOrderRestDTO;
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
    List<Map<String,Integer>> listForABIdSByUserInfoId(@Param("userInfoId") String userInfoId);

    /**
     * 获取当前用户所拥有的账本类型
     * @param userInfoId
     * @return
     */
    @Sql("select DISTINCT account_book_type_id as abTypeId from hbird_account_book as base2,(select account_book_id from hbird_user_account_book where user_info_id=:userInfoId)as base1 where base2.id=base1.account_book_id;")
    List<Map<String,Integer>> listForABTypeIdSByUserInfoId(@Param("userInfoId") String userInfoId);

    @Sql("select user_info_id  from hbird_user_account_book where account_book_id=:ABId;")
    List<Integer> listForUserInfoIdSByaABId(@Param("ABId") Integer ABId);

    /**
     * 获取用户默认账本
     * @param userInfoId
     * @return
     */
    @ResultType(UserAccountBookRestEntity.class)
    @Sql("select * from hbird_user_account_book where user_info_id=:userInfoId and default_flag=1;")
    UserAccountBookRestEntity getUserAccountBookByUserInfoId(@Param("userInfoId") int userInfoId);

    /**
     * 根据用户id  账本id  查询当前邀请账本
     * @param userInfoId   accountBookId
     * @return
     */
    @ResultType(UserAccountBookRestEntity.class)
    @Sql("select * from hbird_user_account_book where user_info_id=:userInfoId and account_book_id=:accountBookId  and delflag=0;")
    UserAccountBookRestEntity getUserAccountBookByUserInfoIdAndAccountBookId(@Param("userInfoId")int userInfoId,@Param("accountBookId")int accountBookId);

    /**
     * 获取用户类型
     * @param userInfoId
     * @param abId
     * @return
     */
    @Sql("select user_type from hbird_user_account_book where user_info_id=:userInfoId and account_book_id=:abId and delflag=0;")
    Integer getUserTypeByUserInfoIdAndABId(@Param("userInfoId") String userInfoId,@Param("abId") String abId);

    @Sql("SELECT COALESCE(count(account_book_id),0) FROM hbird_user_account_book WHERE user_info_id = :userInfoId AND bind_flag = 1;")
    Integer checkBindABFlag(@Param("userInfoId") String userInfoId);
    /**
     * 检查是否存在未同步记录的绑定关系 并返回待同步记录
     * @param userInfoId
     * @return
     */
    @Sql("SELECT base1.*, base3.nick_name AS reporter_nick_name, base3.avatar_url AS reporter_avatar, base2.ab_name AS ab_name FROM hbird_water_order AS base1 INNER JOIN hbird_user_info AS base3 ON base1.update_by = base3.id, ( SELECT base2.id, base2.ab_name FROM ( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id = :userInfoId AND bind_flag = 1 ) AS base1, hbird_account_book AS base2 WHERE base2.id = base1.account_book_id AND base2.STATUS = 0 ) AS base2 WHERE base1.account_book_id = base2.id AND base1.delflag = 0;")
    List<APPWarterOrderRestDTO> checkBindABFlagAndReturn(@Param("userInfoId") String userInfoId);
}
