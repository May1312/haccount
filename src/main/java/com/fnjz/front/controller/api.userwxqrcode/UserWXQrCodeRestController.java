package com.fnjz.front.controller.api.userwxqrcode;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.DomainEnum;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userwxqrcode.UserWXQrCodeRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.WXAppletUtils;
import com.fnjz.utils.upload.QiNiuUploadFileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**   
 * @Title: Controller
 * @Description: 小程序邀请码
 * @author zhangdaihao
 * @date 2018-10-12 20:43:39
 * @version V1.0   
 *
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

	/**
	 * 获取邀请小程序码
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getInviteQrCode", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getInviteQrCode(HttpServletRequest request) {
		String shareCode = (String) request.getAttribute("shareCode");
		String userInfoId = (String) request.getAttribute("userInfoId");
		try {
			//判断用户小程序邀请码是否存在
			String url = userWXQrCodeServiceI.getInviteQrCode(userInfoId);
			if(StringUtils.isNotEmpty(url)){
				return new ResultBean(ApiResultType.OK,url);
			}else{
				String accessToken = WXAppletUtils.getAccessToken();
				JSONObject jsonObject = JSONObject.parseObject(accessToken);
				if (jsonObject.getString("errcode")==null) {
					byte[] result = WXAppletUtils.getWXACode(jsonObject.getString("access_token"),shareCode);
					//上传七牛云
					url = new QiNiuUploadFileUtils().bytesUpload(DomainEnum.WXAPPLET_QR_CODE_DOMAIN.getDomainUrl(),result,DomainEnum.WXAPPLET_QR_CODE_DOMAIN.getDomainName(),"wxqrcode_"+ CommonUtils.getAccountOrder());
					userWXQrCodeServiceI.insert(userInfoId,url);
					return new ResultBean(ApiResultType.OK,url);
				} else {
					return new ResultBean(ApiResultType.SERVER_ERROR, null);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR, null);
		}
	}
}
