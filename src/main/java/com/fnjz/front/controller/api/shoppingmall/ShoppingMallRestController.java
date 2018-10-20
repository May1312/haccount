package com.fnjz.front.controller.api.shoppingmall;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 获取所有商品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/goods", "/goods/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goods(HttpServletRequest request) {
        try {
            List<GoodsRestDTO> list = shoppingMallRestService.getGoods();
            return new ResultBean(ApiResultType.OK,list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/goodsInfo/{id}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsInfo(@PathVariable("id") Integer id) {
        try {
            GoodsInfoRestDTO goodsInfoRestDTO = shoppingMallRestService.getGoodsInfoById(id);
            return new ResultBean(ApiResultType.OK,goodsInfoRestDTO);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }
}
