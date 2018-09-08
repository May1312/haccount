package com.fnjz.front.service.api.checkrest;

import com.fnjz.front.entity.api.check.SystemParamCheckRestDTO;

import java.util.Map;

/**
 * Created by yhang on 2018/8/31.
 */
public interface CheckRestServiceI {
    /**
     * 获取系统类目/用户常用类目/类目排序关系接口
     * @return
     */
    Map<String,Object> getSysAndUserSpendAndSynInterval(String userInfoId,String accountBookId);

    /**
     * 根据版本号校验是否更新
     * @param systemParamCheckRestDTO
     * @return
     */
    Map<String,Object> checkParamVersion(SystemParamCheckRestDTO systemParamCheckRestDTO,String accountBookId,String userInfoId);

    Map<String,Object> checkParamVersion2(String shareCode,SystemParamCheckRestDTO systemParamCheckRestDTO,String accountBookId,String userInfoId);

    Map<String,Object> getSysAndUserSpendAndSynInterval2(String shareCode,String userInfoId,String accountBookId);
}
