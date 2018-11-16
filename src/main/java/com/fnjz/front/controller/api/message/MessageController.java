package com.fnjz.front.controller.api.message;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.jeecgframework.core.common.controller.BaseController;
import com.fnjz.front.service.api.message.MessageServiceI;

import java.util.ArrayList;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 消息通知
 * @date 2018-11-12 13:50:14
 */
@RestController
@RequestMapping(RedisPrefix.BASE_URL)
public class MessageController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(MessageController.class);

    @Autowired
    private MessageServiceI messageService;

    /**
     * 消息通知列表
     *
     * @return
     */
    @RequestMapping(value = "/message/messageList", method = RequestMethod.GET)
    public ResultBean messageList(String userId, Integer page, Integer rows) {
        if (StringUtils.isEmpty(userId)) {
            return new ResultBean(ApiResultType.SERVER_ERROR, "userId must not be null");
        }
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 10;
        }
        MessageVo messageList = messageService.getMessageList(userId, page, rows);
        return new ResultBean(ApiResultType.OK, messageList);
    }

    /**
     * 功能描述: 更新消息状态
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/12 15:14
     */
    @RequestMapping(value = "/message/updateMessageStatus")
    public ResultBean updateMessageStatus(@RequestBody JSONObject jsonObject) {
        String messageUpdateType = jsonObject.getString("messageUpdateType");
        String userinfoId = jsonObject.getString("userinfoId");
        String messageId = jsonObject.getString("messageId");
        if (StringUtils.isEmpty(messageUpdateType)){
            return new ResultBean(ApiResultType.SERVER_ERROR, "messageUpdateType must not be null");
        }
        Integer i = messageService.updateMessageStatus(userinfoId, messageId, messageUpdateType);
        if (i > 0) {
            return new ResultBean(ApiResultType.OK, "更新成功");
        }
        return new ResultBean(ApiResultType.OK, "更新失败检测userId,或者messageId");
    }

}
