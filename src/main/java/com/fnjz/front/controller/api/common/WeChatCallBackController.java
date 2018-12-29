package com.fnjz.front.controller.api.common;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 公众号回调code接口
 * Created by yhang on 2018/11/28.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class WeChatCallBackController {

    private static final Logger logger = Logger.getLogger(WeChatCallBackController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserInfoAddFieldRestService userInfoAddFieldRestService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 公众号  code回调
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/wechatcallback", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean uploadFormId(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        return null;
    }
}
