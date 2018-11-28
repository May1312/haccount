package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletPushUtils;
import com.fnjz.front.utils.WXAppletUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    private WXAppletPushUtils wxAppletPushUtils;

    @RequestMapping(value = "/uploadFormId", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkGestureType(HttpServletRequest request, @RequestBody Map<String,String> map) {
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            //根据code解密 opendid
            String code = WXAppletUtils.getUser(map.get("code"));
            JSONObject user = JSONObject.parseObject(code);
            if (user.getString("errcode") != null) {
                return new ResultBean(ApiResultType.WXAPPLET_LOGIN_ERROR, null);
            }else{
                String opendId = user.getString("openid");
                //判断是否已绑定openid
                userInfoAddFieldRestService.checkExists(userInfoId,opendId);
                //将formid存入redis   以openid为key
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_PUSH+opendId,map.get("formId"));
                //发送测试
                wxAppletPushUtils.wxappletPush(null,opendId,map.get("formId")+"");
                return new ResultBean(ApiResultType.OK,null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

}
