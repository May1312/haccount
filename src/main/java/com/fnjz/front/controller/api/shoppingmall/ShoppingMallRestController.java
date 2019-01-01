package com.fnjz.front.controller.api.shoppingmall;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsListRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangePhysicalRestDTO;
import com.fnjz.front.entity.api.userinfo.ConsigneeAddressRestDTO;
import com.fnjz.front.enums.LoginEnum;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import com.fnjz.front.service.api.userinfoaddfield.UserInfoAddFieldRestService;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.RedisLockUtils;
import com.fnjz.front.utils.newWeChat.WeChatUtils;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * 商城相关
 * Created by yhang on 2018/10/20.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class ShoppingMallRestController {

    private static final Logger logger = Logger.getLogger(ShoppingMallRestController.class);

    @Autowired
    private ShoppingMallRestService shoppingMallRestService;

    @Autowired
    private UserIntegralRestServiceI userIntegralRestServiceI;

    @Autowired
    private UserInfoAddFieldRestService userInfoAddFieldRestService;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WeChatUtils weChatUtils;

    /**
     * 获取所有商品
     *
     * @return
     */
    @RequestMapping(value = {"/goods", "/goods/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goods() {
        try {
            List<GoodsListRestDTO> list = shoppingMallRestService.getGoods();
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 查看用户当前是否存在兑换中商品
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/checkExchangeStatus", "/checkExchangeStatus/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkExchangeStatus(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            boolean flag = shoppingMallRestService.checkExchangeStatus(userInfoId);
            return new ResultBean(ApiResultType.OK, flag);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"/goodsInfo/{id}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsInfo(@PathVariable("id") Integer id) {
        try {
            GoodsInfoRestDTO goodsInfoRestDTO = shoppingMallRestService.getGoodsInfoById(id);
            return new ResultBean(ApiResultType.OK, goodsInfoRestDTO);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 积分兑换  map扩展加入地址信息
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = {"/toExchange/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toExchange(@PathVariable("type") String type, @RequestBody Map<String, String> map, HttpServletRequest request) {
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            boolean flag = shoppingMallRestService.checkExchangeStatus(userInfoId);
            if (flag) {
                return new ResultBean(ApiResultType.INTEGRAL_EXCHANGE_NOT_ALLOW2, null);
            }
            //根据商品id或消耗积分数+用户拥有总积分数
            GoodsRestEntity goodsRestEntity = shoppingMallRestService.getGoodsById(Integer.valueOf(map.get("goodsId")));
            //现金红包类型商品  校验手机号验证码
            if (goodsRestEntity.getGoodsType() == 3) {
                logger.info("红包兑换验证码："+JSON.toJSONString(map));
                //移动端校验验证码
                if (StringUtils.equals(type, "ios") || StringUtils.equals(type, "android")) {
                    ResultBean resultBean = cashCheck(map);
                    if (!StringUtils.equals(resultBean.getCode(), "200")) {
                        return resultBean;
                    }
                    //定义1  小程序  2 移动端
                    map.put("channel", "2");
                } else {
                    ResultBean resultBean = cashCheck(map);
                    if (!StringUtils.equals(resultBean.getCode(), "200")) {
                        return resultBean;
                    }
                    //定义1  小程序
                    map.put("channel", "1");
                }
            }
            //总积分数统计
            double integralTotal = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
            //判断用户积分数
            if (integralTotal < goodsRestEntity.getFengfengTicketValue()) {
                return new ResultBean(ApiResultType.INTEGRAL_EXCHANGE_NOT_ALLOW, null);
            }
            JSONObject jsonObject = shoppingMallRestService.toExchange(map, goodsRestEntity, userInfoId);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    private static final int TIMEOUT= 10*1000;

    @Autowired
    private RedisLockUtils redislock;
    /**
     * 现金红包类型商品
     */
    @Transactional
    public ResultBean cashCheck(Map<String, String> map) {
        ResultBean rb;
        if (StringUtils.isEmpty(map.get("exchangeMobile"))) {
            rb = new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
            return rb;
        }
        //校验验证码
        try {
            long time = System.currentTimeMillis()+TIMEOUT;
            if(!redislock.lock(map.get("exchangeMobile"),String.valueOf(time))){
                //throw new Exception(101,"换个姿势再试试")
                logger.info("索失败");
            }
            String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_CASH_MOBILE + map.get("exchangeMobile"));
            logger.info("redis红包兑换验证码："+code);
            rb = checkVerifycode(map, code);
            redislock.unlock(map.get("exchangeMobile"),String.valueOf(time));
            logger.info("释放锁");
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    private ResultBean checkVerifycode(Map<String, String> map, String code) {
        if (StringUtils.isEmpty(code)) {
            redisTemplateUtils.deleteKey(RedisPrefix.PREFIX_USER_VERIFYCODE_CASH_MOBILE + map.get("exchangeMobile"));
            return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
        }
        if (StringUtils.equals(code, map.get("verifyCode"))) {
            return new ResultBean(ApiResultType.OK, null);
        } else {
            return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
        }
    }

    /**
     * 积分兑换---->树鱼回调接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/soouucallback"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean soouucallback(HttpServletRequest request) {
        try {
            //获取到回调地址传回的参数
            String OrderNo = request.getParameter("OrderNo"); // 福禄订单号
            String ChargeTime = request.getParameter("ChargeTime"); // 交易完成时间
            String CustomerOrderNo = request.getParameter("CustomerOrderNo"); // 合作商家订单号
            String Status = request.getParameter("Status"); // 订单状态(成功,失败)
            String ReMark = request.getParameter("ReMark");
            logger.info("----------树鱼回调触发---------- 订单号:" + CustomerOrderNo + "  状态:" + Status);
            shoppingMallRestService.updateExchange(OrderNo, CustomerOrderNo, Status, ReMark);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 积分兑换---->历史兑换列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/historyIntegralExchange"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean historyIntegralExchange(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            List<ShoppingMallIntegralExchangePhysicalRestDTO> list = shoppingMallRestService.historyIntegralExchange(userInfoId);
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取收货地址
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/consigneeAddress/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean consigneeAddress(@PathVariable("type") String type, HttpServletRequest request) {
        logger.info("访问终端:" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            ConsigneeAddressRestDTO bean = userInfoAddFieldRestService.getConsigneeAddress(userInfoId);
            Map map = new HashMap();
            map.put("aaa", bean);
            System.out.println(JSON.toJSON(map).toString());
            return new ResultBean(ApiResultType.OK, bean);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * save or update 收货地址
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/consigneeAddress/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean consigneeAddress(@PathVariable("type") String type, HttpServletRequest request, @RequestBody ConsigneeAddressRestDTO bean) {
        logger.info("访问终端:" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            if (bean != null) {
                userInfoAddFieldRestService.updateConsigneeAddress(userInfoId, bean);
            }
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 红包类商品兑换 校验openid
     * 移动端-->微信提现至零钱
     * 小程序-->关注公众号 模板消息红包
     * 定义type   1:小程序openid   2:移动端openid   3:公众号openid
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/checkExistsOpenId/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkExistsOpenId(@PathVariable("type") String type, HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            Integer flag = userInfoAddFieldRestService.checkExistsOpenId(userInfoId, Integer.valueOf(type));
            return new ResultBean(ApiResultType.OK, flag);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 移动端上传code ---> 获取openid
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/uploadCode/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadCode(@PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, String> map) {
        logger.info("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkeLoginByWechat(map, LoginEnum.LOGIN_BY_WECHAT);
        //判断CODE
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        //判断是否已绑定openid
        Map<String, Object> map1 = userInfoAddFieldRestService.checkExistsOpenIdByUserInfoIdForWeChat(userInfoId, 2);
        //根据code解密 opendid
        JSONObject user = weChatUtils.getUser(map.get("code") + "");
        String openId = user.getString("openid");
        if (map1 != null) {
            if (map1.get("openid") == null) {
                try {
                    //保存openId
                    if (map1.get("id") != null) {
                        //已存在  更新
                        userInfoAddFieldRestService.updateOpenId(userInfoId, openId, Integer.valueOf(map1.get("id") + ""), 2);
                    } else {
                        //insert
                        userInfoAddFieldRestService.insertOpenId(userInfoId, openId, 2);
                    }
                    return new ResultBean(ApiResultType.OK, null);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            }
        }else{
            //insert
            userInfoAddFieldRestService.insertOpenId(userInfoId, openId, 2);
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    @RequestMapping(value = {"/consigneeAddress"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean consigneeAddress(HttpServletRequest request) {
        return this.consigneeAddress(null, request);
    }

    @RequestMapping(value = {"/consigneeAddress"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean consigneeAddress(HttpServletRequest request, @RequestBody ConsigneeAddressRestDTO bean) {
        return this.consigneeAddress(null, request, bean);
    }

    @RequestMapping(value = {"/toExchange"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toExchange(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return this.toExchange(null, map, request);
    }

    @RequestMapping(value = {"/checkExistsOpenId"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkExistsOpenId(HttpServletRequest request) {
        return this.checkExistsOpenId(null, request);
    }

    @RequestMapping(value = {"/uploadCode"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadCode(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.uploadCode(null, request, map);
    }
}
