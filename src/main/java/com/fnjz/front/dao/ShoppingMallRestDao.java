package com.fnjz.front.dao;

import com.fnjz.front.entity.api.goods.GoodsInfoRestDTO;
import com.fnjz.front.entity.api.goods.GoodsRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/10/20.
 */
@MiniDao
public interface ShoppingMallRestDao {
    /**
     * 获取可用商品
     * @return
     */
    @ResultType(GoodsRestDTO.class)
    @Sql("select * from hbird_goods where status=1")
    List<GoodsRestDTO> getGoods();

    @ResultType(GoodsInfoRestDTO.class)
    @Sql("select * from hbird_goods where id=:id")
    GoodsInfoRestDTO getGoodsInfoById(@Param("id") Integer id);
}
