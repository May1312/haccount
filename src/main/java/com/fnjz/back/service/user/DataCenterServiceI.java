package com.fnjz.back.service.user;

import com.fnjz.back.entity.user.ChannelBehaviorEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/9/17 15:39
 * @Description:
 */
public interface DataCenterServiceI extends CommonService {
    public List<ChannelBehaviorEntity> queryListByPage(int page, int pageSize, String registerStartDate, String registerendDate, String userId, String dowmloadChannel, String sort, String order);
    public Long getCount(String registerStartDate, String registerendDate, String userId, String dowmloadChannel);
}
