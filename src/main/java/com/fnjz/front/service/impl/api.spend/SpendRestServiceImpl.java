package com.fnjz.front.service.impl.api.spend;

import com.fnjz.front.dao.SpendRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.spend.SpendRestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.spend.SpendRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

import java.util.List;

@Service("spendRestService")
@Transactional
public class SpendRestServiceImpl extends CommonServiceImpl implements SpendRestServiceI {

    @Autowired
    private SpendRestDao spendRestDao;

    public PageRest findListForPage(String accountBookId,Integer curPage,Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<SpendRestEntity> listForPage = spendRestDao.findListForPage(accountBookId,curPage,pageRest.getStartIndex());
        //获取总条数
        Integer count = spendRestDao.getCount(accountBookId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }
}