package com.fnjz.front.controller.api.check;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.check.SystemParamCheckRestDTO;
import com.fnjz.front.service.api.checkrest.CheckRestServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: app启动检查类目更新,返回同步时间
 * @date 2018-06-26 13:11:13
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class CheckRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(CheckRestController.class);

    @Autowired
    private CheckRestServiceI checkRestServiceI;

    /**
     * 入参 系统支出/收入表最后更新时间/个人常用支出/收入表最后更新时间/个人排序关系表最后更新时间
     * @param type
     * @param systemParamCheckRestDTO
     * @return
     */
    @RequestMapping(value = "/checkChargeType/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkChargeType(@PathVariable("type") String type, HttpServletRequest request, @RequestBody SystemParamCheckRestDTO systemParamCheckRestDTO) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        if(systemParamCheckRestDTO.getSysIncomeTypeSynDate()==null && systemParamCheckRestDTO.getSysSpendTypeSynDate()==null && systemParamCheckRestDTO.getUserCommUseIncomeTypeSynDate()==null && systemParamCheckRestDTO.getUserCommUseSpendTypeSynDate()==null){
            //返回所有
            Map<String,Object> map = checkRestServiceI.getSysAndUserSpendAndSynInterval(userInfoId);
            return new ResultBean(ApiResultType.OK,map);
        }
        try {
            return null;
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/checkChargeType", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkChargeType(HttpServletRequest request, @RequestBody SystemParamCheckRestDTO systemParamCheckRestDTO) {
        return this.checkChargeType(null, request,systemParamCheckRestDTO);
    }
}
