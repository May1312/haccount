package com.fnjz.front.dao;

import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.SceneABBudgetRestDTO;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
public interface AccountBookRestDao {
    /**
     * 新增用户账本记录 返回主键
     * @param accountBookRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("accountBookRestEntity") AccountBookRestEntity accountBookRestEntity);

    /**
     * 获取当前账本成员数
     * @param accountBookId
     * @return
     */
    @Sql("select member from hbird_account_book where id=:accountBookId;")
    int getTotalMember(@Param("accountBookId") String accountBookId);

    /**
     * 获取账本对应成员数
     * @param userInfoId
     * @return
     */
    @Sql("SELECT base1.member AS members, base2.account_book_id AS accountBookId FROM hbird_account_book as base1,( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id =:userInfoId AND delflag = 0 )as base2 WHERE base1.id =base2.account_book_id AND base1.status = 0;")
    List<Map<String,Integer>> checkABMembers(@Param("userInfoId") String userInfoId);

    /**
     * 首页调用  成员头像
     * @param abId
     * @return
     */
    @Sql("SELECT base1.user_info_id as userInfoId,base1.user_type as userType,base3.avatar_url as avatarUrl FROM hbird_user_account_book base1 inner JOIN hbird_user_info as base3 on base1.user_info_id=base3.id where base1.account_book_id=:abId and base1.delflag=0;")
    List<Map<String,Object>> getABMembers(@Param("abId") Integer abId);

    /**
     * 成员管理页调用
     * @param abId
     * @return
     */
    @Sql("SELECT base1.id,base1.user_info_id AS userInfoId, base1.user_type AS userType, base3.avatar_url AS avatarUrl, base3.nick_name as nickName, base1.create_date AS createDate FROM hbird_user_account_book base1 INNER JOIN hbird_user_info AS base3 ON base1.user_info_id = base3.id WHERE base1.account_book_id =:abId and delflag=0 order by base1.create_date;")
    List<Map<String,Object>> membersInfo(@Param("abId") Integer abId);

    /**
     * 获取用户所有账本
     * @param userInfoId
     * @return
     */
    @Sql("select base1.account_book_id as id,base1.user_type,base1.default_flag,base2.ab_name,base2.account_book_type_id,base2.update_date,base2.member,base3.ab_type_name as abTypeName,base3.type_budget as typeBudget,base3.icon,base3.id as abTypeId from hbird_user_account_book as base1 INNER JOIN hbird_account_book as base2 on base1.account_book_id=base2.id INNER JOIN hbird_account_book_type as base3 on base2.account_book_type_id=base3.id where base1.user_info_id=:userInfoId and base1.delflag=0 order by update_date desc;")
    List<AccountBookRestDTO> getABAll(@Param("userInfoId") String userInfoId);

    @Sql("SELECT base1.account_book_id AS id, base1.user_type, base1.default_flag, base3.type_budget AS typeBudget, base3.id AS abTypeId FROM hbird_user_account_book AS base1 INNER JOIN hbird_account_book AS base2 ON base1.account_book_id = base2.id INNER JOIN hbird_account_book_type AS base3 ON base2.account_book_type_id = base3.id WHERE base1.user_info_id =:userInfoId AND base1.delflag = 0 order by default_flag desc;")
    List<AccountBookRestDTO> getCheckABAll(@Param("userInfoId") String userInfoId);

    /**
     * 判断当前用户  当前账本属性
     * @param userInfoId
     * @param abId
     * @return
     */
    @Sql("select user_type from hbird_user_account_book where user_info_id=:userInfoId and account_book_id=:abId AND delflag=0;")
    Integer checkUserType(@Param("userInfoId") String userInfoId,@Param("abId") Integer abId);

    /**
     * 根据账本id获取账本name
     * @param abId
     * @return
     */
    @Sql("select ab_name from hbird_account_book where id=:abId;")
    String getTypeNameByABId(@Param("abId") Integer abId);

