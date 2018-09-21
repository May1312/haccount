package com.fnjz.front.service.impl.api.offlineSynchronized;

import com.fnjz.front.dao.OfflineSynchronizedRestDao;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.offlineSynchronized.SynDateRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        SynDateRestDTO latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        return latelySynDate.getSynDate();
    }

    /**
     * 移动端pull接口
     * @param mobileDevice
     * @param userInfoId
     * @return
     */
    @Override
    public Map<String,Object> offlinePull(String mobileDevice, String isFirst,String userInfoId) {
        SynDateRestDTO latelySynDate = offlineSynchronizedRestDao.getLatelySynDate(mobileDevice, userInfoId);
        //第一次同步  为null情况下 获取当前时间戳为同步时间
        Date date = new Date();
        Map<String,Object> map = new HashMap<String,Object>();
        if(latelySynDate.getSynDate()==null){
            offlineSynchronizedRestDao.firstInsert(mobileDevice,userInfoId,date);
            map.put("synDate",date);
        }else{
            map.put("synDate",latelySynDate.getSynDate());
        }
        List<WarterOrderRestEntity> list;
        //判断 isFirst标识是否为true, true 获取所有
        if(Boolean.valueOf(isFirst)){
            list = warterOrderRestDao.findAllWaterListOfNoDel(userInfoId, null);
        }else{
            list = warterOrderRestDao.findAllWaterList(userInfoId, latelySynDate.getSynDate());
        }
        map.put("synData", list);
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
        if(list.size()>0){
            for(WarterOrderRestEntity warter:list){
                //同步数据
                warterOrderRestDao.saveOrUpdateOfflineData(warter);
            }
        }
    }
}