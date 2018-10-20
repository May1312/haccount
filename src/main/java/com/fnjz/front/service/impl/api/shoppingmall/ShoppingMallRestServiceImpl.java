package com.fnjz.front.service.impl.api.shoppingmall;

import com.fnjz.front.dao.ShoppingMallRestDao;
import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import com.fnjz.front.service.api.shoppingmall.ShoppingMallRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yhang on 2018/10/20.
 */
@Service("shoppingMallRestService")
@Transactional
public class ShoppingMallRestServiceImpl implements ShoppingMallRestService {

    @Autowired
    private ShoppingMallRestDao shoppingMallRestDao;

    /**
     * 获取可用商品
     * @return
     */
    @Override
    public List<GoodsRestDTO> getGoods() {
        return shoppingMallRestDao.getGoods();
    }

    /**
     * 根据id获取商品详情
     * @param id
     * @return
     */
    @Override
    public GoodsInfoRestDTO getGoodsInfoById(Integer id) {
        return shoppingMallRestDao.getGoodsInfoById(id);
    }
}
