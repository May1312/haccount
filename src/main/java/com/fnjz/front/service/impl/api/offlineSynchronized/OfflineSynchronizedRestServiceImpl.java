package com.fnjz.front.service.impl.api.offlineSynchronized;

import com.fnjz.front.dao.OfflineSynchronizedRestDao;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("offlinesynchronizedRestService")
@Transactional
public class OfflineSynchronizedRestServiceImpl extends CommonServiceImpl implements OfflineSynchronizedRestServiceI {

    @Autowired
    private OfflineSynchronizedRestDao offlineSynchronizedRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    /**
     * 获取最新同步时间
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    @Override
    public Date getLatelySynDate(String mobileDevice, String userInfoId) {
        return offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
    }

    /**
     * 移动端pull接口
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String,Object> offlinePull(String mobileDevice, String userInfoId) {
        Date latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        List<WarterOrderRestDTO> list = warterOrderRestDao.findAllWaterList(userInfoId, null);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("synData", list);
        map.put("synDate",latelySynDate);
        return map;
    }

    /**
     * 移动端push 插入记录
     * @param list
     * @param mobileDevice
     * @param userInfoId
     */
    @Override
    public void offlinePush(List<WarterOrderRestEntity> list, String mobileDevice, String userInfoId) {
        //生成本次同步记录
        offlineSynchronizedRestDao.insert(mobileDevice,userInfoId);
        for(WarterOrderRestEntity warter:list){
            //同步数据
            warterOrderRestDao.saveOrUpdateOfflineData(warter);
        }
    }
}