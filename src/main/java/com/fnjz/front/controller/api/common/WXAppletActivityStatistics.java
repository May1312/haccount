package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序活动统计接口
 * Created by yhang on 2018/9/5.
 */
@Controller
@RequestMapping("/api/wxActivity/")
public class WXAppletActivityStatistics {

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    //老用户通过小游戏引导到小程序访问量统计
    @RequestMapping(value = "/statisticsOldVisitor", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean statisticsOldVisitor(HttpServletRequest request, @RequestParam String wxappletChannel) {
        String shareCode = (String) request.getAttribute("shareCode");
        //统计老用户访问量
        redisTemplateUtils.incrementOldVisitor(wxappletChannel, "sumOldVisitor");
        //统计老用户去重访问量
        redisTemplateUtils.addOldVisitorToSet(wxappletChannel, shareCode);
        return new ResultBean(ApiResultType.OK, null);
    }

    //新用户通过小游戏引导到小程序访问量统计
    @RequestMapping(value = "/statisticsNewVisitor", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsNewVisitor(@RequestBody Map<String, String> map) {
        String user = WXAppletUtils.getUser(map.get("code"));
        JSONObject jsonObject = JSONObject.parseObject(user);
        if (jsonObject.getString("errcode") != null) {
            return new ResultBean(ApiResultType.WXAPPLET_LOGIN_ERROR, null);
        } else {
            if (StringUtils.isNotEmpty(map.get("wxappletChannel"))) {
                String openid = jsonObject.getString("openid");
                //统计新用户访问量
                redisTemplateUtils.incrementNewVisitor(map.get("wxappletChannel"), "sumNewVisitor");
                //统计新用户去重访问量
                redisTemplateUtils.addNewVisitorToSet(map.get("wxappletChannel"), openid);
            }
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    //统计小游戏引导到小程序访问量统计
    @RequestMapping(value = "sum", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean teacherDay(HttpServletRequest request, @RequestParam String wxappletChannel) {
        //获取活动注册成功人数+老用户访问量总量+老用户有效访问量+新用户访问总量+新用户有效访问量
        //获取活动注册成功人数
        int sumNewRegister = redisTemplateUtils.getHashValue(wxappletChannel, "sumNewRegister");
        //老用户访问量总量
        int sumOldVisitor = redisTemplateUtils.getHashValue(wxappletChannel, "sumOldVisitor");
        //老用户有效访问量
        long oldVisitorSet = redisTemplateUtils.getSetSize(wxappletChannel, "oldVisitorSet");
        //新用户访问总量
        int sumNewVisitor = redisTemplateUtils.getHashValue(wxappletChannel, "sumNewVisitor");
        //新用户有效访问量
        long newVisitorSet = redisTemplateUtils.getSetSize(wxappletChannel, "newVisitorSet");
        Map<String, Object> map = new HashMap<>();
        map.put("活动注册成功人数", sumNewRegister);
        map.put("老用户访问总量", sumOldVisitor);
        map.put("老用户有效访问量", oldVisitorSet);
        map.put("新用户访问总量", sumNewVisitor);
        map.put("新用户有效访问量", newVisitorSet);
        return new ResultBean(ApiResultType.OK, map);
    }
}
