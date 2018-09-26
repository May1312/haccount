package com.fnjz.back.service.impl.user;

import com.fnjz.back.entity.user.ChannelBehaviorEntity;
import com.fnjz.back.service.user.DataCenterServiceI;
import com.fnjz.back.service.user.UserInfoServiceI;
import com.fnjz.front.utils.ShareCodeUtil;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/9/17 15:39
 * @Description:
 */
@Service("dataCenterService")
public class DataCenterServiceImpl extends CommonServiceImpl implements DataCenterServiceI {

    @Autowired
    private UserInfoServiceI userInfoService;

    public List<ChannelBehaviorEntity> getChannelBehaviorList(int pageSize, int rows, String registerStartDate, String registerendDate, String userId, String dowmloadChannel) {
        List<ChannelBehaviorEntity> channelBehaviorEntities = new ArrayList<>();
        String sql = "select id,nick_name,register_date,mobile_system,android_channel  from hbird_user_info where  1=1";
        sql = getQueryCondition(sql, registerStartDate, registerendDate, userId, dowmloadChannel);
        //按条件查询
        List<Map<String, Object>> userinfoList = userInfoService.findForJdbc(sql, pageSize, rows);
        //组合数据总笔数，天数
        for (int i = 0; i < userinfoList.size(); i++) {
            ChannelBehaviorEntity channelBehaviorEntity = new ChannelBehaviorEntity();
            channelBehaviorEntity.setUserId(String.valueOf(userinfoList.get(i).get("id")));
            channelBehaviorEntity.setUserNickName((String) userinfoList.get(i).get("nick_name"));
            channelBehaviorEntity.setRegisterDate((Date) userinfoList.get(i).get("register_date"));
            String downloadChannel = (String) userinfoList.get(i).get("mobile_system");
            String androidChannel = (String) userinfoList.get(i).get("android_channel");
            if (StringUtil.isNotEmpty(downloadChannel) && downloadChannel.equals("android")) {
                downloadChannel = androidChannel;
            } else if (StringUtil.isEmpty(downloadChannel)) {
                downloadChannel = "小程序";
            }
            channelBehaviorEntity.setDownloadChannel(downloadChannel);
            //用户记账总笔数
            String theNumberSql = "SELECT COUNT(id) FROM `hbird_water_order` WHERE create_by = '" + userinfoList.get(i).get("id") + "'  AND delflag = 0;";
            List<Object> theNumberSqlList = this.findListbySql(theNumberSql);
            channelBehaviorEntity.setAccountTotalTheNumber(Integer.parseInt(String.valueOf(theNumberSqlList.get(0))));
            //用户总记录天数
            String dayNmuberSql = "SELECT COUNT(*) count FROM ( SELECT COUNT(charge_date) FROM `hbird_water_order` WHERE create_by ='" + userinfoList.get(i).get("id") + "'  AND delflag = 0  GROUP BY DATE_FORMAT(charge_date,'%Y%m%d') ) as c;";
            List<Object> dayNmuberSqlList = this.findListbySql(dayNmuberSql);
            channelBehaviorEntity.setAccountTotalDayNumber(Integer.parseInt(String.valueOf(dayNmuberSqlList.get(0))));
            //添加集合
            channelBehaviorEntities.add(channelBehaviorEntity);
        }
        return channelBehaviorEntities;
    }

    public String getQueryCondition(String sql, String registerStartDate, String registerendDate, String userId, String dowmloadChannel) {
        if (StringUtil.isNotEmpty(userId)) {
            sql += " and id = '" + userId + "'";
        }
        if (StringUtil.isNotEmpty(dowmloadChannel) && !dowmloadChannel.equals("全部")) {
            sql += " and mobile_system = '" + dowmloadChannel + "'";
        }
        if (StringUtil.isNotEmpty(registerStartDate)) {
            sql += " and register_date  >= '" + registerStartDate + "'";
        }
        if (StringUtil.isNotEmpty(registerendDate)) {
            sql += " and register_date  <= '" + registerendDate + "'";
        }
        return sql;
    }

