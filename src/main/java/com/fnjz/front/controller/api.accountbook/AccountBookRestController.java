package com.fnjz.front.controller.api.accountbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.service.api.accountbook.AccountBookRestServiceI;
import com.fnjz.front.utils.ShareCodeUtil;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 账本表相关
 * @date 2018-05-30 14:08:15
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class AccountBookRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AccountBookRestController.class);

    @Autowired
    private AccountBookRestServiceI accountBookRestService;

    /**
     * 获取 账本id对应成员数关系
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkABMembers/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkABMembers(@PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            JSONArray jsonArray = accountBookRestService.checkABMembers(userInfoId);
            return new ResultBean(ApiResultType.OK, jsonArray);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 首页获取当前账本对应成员信息
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/getABMembers/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getABMembers(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        if(abId==null){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            //创建者  当前用户   其他组员需要区分
            JSONObject jsonObject = accountBookRestService.getABMembers(abId, userInfoId);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取用户所拥有账本
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/getABAll/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getABAll(@PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            List<AccountBookRestDTO> list = accountBookRestService.getABAll(userInfoId);
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取用户所拥有账本
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteAB/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteAB(@PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, Integer> map) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            accountBookRestService.deleteAB(map.get("abId"), userInfoId);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 创建账本
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/createAB/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean createAB(@PathVariable("type") String type, HttpServletRequest request, @RequestBody AccountBookRestEntity accountBookRestEntity) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            accountBookRestEntity.setCreateBy(Integer.valueOf(userInfoId));
            int abId = accountBookRestService.createAB(accountBookRestEntity);
            return new ResultBean(ApiResultType.OK, abId);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 更新账本名称
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateAB/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateAB(@PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, Object> map) {
        System.out.println("登录终端：" + type);
        if (StringUtils.isEmpty(map.get("abName") + "") || StringUtils.isEmpty(map.get("abId") + "")) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        try {
            accountBookRestService.updateAB(map.get("abName") + "", map.get("abId") + "");
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 成员管理页 数据获取
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/membersInfo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean membersInfo(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        if(abId==null){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            //创建者  当前用户   其他组员需要区分
            JSONObject jsonObject = accountBookRestService.membersInfo(abId, userInfoId);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 成员管理页 删除成员
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteMembers/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteMembers(@PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,Object> map) {
        System.out.println("登录终端：" + type);
        if(map==null){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        if(map.get("memberIds")==null || map.get("abId")==null){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            accountBookRestService.deleteMembers(map, userInfoId);

            //移除流程执行成功之后发送消息推送
            new Thread() {
                @Override
                public void run() {
                    accountBookRestService.removeTheNotification(map,userInfoId);
                }
            }.start();

            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }


    @RequestMapping(value = "/checkABMembers", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkABMembers(HttpServletRequest request) {
        return this.checkABMembers(null, request);
    }

    @RequestMapping(value = "/getABMembers", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getABMembers(HttpServletRequest request, @RequestParam Integer abId) {
        return this.getABMembers(null, request, abId);
    }

    @RequestMapping(value = "/getABAll", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getABAll(HttpServletRequest request) {
        return this.getABAll(null, request);
    }

    @RequestMapping(value = "/deleteAB", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteAB(HttpServletRequest request, @RequestBody Map<String, Integer> map) {
        return this.deleteAB(null, request, map);
    }

    @RequestMapping(value = "/createAB", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean createAB(HttpServletRequest request, @RequestBody AccountBookRestEntity accountBookRestEntity) {
        return this.createAB(null, request, accountBookRestEntity);
    }

    @RequestMapping(value = "/updateAB", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateAB(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        return this.updateAB(null, request, map);
    }

    @RequestMapping(value = "/membersInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean membersInfo(HttpServletRequest request, @RequestParam(required = false) Integer abId) {
        return this.membersInfo(null, request, abId);
    }

    @RequestMapping(value = "/deleteMembers", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteMembers(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        return this.deleteMembers(null, request, map);
    }

    /**
     * 功能描述:同意加入账本调用
     *
     * @param:  账本id，通用userinid
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 15:37
     */
    @RequestMapping(value = "/acceptInvitationToAccount/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean acceptInvitationToAccount(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,Object> map) {
        //账本创建者id
        String adminUserFFid =String.valueOf(map.get("adminUserFFid"));
        //账本id
        String accountBookId =String.valueOf(map.get("accountBookId"));
        if (StringUtils.isEmpty(adminUserFFid) || StringUtils.isEmpty(accountBookId)){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,"检查账本创建者id，账本id");
        }
        int adminUserInfoId = ShareCodeUtil.sharecode2id(adminUserFFid);
        //当前登录用户id
        String userinfoId = (String) request.getAttribute("userInfoId");
        JSONObject jsonObject = accountBookRestService.invitationToAccount(String.valueOf(adminUserInfoId), accountBookId, userinfoId);
        return new ResultBean(ApiResultType.OK,jsonObject);
    }
    /**
     * 功能描述: 获取当前账本是否满员
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 16:41
     */
    @RequestMapping(value = "/accountBooksIsFull/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean accountBooksIsFull(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,HttpServletRequest request, @RequestBody Map<String,Object> map) {
        String accountBookId1 = String.valueOf(map.get("accountBookId"));
        if (StringUtils.isEmpty(accountBookId1)){
            return new ResultBean(ApiResultType.SERVER_ERROR,"去检查accountBookId");
        }
        Integer accountBookId = accountBookRestService.getAccountNumber(accountBookId1);
        if (accountBookId<5){
            return new ResultBean(ApiResultType.OK,"true");
        }else {
            return new ResultBean(ApiResultType.OK,"false");
        }
    }
}
