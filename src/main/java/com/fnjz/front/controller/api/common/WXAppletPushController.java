package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserInfoAddFieldRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.newWeChat.WXAppletPushUtils;
import com.fnjz.front.utils.newWeChat.WXAppletUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 小程序端push 服务通知相关
 * Created by yhang on 2018/11/28.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class WXAppletPushController {

    private static final Logger logger = Logger.getLogger(WXAppletPushController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserInfoAddFieldRestService userInfoAddFieldRestService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private WXAppletUtils wxAppletUtils;

    /**
     * 上传formId
     *
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/uploadFormId", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadFormId(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        taskExecutor.execute(() -> {
            //判断是否已绑定openid
            Map<String, Object> map1 = userInfoAddFieldRestService.checkExists(userInfoId);
            String openId = null;
            if (map1 != null) {
                if (map1.get("openid") == null) {
                    //根据code解密 opendid
                    String code = wxAppletUtils.getUser(map.get("code") + "");
                    JSONObject user = JSONObject.parseObject(code);
                    if (user.get("errcode") != null) {
                        logger.error("/uploadFormId   ----code解密异常-----");
                    } else {
                        openId = user.getString("openid");
                        //保存openId
                        if (map1.get("id") != null) {
                            //已存在  更新
                            userInfoAddFieldRestService.updateOpenId(userInfoId, openId, Integer.valueOf(map1.get("id") + ""), 1);
                        } else {
                            //insert
                            userInfoAddFieldRestService.insertOpenId(userInfoId, openId, 1);
                        }
                    }
                } else {
                    openId = (String) map1.get("openid");
                }
            } else {
                //根据code解密 opendid
                String code = wxAppletUtils.getUser(map.get("code") + "");
                JSONObject user = JSONObject.parseObject(code);
                if (user.getString("errcode") != null) {
                    logger.error("/uploadFormId   ----code解密异常-----");
                } else {
                    openId = user.getString("openid");
                    //insert
                    userInfoAddFieldRestService.insertOpenId(userInfoId, openId, 1);
                }
            }
            if (StringUtils.isNotEmpty(openId)) {
                //将formid存入redis   按日区分
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
                String time = date.format(formatters);
                List<String> arrays = (List<String>) map.get("formIds");
                if (arrays != null) {
                    if (arrays.size() > 0) {
                        boolean status = redisTemplateUtils.hasKey(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "_" + time);
                        if (!status) {
                            //当天首次上传
                            redisTemplateUtils.setListRight(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "_" + time, arrays, 1, 7L);
                        } else {
                            redisTemplateUtils.setListRight(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "_" + time, arrays, 2, null);
                        }
                        //cache openId   以user_info_id 为key
                        redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID + userInfoId, openId, 7L);
                    }
                }
            }
        });
        return new ResultBean(ApiResultType.OK, null);
    }

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;
    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;
    @Autowired
    private UserIntegralRestDao userIntegralRestDao;
    /**
     * 七天内记账提醒通知  测试方法
     * 以当前日期为终点  入参日期为范围
     * @return
     */
    @RequestMapping(value = "/notifyToCharge", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean notifyToCharge(@RequestParam String beginDate, @RequestParam String endDate) {
        int total = 0;
        int success = 0;
        LocalDate begin = LocalDate.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime begin2 = begin.atTime(0, 0, 0);
        LocalDateTime end2 = end.atTime(23,59,59);
        //获取符合条件用户
        List<Map<String,Integer>> list = userIntegralRestDao.getNotifyToChargeUsers(begin2.toString(),end2.toString());
        if(list!=null){
            total = list.size();
            if(list.size()>0){
                success += (int) list.stream().filter(v -> send(v.get("id") + "") == 1).count();
            }
        }
        Map<String,Object> map = new HashMap();
        map.put("totalUsers",total);
        map.put("successUsers",success);
        map.put("totalUsersId",Arrays.toString(list.toArray()));
        return new ResultBean(ApiResultType.OK,map);
    }

    private int send(String userInfoId) {
        String openId = userInfoAddFieldRestDao.getByUserInfoId(userInfoId);
        if (StringUtils.isNotEmpty(openId)) {
            //获取formId
            Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
            if (keys.size() > 0) {
                Object[] arrays = keys.toArray();
                Arrays.sort(arrays, Collections.reverseOrder());
                String formId = (String) redisTemplateUtils.popListRight(arrays[0] + "");
                WXAppletMessageBean bean = new WXAppletMessageBean();
                //Map<String, Object> map = userInfoRestDao.getNKAndAUById(inviteUserInfoId);
                //设置好友昵称
                //bean.getKeyword1().put("value", map.get("nickname") == null ? "蜂鸟用户" : map.get("nickname") + "");
                //设置邀请时间
                bean.getKeyword2().put("value", LocalDate.now().toString());
                //bean.getKeyword3().put("value", fengFengTicket.getBehaviorTicketValue() + "积分（价值0.4元）");
                //设置已邀请人数
                //int inviteUsers = userInviteRestDao.getCountForInvitedUsers(userInfoId + "");
                //bean.getKeyword4().put("value", inviteUsers + "人");
                //温馨提示
                bean.getKeyword5().put("value", "hi，亲爱的蜂鸟小伙伴，本周你记账了几次呢？点击进入小程序，快来整理本周的账目吧！");
                return wxAppletPushUtils.wxappletPush(WXAppletPushUtils.inviteFriendId, openId, formId, WXAppletPushUtils.inviteFriendPage, bean);
            }
        }
        return 0;
    }
}
