package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.registerchannel.RegisterChannelRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 小程序活动统计接口
 * Created by yhang on 2018/9/5.
 */
@Controller
@RequestMapping("/api/wxActivity/")
public class WXAppletActivityStatistics {

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private RegisterChannelRestServiceI registerChannelRestServiceI;

    //老用户通过小游戏引导到小程序访问量统计
    @RequestMapping(value = "/statisticsOldVisitor", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean statisticsOldVisitor(HttpServletRequest request, @RequestParam String wxappletChannel) {
        String shareCode = (String) request.getAttribute("shareCode");
        //统计老用户访问量
        redisTemplateUtils.incrementOldVisitor(wxappletChannel, "sumOldVisitor");
        //统计老用户去重访问量
        redisTemplateUtils.addOldVisitorToSet(wxappletChannel, shareCode);
        //按天统计 老用户访问量+去重访问量
        statisticsByDay(wxappletChannel, shareCode, 1);
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
            String wxappletChannel = map.get("wxappletChannel");
            if (StringUtils.isNotEmpty(wxappletChannel)) {
                String openid = jsonObject.getString("openid");
                //统计新用户访问量
                redisTemplateUtils.incrementNewVisitor(wxappletChannel, "sumNewVisitor");
                //统计新用户去重访问量
                redisTemplateUtils.addNewVisitorToSet(wxappletChannel, openid);
                //按天统计 访问量+去重访问量
                statisticsByDay(wxappletChannel, openid, 2);
            }
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    private static int sumNewRegister2=0;
    //统计小游戏引导到小程序访问量统计
    @RequestMapping(value = "sum", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean teacherDay(@RequestParam String wxappletChannel) {
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
        Map<String, Object> map = new TreeMap<>();
        map.put("老用户访问总量", sumOldVisitor);
        map.put("老用户有效访问量", oldVisitorSet);
        map.put("新用户访问总量", sumNewVisitor);
        map.put("新用户有效访问量", newVisitorSet);
        map.put("微信授权用户人数", sumNewRegister);
        sumNewRegister2=sumNewRegister;
        //今日数据
        Map<String, Object> todayStatistics = getTodayStatistics(wxappletChannel, LocalDate.now(),sumNewRegister2);
        Map<String, Object> result = new TreeMap<>();
        result.put("日统计", todayStatistics);
        //累计数据
        Map<String, Object> totalStatistics = registerChannelRestServiceI.getTotalStatistics(wxappletChannel);
        Map<String, Object> total = new TreeMap<>();
        total.put("访问量统计", map);
        total.put("用户数据统计", totalStatistics);
        result.put("累加统计", total);
            return new ResultBean(ApiResultType.OK, result);
    }

    /**
     * 【今日新增数据】：5个数据，首次记账人数(查看当天是否为首次记账)、记账笔数分布（0.1.2.）3，邀请好友数、丰丰票数
     */
    private Map<String, Object> getTodayStatistics(String channel, LocalDate now,int sumNewRegister2) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
        Map<String, Object> total = new TreeMap<>(Comparator.reverseOrder());
        for (int i = 1; i <= 7; i++) {
            Map<String, Object> total2;
            now = now.minusDays(1);
            String time = now.format(formatters);
            //获取活动注册成功人数
            int sumNewRegister = redisTemplateUtils.getHashValue(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumNewRegister" + "_" + time, "sumNewRegister");
            //老用户访问量总量
            int sumOldVisitor = redisTemplateUtils.getHashValue(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumOldVisitor" + "_" + time, "sumOldVisitor");
            //老用户有效访问量
            long oldVisitorSet = redisTemplateUtils.getSetSize(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":oldVisitorSet" + "_" + time, "oldVisitorSet");
            //新用户访问总量
            int sumNewVisitor = redisTemplateUtils.getHashValue(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumNewVisitor" + "_" + time, "sumNewVisitor");
            //新用户有效访问量
            long newVisitorSet = redisTemplateUtils.getSetSize(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":newVisitorSet" + "_" + time, "newVisitorSet");
            Map<String, Object> map = new TreeMap<>();
            map.put("[" + now.toString() + "]微信授权用户人数", sumNewRegister);
            map.put("[" + now.toString() + "]新用户有效访问量", newVisitorSet);
            map.put("[" + now.toString() + "]新用户访问总量", sumNewVisitor);
            map.put("[" + now.toString() + "]老用户有效访问量", oldVisitorSet);
            map.put("[" + now.toString() + "]老用户访问总量", sumOldVisitor);
            //统计7天的数据
            //统计当天记账人数    记账笔数分布  0  未记账    1记账    2 记账两笔   3笔及以上
            total2 = registerChannelRestServiceI.getTodayStatistics(channel, now.toString(),sumNewRegister2);
            ((Map) total2.get("日新增数据")).put("[" + now.toString() + "]访问量", map);
            total.put("[" + now.toString() + "]日访问量统计", total2);
        }
        return total;
    }

    /**
     * 按天统计数据---->新用户访问量+新用户去重访问量
     *
     * @param flag 1 老用户   2 新用户
     */
    private void statisticsByDay(String channel, String openid, int flag) {
        //按日区分
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
        String time = LocalDate.now().format(formatters);
        if (1 == flag) {
            //统计老用户访问量
            redisTemplateUtils.incrementNewVisitor(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumOldVisitor" + "_" + time, "sumOldVisitor", 1);
            //统计老用户去重访问量
            redisTemplateUtils.addNewVisitorToSet(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":oldVisitorSet" + "_" + time, openid, 1);
        } else if (2 == flag) {
            //统计新用户访问量
            redisTemplateUtils.incrementNewVisitor(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":sumNewVisitor" + "_" + time, "sumNewVisitor", 1);
            //统计新用户去重访问量
            redisTemplateUtils.addNewVisitorToSet(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + ":" + channel + ":newVisitorSet" + "_" + time, openid, 1);
        }
    }
}
