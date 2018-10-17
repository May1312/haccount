package com.fnjz.front.service.impl.api.offlineSynchronized;

import com.fnjz.front.dao.OfflineSynchronizedRestDao;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.offlineSynchronized.SynDateRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service("offlinesynchronizedRestService")
@Transactional
public class OfflineSynchronizedRestServiceImpl extends CommonServiceImpl implements OfflineSynchronizedRestServiceI {

    @Autowired
    private OfflineSynchronizedRestDao offlineSynchronizedRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private CreateTokenUtils createTokenUtils;

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
        if(list!=null){
            if(list.size()>0){
                //list排序  正序
                list.sort(Comparator.naturalOrder());
                if(LocalDateTime.ofInstant( list.get(list.size()-1).getCreateDate().toInstant(), ZoneId.systemDefault()).toLocalDate().isEqual(LocalDate.now())){
                    //引入新手任务 判断当前时间是否为
                    createTokenUtils.integralTask(userInfoId, CategoryOfBehaviorEnum.TodayTask, AcquisitionModeEnum.Write_down_an_account);
                }
                for(WarterOrderRestEntity warter:list){
                    //同步数据
                    warterOrderRestDao.saveOrUpdateOfflineData(warter);
                }
            }
        }
    }

    @Test
    public void run(){
        String a = "[{\"accountBookId\":3237,\"chargeDate\":1539598142000,\"createBy\":6145,\"createDate\":1539655319000,\"delflag\":0,\"id\":\"64082620-927e-4b0f-874b-3e1dd66b594d\",\"isStaged\":1,\"money\":5,\"orderType\":1,\"remark\":\"洗澡\",\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f8b9390163fc36f2010027\",\"typeName\":\"日用\",\"typePname\":\"购物\",\"updateDate\":1539598142000},{\"accountBookId\":3237,\"chargeDate\":1539598159000,\"createBy\":6145,\"createDate\":1539598159000,\"delflag\":0,\"id\":\"6c336608-b2b8-471f-85dc-0c88f6e09737\",\"isStaged\":1,\"money\":7,\"orderType\":1,\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83d33320016\",\"typeName\":\"饮食\",\"typePname\":\"饮食\",\"updateDate\":1539598159000},{\"accountBookId\":3237,\"chargeDate\":1539598175000,\"createBy\":6145,\"createDate\":1539598175000,\"delflag\":0,\"id\":\"6b749d45-7399-4e12-86fa-6c112429c1d2\",\"isStaged\":1,\"money\":7.5,\"orderType\":1,\"remark\":\"橘子\",\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83deeba0018\",\"typeName\":\"水果\",\"typePname\":\"饮食\",\"updateDate\":1539598175000},{\"accountBookId\":3237,\"chargeDate\":1539655319000,\"createBy\":6145,\"createDate\":1539598142000,\"delflag\":0,\"id\":\"fae70596-acc9-4e46-83b7-dd6de2514455\",\"isStaged\":1,\"money\":3,\"orderType\":1,\"spendHappiness\":-1,\"typeId\":\"2c91dbe363f81ded0163f83d33320016\",\"typeName\":\"饮食\",\"typePname\":\"饮食\",\"updateDate\":1539655319000}]";
        List<WarterOrderRestEntity> ts = com.alibaba.fastjson.JSONArray.parseArray(a, WarterOrderRestEntity.class);
        System.out.println(Arrays.toString(ts.toArray()));
        ts.sort(Comparator.naturalOrder());
        System.out.println(Arrays.toString(ts.toArray()));
    }
}