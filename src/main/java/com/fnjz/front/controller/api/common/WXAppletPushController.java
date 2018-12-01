package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
            //根据code解密 opendid
            String code = WXAppletUtils.getUser(map.get("code") + "");
            JSONObject user = JSONObject.parseObject(code);
            if (user.getString("errcode") != null) {
                logger.error("/uploadFormId   ----code解密异常-----");
            } else {
                String opendId = user.getString("openid");
                //判断是否已绑定openid
                userInfoAddFieldRestService.checkExists(userInfoId, opendId);
                //将formid存入redis   按日区分
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
                String time = date.format(formatters);
                List<String> arrays = (List<String>) map.get("formIds");
                boolean status = redisTemplateUtils.hasKey(RedisPrefix.PREFIX_WXAPPLET_PUSH + opendId + "_" + time);
                if (!status) {
                    //当天首次上传
                    redisTemplateUtils.setListRight(RedisPrefix.PREFIX_WXAPPLET_PUSH + opendId + "_" + time, arrays, 1);
                } else {
                    redisTemplateUtils.setListRight(RedisPrefix.PREFIX_WXAPPLET_PUSH + opendId + "_" + time, arrays, 2);
                }
                //cache openId   以user_info_id 为key
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID + userInfoId, opendId, 7L);
            }
        });
        return new ResultBean(ApiResultType.OK, null);
    }
}
