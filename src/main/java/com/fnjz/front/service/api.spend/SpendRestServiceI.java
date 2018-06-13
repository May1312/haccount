package com.fnjz.front.service.api.spend;

import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.spend.SpendRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

public interface SpendRestServiceI extends CommonService{

    PageRest findListForPage(String accountBookId, Integer curpage, Integer itemPerPage);
}
