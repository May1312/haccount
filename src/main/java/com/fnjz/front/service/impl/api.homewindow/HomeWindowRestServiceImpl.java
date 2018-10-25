package com.fnjz.front.service.impl.api.homewindow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.HomeWindowRestDao;
import com.fnjz.front.entity.api.banner.BannerRestDTO;
import com.fnjz.front.entity.api.homewindow.HomeWindowRestDTO;
import com.fnjz.front.service.api.homewindow.HomeWindowRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service("homeWindowRestService")
@Transactional
public class HomeWindowRestServiceImpl extends CommonServiceImpl implements HomeWindowRestServiceI {

    @Autowired
    private HomeWindowRestDao homeWindowRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取首页弹框
     *
     * @param userInfoId
     * @param shareCode
     * @return
     */
    @Override
    public JSONObject listForWindow(String userInfoId, String shareCode,String type,String version) {
        //获取用户弹框读取情况
        String cacheActivity = redisTemplateUtils.getForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode);
        JSONArray activity = JSONArray.parseArray(cacheActivity);
        boolean flag = false;
        if (activity != null) {
            for (int i = 0; i < activity.size(); i++) {
                JSONObject jsonObject = activity.getJSONObject(i);
                //判断当前获取用户读取状态
                if (jsonObject.getInteger("hasRead") != 2) {
                    //判断推送次数
                    if (jsonObject.getInteger("pushToUser") < 2) {
                        //定义推送两次
                        flag = true;
                        break;
                    }
                }
            }
        } else {
            flag = true;
        }
        List<HomeWindowRestDTO> list = new ArrayList<>();
        Period period = null;
        if (flag) {
            list = homeWindowRestDao.listForWindow(type,version);
            if(list.size()>0){
                //获取下线时间
                if(list.get(0).getDowntime()!=null){
                    Instant instant = list.get(0).getDowntime().toInstant();
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
                    LocalDate localDate = localDateTime.toLocalDate();
                    if(localDate.isAfter(LocalDate.now())){
                        period = Period.between(LocalDate.now(),localDate);
                    }
                }
            }
            //判断读取情况   id匹配
            if (activity != null) {
                boolean tag = true;
                for (HomeWindowRestDTO homeWindowRestDTO : list) {
                    for (int i = 0; i < activity.size(); i++) {
                        JSONObject jsonObject = activity.getJSONObject(i);
                        //遍历cache中id  匹配mysql中id
                        if (StringUtils.equals(jsonObject.getString("activityId"), homeWindowRestDTO.getId())) {
                            if (jsonObject.getInteger("hasRead") != 2) {
                                //判断推送次数
                                if (jsonObject.getInteger("pushToUser") < 2) {
                                    //定义推送两次
                                    jsonObject.put("pushToUser", (jsonObject.getInteger("pushToUser") + 1));
                                    tag = false;
                                    break;
                                }
                            }
                            //id相等 次数累计
                            jsonObject.put("pushToUser", jsonObject.getInteger("pushToUser") == null ? 1 : (jsonObject.getInteger("pushToUser") + 1));
                            tag = false;
                            break;
                        }
                    }
                    if (tag) {
                        //遍历完不匹配id  即首次读取
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("activityId", homeWindowRestDTO.getId());
                        jsonObject.put("pushToUser", 1);
                        jsonObject.put("hasRead", 1);
                        activity.add(jsonObject);
                    }
                }
            } else {
                activity = new JSONArray();
                for (HomeWindowRestDTO homeWindowRestDTO : list) {
                    //遍历完不匹配id  即首次读取
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("activityId", homeWindowRestDTO.getId());
                    jsonObject.put("pushToUser", 1);
                    jsonObject.put("hasRead", 1);
                    activity.add(jsonObject);
                }
            }
        }
        //cache
        /*if(period!=null){
            redisTemplateUtils.cacheForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode, activity.toJSONString(),Long.valueOf(period.getDays()+1));
        }else{
            //没有设置下线时间 持久存储吧
            redisTemplateUtils.cacheForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode, activity.toJSONString());
        }*/
        //获取邀请用户成功人数
        Object inviteCount = redisTemplateUtils.getForHashKeyObject(RedisPrefix.USER_INVITE_COUNT + shareCode, "inviteCount");
        JSONObject jsonObject = new JSONObject();
        if (inviteCount != null) {
            jsonObject.put("inviteCount", inviteCount);
            //获取奖励积分数
            Object obj = redisTemplateUtils.getForHashKeyObject(RedisPrefix.SYS_INTEGRAL_TODAY_TASK, "inviteFriendsAware");
            if (obj != null) {
                jsonObject.put("inviteFriendsAware", Integer.valueOf(obj + "") * Integer.valueOf(inviteCount + ""));
            }
        }
        //redisTemplateUtils.deleteKey(RedisPrefix.USER_INVITE_COUNT + shareCode);
        JSONObject result = new JSONObject();
        result.put("inviteCount", jsonObject);
        result.put("activity", list);
        return result;
    }

    /**
     * 获取轮播图
     * @param userInfoId
     * @param shareCode
     * @return
     */
    @Override
    public JSONObject listForSlideShow(String userInfoId, String shareCode) {
        List<BannerRestDTO> list = homeWindowRestDao.listForSlideShow();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("slideShow",list);
        return jsonObject;
    }
}