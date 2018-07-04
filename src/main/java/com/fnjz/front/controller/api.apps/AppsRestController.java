package com.fnjz.front.controller.api.apps;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.apps.AppsRestDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import com.fnjz.front.service.api.apps.AppsRestServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Map;
import springfox.documentation.annotations.ApiIgnore;

/**   
 * @Title: Controller
 * @Description: app版本管理表相关
 * @date 2018-06-26 13:11:13
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
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
	public ResultBean appCheck (@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
									  type,@RequestBody Map<String,String> map){
		System.out.println("登录终端：" + type);
		ResultBean rb = new ResultBean();
		Integer flag = null;
		if(StringUtils.isEmpty(type)){
			rb.setFailMsg(ApiResultType.SYSTEM_TYPE_IS_NULL);
			return rb;
		}
		if(StringUtils.equals("ios",type)){
			flag = 1;
		}
		if(StringUtils.equals("android",type)){
			flag = 0;
		}
		//判断版本号
		if (StringUtil.isEmpty(map.get("version"))) {
			rb.setFailMsg(ApiResultType.VERSION_IS_NULL);
			return rb;
		}
		try {
			if(flag!=null){
				AppsRestDTO appsRestDTO = appsRestService.appCheck(map.get("version"),flag);
				rb.setSucResult(ApiResultType.OK);
				rb.setResult(appsRestDTO);
				return rb;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			rb.setFailMsg(ApiResultType.SERVER_ERROR);
			return rb;
		}
		return rb;
	}

	@RequestMapping(value = "/appCheck", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean appCheck (@RequestBody @ApiIgnore Map<String, String> map){
		return this.appCheck(null, map);
	}
}
