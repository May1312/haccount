package com.fnjz.front.controller.api.offlinesynchronized;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**   
 * @Title: Controller
 * @Description: 离线同步记录表相关
 * @author zhangdaihao
 * @date 2018-08-29 14:34:55
 * @version V1.0   
 *
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class OfflineSynchronizedRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(OfflineSynchronizedRestController.class);

	@Autowired
	private OfflineSynchronizedRestServiceI offlineSynchronizedRestServiceI;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private RedisTemplateUtils redisTemplateUtils;

	/**
	 * 移动端pull同步
	 * @param type
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/offlinePull/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean offlinePull(@PathVariable("type") String type,HttpServletRequest request,@RequestBody Map<String,String> map) {
		System.out.println("登录终端：" + type);
		if(StringUtils.isEmpty(map.get("mobileDevice"))){
			return new ResultBean(ApiResultType.MY_PARAMS_ERROR,null);
		}
		try {
			String userInfoId = (String) request.getAttribute("userInfoId");
			Map<String,Object> pullData = offlineSynchronizedRestServiceI.offlinePull(map.get("mobileDevice"),map.get("isFirst"),userInfoId);
			return new ResultBean(ApiResultType.OK,pullData);
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR,null);
		}
	}

	/**
	 * 移动端push同步
	 * @param type
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/offlinePush/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean offlinePush(@PathVariable("type") String type,HttpServletRequest request,@RequestBody Map<String,Object> map) {
		System.out.println("登录终端：" + type);
		final String mobileDevice = map.get("mobileDevice")+"";
		if(StringUtils.isEmpty(mobileDevice)){
			return new ResultBean(ApiResultType.MY_PARAMS_ERROR,null);
		}
		try {
			final String userInfoId = (String) request.getAttribute("userInfoId");
			final String shareCode = (String) request.getAttribute("shareCode");
			if(map.get("synData")!=null){
				//校验同步时间
				if(null!=map.get("synDate")){
					Date latelySynDate = offlineSynchronizedRestServiceI.getLatelySynDate(mobileDevice, userInfoId);
					if(!StringUtils.equals(DateUtils.convert2StringAll(latelySynDate),DateUtils.convert2StringAll(Long.valueOf(map.get("synDate")+"")))){
						return new ResultBean(ApiResultType.SYN_DATE_IS_ERROR,null);
					}
				}
				//转json对象
				final List<WarterOrderRestEntity> list = JSONObject.parseArray(JSON.toJSONString(map.get("synData")),WarterOrderRestEntity.class);
				//异步处理插入
				taskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						//插入数据
						offlineSynchronizedRestServiceI.offlinePush(list,mobileDevice,userInfoId);
						//重置redis 记账总笔数 记账天数
						redisTemplateUtils.deleteHashKey(RedisPrefix.PREFIX_MY_COUNT + shareCode,"chargeTotal");
					}
				});
			}
			return new ResultBean(ApiResultType.OK,null);
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR,null);
		}
	}

	@RequestMapping(value = "/offlinePull", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean offlinePull(HttpServletRequest request,@RequestBody Map<String,String> map) {
		return this.offlinePull(null, request,map);
	}

	@RequestMapping(value = "/offlinePush", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean offlinePush(HttpServletRequest request,@RequestBody Map<String,Object> map) {
		return this.offlinePush(null, request,map);
	}
}
