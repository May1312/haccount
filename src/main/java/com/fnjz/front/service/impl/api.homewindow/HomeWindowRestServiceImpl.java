package com.fnjz.front.service.impl.api.homewindow;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.HomeWindowRestDao;
import com.fnjz.front.entity.api.homewindow.HomeWindowRestEntity;
import com.fnjz.front.service.api.homewindow.HomeWindowRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.google.gson.JsonObject;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userInfoId
     * @param shareCode
     * @return
     */
    @Override
    public JsonObject listForWindow(String userInfoId, String shareCode) {
        //获取用户弹框读取情况
        String cacheActivity = redisTemplateUtils.getForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode);

        /*if(forHash.size()==0){
            //未读取
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pushToUser",1);
            //定义1 未读   2已读
            jsonObject.put("hasRead",1);

            jsonObject.put("activityId",1);
        }else{

        }*/
        List<HomeWindowRestEntity> list = homeWindowRestDao.listForWindow();
        //判断读取情况   id匹配
        return null;
    }
}