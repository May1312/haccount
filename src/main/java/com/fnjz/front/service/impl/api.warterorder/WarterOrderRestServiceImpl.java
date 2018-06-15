package com.fnjz.front.service.impl.api.warterorder;

import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.util.List;

@Service("warterOrderRestService")
@Transactional
public class WarterOrderRestServiceImpl extends CommonServiceImpl implements WarterOrderRestServiceI {

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Override
    public PageRest findListForPage(String time, String accountBookId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<WarterOrderRestEntity> listForPage = warterOrderRestDao.findListForPage(time,accountBookId,pageRest.getStartIndex(),pageSize);
        //获取总条数
        Integer count = warterOrderRestDao.getCount(time,accountBookId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }

    @Override
    public Integer update(WarterOrderRestEntity charge) {
        return  warterOrderRestDao.update(charge);
    }

    @Override
    public Integer deleteOrder(String orderId, String userInfoId, String code) {
        int i = commonDao.updateBySqlString("UPDATE `hbird_account`.`hbird_water_order` SET `delflag` = " + 1 + " , `del_date` = NOW(), `update_by` = "+userInfoId+", `update_name` = "+code+" WHERE `id` = '" + orderId + "';");
        return i;
    }
}