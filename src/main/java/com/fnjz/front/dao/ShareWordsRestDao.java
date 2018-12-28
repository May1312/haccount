package com.fnjz.front.dao;

import com.fnjz.front.entity.api.sharewords.ShareWordsRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2018/12/08.
 */
@MiniDao
public interface ShareWordsRestDao {

    /**
     * 获取一条随机分享语
     * 产生一个随机数
     * ( ( SELECT MAX( id )+1 FROM hbird_share_words WHERE festival IS NULL ) - ( SELECT MIN( id ) FROM hbird_share_words WHERE festival IS NULL ) ) * RAND( )
     * ifnull中先判断是为节日
     * @return
     */
    @ResultType(ShareWordsRestDTO.class)
    @Sql("SELECT festival, festival_day, words, icon FROM hbird_share_words WHERE id = ( SELECT * FROM ( SELECT IFNULL( ( SELECT id FROM hbird_share_words WHERE festival_day = DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND STATUS = 1 AND id >= ( ( SELECT MAX( id ) + 1 FROM hbird_share_words WHERE festival_day = DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND STATUS = 1 ) - ( SELECT MIN( id ) FROM hbird_share_words WHERE festival_day = DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND STATUS = 1 ) ) * RAND( ) AND festival_day = DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND STATUS = 1 LIMIT 1 ), ( SELECT id FROM hbird_share_words WHERE id >= ( ( SELECT MAX( id ) + 1 FROM hbird_share_words WHERE festival_day IS NULL AND STATUS = 1 ) - ( SELECT MIN( id ) FROM hbird_share_words WHERE festival_day IS NULL AND STATUS = 1 )* RAND( ) ) AND festival_day IS NULL AND STATUS = 1 LIMIT 1 ) ) ) AS base1 );")
    ShareWordsRestDTO getShareWords();
}
