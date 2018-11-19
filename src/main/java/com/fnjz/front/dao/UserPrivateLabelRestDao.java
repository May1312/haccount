package com.fnjz.front.dao;

import com.fnjz.front.entity.api.incometype.IncomeTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeLabelIdRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestDTO;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateIncomeLabelRestDTO;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateLabelUpdateRestDTO;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateSpendLabelRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/11/07.
 */
@MiniDao
public interface UserPrivateLabelRestDao {

    /**
     * 根据用户自有标签id--->查询详情
     * @param labelId
     * @return
     */
    @ResultType(UserPrivateLabelRestEntity.class)
    @Sql("SELECT * from hbird_user_private_label where id=:labelId")
    UserPrivateLabelRestEntity selectInfoByLabelId(@Param("labelId") Integer labelId);

    /**
     * 获取当前账本类型对应系统标签  todo 优先级获取从账本类型对应标签表中获取
     * @return
     */
    @ResultType(IncomeTypeRestEntity.class)
    @Sql("SELECT base2.id, sys_income.income_name, sys_income.parent_id, sys_income.icon, base2.priority FROM hbird_income_type AS sys_income, ( SELECT id, sys_label_id, priority FROM hbird_account_book_type_label WHERE ab_type_id =:abTypeId AND label_type = 2 AND `status` = 1 ) AS base2 WHERE sys_income.id = base2.sys_label_id ORDER BY base2.priority;")
    List<IncomeTypeRestEntity> listLabelByAbIdForIncome(@Param("abTypeId") Integer abTypeId);

    @ResultType(SpendTypeRestEntity.class)
    @Sql("SELECT base2.id, sys_spend.spend_name, sys_spend.parent_id, sys_spend.icon, base2.priority FROM hbird_spend_type AS sys_spend, ( SELECT id, sys_label_id, priority FROM hbird_account_book_type_label WHERE ab_type_id = :abTypeId AND label_type = 1 AND status = 1 ) AS base2 WHERE sys_spend.id = base2.sys_label_id ORDER BY base2.priority;")
    List<SpendTypeRestEntity> listLabelByAbIdForSpend(@Param("abId")Integer abId);

    /**
     * 获取当前账本类型对应系统常用标签
     * @return
     */
    @ResultType(IncomeTypeLabelIdRestDTO.class)
    @Sql("SELECT sys_income.id, sys_income.income_name, sys_income.parent_id, sys_income.icon, base2.priority, base2.id AS labelId FROM hbird_income_type AS sys_income, ( SELECT sys_label_id, priority, id FROM hbird_account_book_type_label WHERE ab_type_id = :abTypeId AND label_type = 2 AND status = 1 AND mark = 1 ) AS base2 WHERE sys_income.id = base2.sys_label_id ORDER BY base2.priority;")
    List<IncomeTypeLabelIdRestDTO> listMarkLabelByAbIdForIncome(@Param("abTypeId") Integer abTypeId);

    @ResultType(SpendTypeLabelIdRestDTO.class)
    @Sql("SELECT sys_spend.id, sys_spend.spend_name, sys_spend.parent_id, sys_spend.icon, base2.priority, base2.id AS labelId FROM hbird_spend_type AS sys_spend, ( SELECT sys_label_id, priority, id FROM hbird_account_book_type_label WHERE ab_type_id = :abTypeId AND label_type = 1 AND STATUS = 1 AND mark = 1 ) AS base2 WHERE sys_spend.id = base2.sys_label_id ORDER BY base2.priority;")
    List<SpendTypeLabelIdRestDTO> listMarkLabelByAbIdForSpend(@Param("abTypeId") Integer abTypeId);

    /**
     * 判断用户自有标签是否已存在  ---支出
     * @return
     */
    @Sql("SELECT COALESCE ( count( id ), 0 ) FROM hbird_user_private_label WHERE user_info_id=:userInfoId and ab_type_id=:abTypeId AND property = 1 AND type = 1;")
    int checkUserPrivateLabelForSpend(@Param("userInfoId")String userInfoId,@Param("abTypeId")Integer abTypeId);

    /**
     * 判断用户自有标签是否已存在  ---收入
     * @return
     */
    @Sql("SELECT COALESCE ( count( id ), 0 ) FROM hbird_user_private_label WHERE user_info_id=:userInfoId and ab_type_id=:abTypeId AND property = 2 AND type = 1;")
    int checkUserPrivateLabelForIncome(@Param("userInfoId")String userInfoId,@Param("abTypeId")Integer abTypeId);

    /**
     * 插入用户自有标签表
     * @param userPrivateLabelRestEntity
     */
    @Sql("INSERT INTO `hbird_user_private_label` ( `user_info_id`, `type_pid`, `type_id`, `type_name`, `icon`, `priority`, `property`, `type`, `status`, `account_book_id`,`ab_type_label_id`,`ab_type_id` ) VALUES(:label.userInfoId,:label.typePid,:label.typeId,:label.typeName,:label.icon,:label.priority,:label.property,:label.type,:label.status,:label.accountBookId,:label.abTypeLabelId,:label.abTypeId);")
    void insert(@Param("label") UserPrivateLabelRestEntity userPrivateLabelRestEntity);

    /**
     * 根据账本类型id获取自有标签表中有效数据
     * @return
     */
    @ResultType(UserPrivateSpendLabelRestDTO.class)
    @Sql("SELECT id, type_name AS spendName, icon, priority FROM hbird_user_private_label WHERE user_info_id =:userInfoId AND ab_type_id =:abTypeId AND IF ( :property = 1, property = 1, property = 2 ) AND type = 1 AND STATUS = 1;")
    List<UserPrivateSpendLabelRestDTO> selectLabelByAbId(@Param("userInfoId")String userInfoId,@Param("abTypeId")Integer abTypeId, @Param("property") Integer property);

