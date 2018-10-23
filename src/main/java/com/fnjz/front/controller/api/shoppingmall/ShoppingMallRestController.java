package com.fnjz.front.controller.api.shoppingmall;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ShoppingMallIntegralExchangeRestDTO;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    //TODO 总积分数  统计不合理  待修改！！！！！！
    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    /**
     * 获取所有商品
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/goods", "/goods/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goods(HttpServletRequest request) {
        try {
            List<GoodsRestDTO> list = shoppingMallRestService.getGoods();
            return new ResultBean(ApiResultType.OK, list);
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
     * 积分兑换
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = {"/toExchange"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toExchange(@RequestBody Map<String,String> map,HttpServletRequest request) {
        try {
            String userInfoId = (String)request.getAttribute("userInfoId");
            //根据商品id或消耗积分数+用户拥有总积分数
            GoodsRestEntity goodsRestEntity = shoppingMallRestService.getGoodsById(Integer.valueOf(map.get("goodsId")));
            //总积分数统计
            int integralTotal = userIntegralRestDao.getTotalIntegral(userInfoId);
            //判断用户积分数
            if(integralTotal<goodsRestEntity.getFengfengTicketValue()){
                return new ResultBean(ApiResultType.INTEGRAL_EXCHANGE_NOT_ALLOW,null);
            }
            JSONObject jsonObject = shoppingMallRestService.toExchange(map.get("exchangeMobile"),goodsRestEntity,userInfoId);
            return new ResultBean(ApiResultType.OK,jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 积分兑换---->树鱼回调接口
     * @param request
     * @return
     */
    @RequestMapping(value = {"/soouucallback"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean soouucallback(HttpServletRequest request) {
        logger.info("----------树鱼回调触发----------");
        try {
            //获取到回调地址传回的参数
            String OrderNo = request.getParameter("OrderNo"); // 福禄订单号
            String ChargeTime = request.getParameter("ChargeTime"); // 交易完成时间
            String CustomerOrderNo = request.getParameter("CustomerOrderNo"); // 合作商家订单号
            String Status = request.getParameter("Status"); // 订单状态(成功,失败)
            String ReMark = request.getParameter("ReMark");
            logger.info("----------树鱼回调触发---------- 订单号:"+CustomerOrderNo+"  状态:"+Status);
            shoppingMallRestService.updateExchange(OrderNo,CustomerOrderNo,Status,ReMark);
            return new ResultBean(ApiResultType.OK,null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 积分兑换---->历史兑换列表
     * @param request
     * @return
     */
    @RequestMapping(value = {"/historyIntegralExchange"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean historyIntegralExchange(HttpServletRequest request) {
        String userInfoId = (String)request.getAttribute("userInfoId");
        try {
            List<ShoppingMallIntegralExchangeRestDTO> list = shoppingMallRestService.historyIntegralExchange(userInfoId);
            return new ResultBean(ApiResultType.OK,list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }
}
