package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userfestivaltags.FestivalTagsRestEntity;
import com.fnjz.front.entity.api.userfestivaltags.UserFestivalTagsRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2019/1/18.
 */
@MiniDao
public interface UserFestivalTagsRestDao {

    @Sql("select * from hbird_user_festival_tags where user_info_id=:userInfoId order by id desc limit 1;")
    UserFestivalTagsRestEntity getLatest(@Param("userInfoId") String userInfoId);

    @Sql("insert into hbird_user_festival_tags (`user_info_id`,`tags_id`,`create_date`) values (:userInfoId,:tagsId,now());")
    void insert(@Param("userInfoId") String userInfoId,@Param("tagsId") int id);

    @Sql("select festival_type,icon_type,icon,description from hbird_festival_tags where id=:id;")
    FestivalTagsRestEntity getTagsById(@Param("id") int id);

    @Sql("select * from hbird_festival_tags where festival_type=:festivalType;")
    List<FestivalTagsRestEntity> getSystemFestivalTags(@Param("festivalType") String festivalType);

    @Sql("select * from hbird_user_festival_tags where user_info_id=:userInfoId;")
    List<UserFestivalTagsRestEntity> getUserFestivalTags(@Param("userInfoId") String userInfoId);
}