    @ResultType(UserPrivateIncomeLabelRestDTO.class)
    @Sql("SELECT id, type_name AS incomeName, icon, priority FROM hbird_user_private_label WHERE user_info_id=:userInfoId and ab_type_id=:abTypeId AND IF ( :property = 1, property = 1, property = 2 ) AND type = 1 AND STATUS = 1;")
    List<UserPrivateIncomeLabelRestDTO> selectLabelByAbId2(@Param("userInfoId")String userInfoId,@Param("abTypeId")Integer abTypeId, @Param("property") Integer property);

    /**
     *
     * @param parentId
     * @return
     */
    @ResultType(IncomeTypeRestDTO.class)
    @Sql("select * from hbird_income_type where id=:id;")
    IncomeTypeRestDTO getSystemLabelInfoByParentIdForIncome(@Param("id")String parentId);

    /**
     *
     * @param parentId
     * @return
     */
    @ResultType(IncomeTypeRestDTO.class)
    @Sql("select * from hbird_spend_type where id=:id;")
    SpendTypeRestDTO getSystemLabelInfoByParentIdForSpend(@Param("id") String parentId);

    /**
     * 根据账本id获取账本类型id
     * @param abId
     * @return
     */
    @Sql("select account_book_type_id from hbird_account_book where id = :abId")
    Integer getAccountBookTypeIdByABId(@Param("abId") Integer abId);

    /**
     * 获取所有账本类型id
     * @return
     */
    @Sql("select id from hbird_account_book_type where status=1")
    List<Integer> listAccountBookTypeId();

    /**
     * 获取日常账本对应常用标签
     * @return
     */
    @Sql("SELECT sys_income.id, sys_income.income_name, sys_income.parent_id, sys_income.icon, base2.priority,base2.id as labelId FROM hbird_income_type AS sys_income, ( SELECT base1.id,sys_label_id, priority FROM hbird_account_book_type_label AS type_label, ( select id from hbird_account_book_type where ab_type_name=\"日常账本\") AS base1 WHERE type_label.ab_type_id = base1.id AND type_label.label_type = 2 AND type_label.`status` = 1 AND mark = 1 ) AS base2 WHERE sys_income.id = base2.sys_label_id ORDER BY base2.priority;")
    List<IncomeTypeLabelIdRestDTO> listMarkLabelByDefaultForIncome();

    @Sql("SELECT sys_spend.id, sys_spend.spend_name, sys_spend.parent_id, sys_spend.icon, base2.priority,base2.id as labelId FROM hbird_spend_type AS sys_spend, ( SELECT base1.id,sys_label_id, priority FROM hbird_account_book_type_label AS type_label, ( select id from hbird_account_book_type where ab_type_name=\"日常账本\") AS base1 WHERE type_label.ab_type_id = base1.id AND type_label.label_type = 1 AND type_label.`status` = 1 AND mark = 1 ) AS base2 WHERE sys_spend.id = base2.sys_label_id ORDER BY base2.priority;")
    List<SpendTypeLabelIdRestDTO> listMarkLabelByDefaultForSpend();

    /**
     * 获取日常账本类型id
     * @return
     */
    @Sql("select id from hbird_account_book_type where ab_type_name='日常账本';")
    int getDefaultAccountBookTypeId();

    /**
     * 检查是否存在此标签
     * @param abId
     * @param labelId
     * @return
     */
    @Sql("select count(id) from hbird_user_private_label where account_book_id=:abId and ab_type_label_id=:labelId and status=1;")
    Integer checkExists(@Param("abId") String abId,@Param("labelId") String labelId);

    /**
     * 获取收入标签详情
     * @return
     */
    @ResultType(UserPrivateLabelUpdateRestDTO.class)
    @Sql("select base2.id,income.parent_id as typePid,income.id as typeId,income.income_name as typeName, base2.priority,income.icon from hbird_income_type as income,(select id,sys_label_id,priority from hbird_account_book_type_label where id=:id)as base2 where income.id=base2.sys_label_id;")
    UserPrivateLabelUpdateRestDTO getLabelInfoForIncome(@Param("id")String id);

    /**
     * 获取支出标签详情
     * @return
     */
    @ResultType(UserPrivateLabelUpdateRestDTO.class)
    @Sql("select base2.id,spend.parent_id as typePid,spend.id as typeId,spend.spend_name as typeName, base2.priority,spend.icon from hbird_spend_type as spend,(select id,sys_label_id,priority from hbird_account_book_type_label where id=:id)as base2 where spend.id=base2.sys_label_id;")
    UserPrivateLabelUpdateRestDTO getLabelInfoForSpend(@Param("id")String id);

    /**
     * 获取最大优先级
     * @param abId
     * @return
     */
    @Sql("SELECT MAX(priority) FROM hbird_user_private_label where account_book_id = :adId and property=1;")
    Integer getMaxPriorityForSpend(@Param("adId") String abId);

    /**
     * 获取最大优先级
     * @param abId
     * @return
     */
    @Sql("SELECT MAX(priority) FROM hbird_user_private_label where account_book_id = :adId and property=2;")
    Integer getMaxPriorityForIncome(@Param("adId") String abId);

    /**
     * 删除标签
     * @param abId
     */
    @Sql("update hbird_user_private_label set status=0 where id=:labelId and account_book_id=:abId;")
    void delete(@Param("abId") String abId,@Param("labelId") String labelId);

}
