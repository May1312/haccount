package com.fnjz.front.controller.api.usercommtypepriority;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.jeecgframework.core.common.controller.BaseController;
import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**   
 * @Title: Controller
 * @Description: 用户所属类目排序表相关
 * @author zhangdaihao
 * @date 2018-06-21 15:47:15
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
public class UserCommTypePriorityRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserCommTypePriorityRestController.class);

	@Autowired
	private UserCommTypePriorityRestServiceI userCommTypePriorityRestService;

	@ApiOperation(value = "上传/修改用户所属类目排序关系")
	@RequestMapping(value = "/uploadUserTypePriority/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadUserTypePriority(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request,@RequestBody Map<String,Object> map) {
		ResultBean rb = new ResultBean();
		if(StringUtils.isEmpty(map.get("type")+"")){
			rb.setFailMsg(ApiResultType.TYPE_IS_NULL);
			return rb;
		}
		if(map.get("relation")==null){
			rb.setFailMsg(ApiResultType.TYPE_RELATION_IS_NULL);
			return rb;
		}
		try {
			String userInfoId = (String) request.getAttribute("userInfoId");
			UserCommTypePriorityRestEntity userCommTypePriorityRestEntity = new UserCommTypePriorityRestEntity();
			userCommTypePriorityRestEntity.setType(Integer.valueOf(map.get("type")+""));
			userCommTypePriorityRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
			JSONArray relation1 = JSONArray.fromObject((ArrayList) map.get("relation"));
			userCommTypePriorityRestEntity.setRelation(relation1.toString());
			//判断是否已存在  TODO 业务逻辑应该放到service层处理
			String hql = "from UserCommTypePriorityRestEntity where userInfoId = "+ userInfoId +" AND type = "+ userCommTypePriorityRestEntity.getType()+"";
			UserCommTypePriorityRestEntity o = userCommTypePriorityRestService.singleResult(hql);
			if(o!=null){
				String sql = "UPDATE `hbird_account`.`hbird_user_comm_type_priority` SET `type` = "+userCommTypePriorityRestEntity.getType()+", `relation` = '"+userCommTypePriorityRestEntity.getRelation()+"', `update_date` = NOW() WHERE `id` = "+o.getId()+";";
				userCommTypePriorityRestService.updateBySqlString(sql);
			}else{
				userCommTypePriorityRestEntity.setCreateDate(new Date());
				userCommTypePriorityRestService.saveOrUpdate(userCommTypePriorityRestEntity);
			}
			rb.setSucResult(ApiResultType.OK);
			return rb;
		} catch (Exception e) {
			logger.error(e.toString());
			rb.setFailMsg(ApiResultType.SERVER_ERROR);
			return rb;
		}
	}

	@RequestMapping(value = "/uploadUserTypePriority", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadUserTypePriority(HttpServletRequest request,@RequestBody Map<String,Object> map) {
		return this.uploadUserTypePriority(null,request,map);
	}
}
