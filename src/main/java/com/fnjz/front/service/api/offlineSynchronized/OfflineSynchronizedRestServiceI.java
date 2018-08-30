package com.fnjz.front.service.api.offlineSynchronized;

import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OfflineSynchronizedRestServiceI extends CommonService{

    /**
     * 获取最新同步时间
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    Date getLatelySynDate(String mobileDevice, String userInfoId);
    /**
     * 移动端pull接口
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    Map<String,Object> offlinePull(String mobileDevice, String userInfoId);

    /**
     * 移动端push 插入记录
     * @param list
     * @param mobileDevice
     * @param userInfoId
     */
    void offlinePush(List<WarterOrderRestEntity> list, String mobileDevice, String userInfoId);
}