    @Override
    public List<ChannelBehaviorEntity> queryListByPage(int pageSize, int rows, String registerStartDate, String registerendDate, String userId, String dowmloadChannel, String sort, String order) {
        List<ChannelBehaviorEntity> channelBehaviorEntities = new ArrayList<>();
        String sql = "SELECT * FROM ( SELECT uu.id,uu.nick_name,uu.register_date,  " +
                "(CASE  " +
                " WHEN uu.android_channel IS NOT NULL   " +
                " THEN uu.android_channel  " +
                " WHEN uu.android_channel IS NULL THEN IFNULL(uu.mobile_system,'小程序')  " +
                " ELSE NULL END) AS mobile_system " +
                ",pp.create_by,pp.days,pp.total from hbird_user_info uu ,( " +
                "SELECT create_by,COUNT(0) days,SUM(s.t) total from ( " +
                "SELECT create_by,charge_date,COUNT(0) t from hbird_water_order WHERE delflag=0 GROUP BY create_by,charge_date  " +
                ") s GROUP BY s.create_by) pp where uu.id=pp.create_by ) as result where 1=1   ";
        //按条件查询
        sql = getQueryCondition(sql, registerStartDate, registerendDate, userId, dowmloadChannel);
        //排序
        if (StringUtil.isNotEmpty(sort)) {
            if (sort.equals("accountTotalDayNumber")) {
                sql += "ORDER BY days " + order;
            }
            if (sort.equals("accountTotalTheNumber")) {
                sql += "ORDER BY total " + order;
            }
        } else {
            sql += " ORDER BY total  desc ";
        }

        List<Map<String, Object>> userinfoList = userInfoService.findForJdbc(sql, pageSize, rows);
        for (int i = 0; i < userinfoList.size(); i++) {
            ChannelBehaviorEntity channelBehaviorEntity = new ChannelBehaviorEntity();
            String id = String.valueOf(userinfoList.get(i).get("id"));
            channelBehaviorEntity.setFengniaoId(ShareCodeUtil.id2sharecode(Integer.parseInt(id)));
            channelBehaviorEntity.setUserId(id);
            channelBehaviorEntity.setUserNickName((String) userinfoList.get(i).get("nick_name"));
            channelBehaviorEntity.setRegisterDate((Date) userinfoList.get(i).get("register_date"));
            String downloadChannel = (String) userinfoList.get(i).get("mobile_system");
            channelBehaviorEntity.setDownloadChannel(downloadChannel);
            channelBehaviorEntity.setAccountTotalDayNumber(Integer.parseInt(String.valueOf(userinfoList.get(i).get("days"))));
            channelBehaviorEntity.setAccountTotalTheNumber(Integer.parseInt(String.valueOf(userinfoList.get(i).get("total"))));
            //添加集合
            channelBehaviorEntities.add(channelBehaviorEntity);
        }
        return channelBehaviorEntities;
    }

    @Override
    public Long getCount(String registerStartDate, String registerendDate, String userId, String dowmloadChannel) {
        String sql = "select count(id) from (SELECT uu.id,uu.nick_name,uu.register_date,   " +
                "  (CASE   " +
                "  WHEN uu.android_channel IS NOT NULL   " +
                "  THEN uu.android_channel   " +
                "  WHEN uu.android_channel IS NULL THEN IFNULL(uu.mobile_system,'小程序')   " +
                "  ELSE NULL END) AS mobile_system  " +
                "  ,pp.create_by,pp.days,pp.total from hbird_user_info uu ,(  " +
                "  SELECT create_by,COUNT(0) days,SUM(s.t) total from (  " +
                "  SELECT create_by,charge_date,COUNT(0) t from hbird_water_order WHERE delflag=0 GROUP BY create_by,charge_date   " +
                "  ) s GROUP BY s.create_by) pp where uu.id=pp.create_by ) AS result where 1=1 ";
        sql = getQueryCondition(sql, registerStartDate, registerendDate, userId, dowmloadChannel);

        Long countForJdbc = userInfoService.getCountForJdbc(sql);
        return countForJdbc;
    }
}
