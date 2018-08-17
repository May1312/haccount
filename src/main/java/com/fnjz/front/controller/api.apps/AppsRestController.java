package com.fnjz.front.controller.api.apps;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.apps.AppsRestDTO;
import com.fnjz.front.service.api.apps.AppsRestServiceI;
import com.fnjz.front.utils.ParamValidateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: app版本管理表相关
 * @date 2018-06-26 13:11:13
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class AppsRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AppsRestController.class);

    @Autowired
    private AppsRestServiceI appsRestService;

    @ApiOperation(value = "app检查更新")
    @RequestMapping(value = "/appCheck/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean appCheck(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
                                       type, @RequestBody Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkApp(map, type);
        Integer flag = null;
        if (StringUtils.equals("ios", type)) {
            flag = 1;
        }
        if (StringUtils.equals("android", type)) {
            flag = 0;
        }
        try {
            if (flag != null) {
                AppsRestDTO appsRestDTO = appsRestService.appCheck(map.get("version"), flag);
                return new ResultBean(ApiResultType.OK, appsRestDTO);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
        return rb;
    }

    @RequestMapping(value = "/appCheck", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean appCheck(@RequestBody @ApiIgnore Map<String, String> map) {
        return this.appCheck(null, map);
    }
}