    /**
     * 删除用户---账本绑定关系
     * @param userInfoId
     * @param abId
     */
    @Sql("UPDATE `hbird_user_account_book` SET `delflag` = 1, `del_date` = now() WHERE user_info_id = :userInfoId and account_book_id=:abId;")
    void deleteUserAB(@Param("userInfoId") String userInfoId,@Param("abId") Integer abId);

    /**
     * 根据记录id 删除
     * @param abId
     */
    @Sql("UPDATE `hbird_user_account_book` SET `delflag` = 1, `del_date` = now() WHERE id=:id;")
    void deleteUserABById(@Param("id") String id,@Param("abId") Integer abId);

    /**
     * 根据绑定时间设置为账本所有者  即赋予管理员权限
     * @param abId
     */
    @Sql("update hbird_user_account_book as base1,(select id from hbird_user_account_book where account_book_id=:abId and delflag=0 group by create_date limit 0,1) as base2 set base1.user_type=0 where base1.id=base2.id;")
    void setOwner(@Param("abId") Integer abId);

    /**
     * 修改账本成员数
     * @param abId
     * @param i
     */
    @Sql("update hbird_account_book set member=member+(:num) where id=:abId;")
    void updateABMember(@Param("abId")Integer abId,@Param("num") int i);

    /**
     * 删除账本
     * @param abId
     */
    @Sql("UPDATE `hbird_account_book` SET `status` = 1, `del_date` = now() WHERE id=:abId;")
    void deleteAB(@Param("abId") Integer abId);

    /**
     * 创建账本
     * @param accountBookRestEntity
     * @return
     */
    @IdAutoGenerator
    @Sql("INSERT INTO `hbird_account_book` ( `ab_name`, `status`, `update_date`, `create_date`, `create_by`,`account_book_type_id`, `member` ) VALUES ( :ab.abName, 0, now(), now(), :ab.createBy, :ab.accountBookTypeId,1);")
    int createAB(@Param("ab") AccountBookRestEntity accountBookRestEntity);

    /**
     * 修改账本名称
     * @param abName
     * @param abId
     */
    @Sql("UPDATE `hbird_account_book` SET `ab_name` = :abName, `update_date` = now() WHERE id=:abId;")
    void updateAB(@Param("abName") String abName,@Param("abId") String abId);

    /**
     * 获取账本类型
     * @return
     */
    @Sql("select base2.type_budget from (select account_book_type_id from hbird_account_book where id=:abId) as base1,hbird_account_book_type as base2 where base2.id=base1.account_book_type_id;")
    int getABTypeByABId(@Param("abId")Integer abId);

    /**
     * 获取场景账本预算
     * @param abId
     * @return
     */
    @Sql("select id,account_book_id,budget_money,begin_time,end_time,scene_type from hbird_accountbook_budget where account_book_id=:abId;")
    SceneABBudgetRestDTO getSceneABBudget(@Param("abId") Integer abId);

    /**
     * 更新账本时间
     * @param abId
     */
    @Sql("UPDATE `hbird_account_book` SET `update_date` = NOW() WHERE `id` = :abId;")
    void updateABtime(@Param("abId") Integer abId);

    /**
     * 获取默认账本数据
     * @param userInfoId
     * @return
     */
    @Sql("SELECT base1.account_book_id AS id,base1.user_type,base1.default_flag,base3.type_budget AS typeBudget,base3.id AS abTypeId,base2.ab_name FROM hbird_user_account_book AS base1 INNER JOIN hbird_account_book AS base2 ON base1.account_book_id = base2.id INNER JOIN hbird_account_book_type AS base3 ON base2.account_book_type_id = base3.id WHERE base1.user_info_id =:userInfoId AND base1.delflag = 0 ORDER BY base1.user_type,base1.default_flag limit 0,1;")
    AccountBookRestDTO getDefaultAB(@Param("userInfoId") String userInfoId);
}
