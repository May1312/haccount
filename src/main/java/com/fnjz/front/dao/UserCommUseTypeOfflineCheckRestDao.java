package com.fnjz.front.dao;

import com.fnjz.front.entity.api.usercommusetypeofflinecheck.UserCommUseTypeOfflineCheckRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * 离线-用户常用类目/排序关系检查表dao
 * Created by yhang on 2018/9/4.
 */
@MiniDao
public interface UserCommUseTypeOfflineCheckRestDao {

    /**
     * 获取参数---->version最大版本
     * @return
     */
    @Sql("select type,max(version) as version from hbird_user_comm_use_type_offline_check where account_book_id =:accountBookId group by type;")
    List<UserCommUseTypeOfflineCheckRestEntity> getUserCommUseTypeOfflineCheck(@Param("accountBookId")String accountBookId);

    //first to 插入版本标签
    @Sql("INSERT INTO `hbird_account`.`hbird_user_comm_use_type_offline_check` (`account_book_id`, `type`, `version`, `create_date`) VALUES(:accountBookId, :type, 'v1', now());")
    void insert(@Param("accountBookId")String accountBookId, @Param("type")String type);

    //first to 更新版本标签
    @Sql("UPDATE `hbird_account`.`hbird_user_comm_use_type_offline_check` AS off, ( SELECT version FROM hbird_user_comm_use_type_offline_check WHERE account_book_id = :accountBookId and type = :type ) AS one SET off.`version` = CONCAT( 'v', SUBSTR( one.version, 2 )+1 ),update_date = NOW() WHERE off.account_book_id = :accountBookId and type = :type;")
    void update(@Param("accountBookId")String accountBookId, @Param("type")String type);
}
