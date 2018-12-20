package com.fnjz.front.controller.api.userwxqrcode;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userwxqrcode.UserWXQrCodeRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 小程序邀请码
 * @date 2018-10-12 20:43:39
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserWXQrCodeRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserWXQrCodeRestController.class);

    @Autowired
    private UserWXQrCodeRestServiceI userWXQrCodeServiceI;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取邀请小程序码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getInviteQrCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getInviteQrCode(HttpServletRequest request, @RequestParam(value = "type",required = false) String type) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (StringUtils.isEmpty(type)) {
            //爱钱鸭邀请码
            type = "1";
        }
        try {
            //判断用户小程序邀请码是否存在
            String url = userWXQrCodeServiceI.getInviteQrCode(userInfoId, type);
            return new ResultBean(ApiResultType.OK, url);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }
}
