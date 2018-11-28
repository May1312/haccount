package com.fnjz.front.service.impl.api.userassets;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserAssetsRestDao;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.userassets.UserAssetsRestDTO;
import com.fnjz.front.entity.api.userassets.UserAssetsRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userassets.UserAssetsRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service("userAssetsRestService")
@Transactional
public class UserAssetsRestServiceImpl extends CommonServiceImpl implements UserAssetsRestServiceI {

    @Autowired
    private UserAssetsRestDao userAssetsRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    /**
     * 获取用户资产
     * @param userInfoId
     * @return
     */
    @Override
    public JSONObject getAssets(String userInfoId,String shareCode) {
        //定义 type 1为资产类 2为初始时间
        //查询资产
        List<UserAssetsRestDTO> assets = userAssetsRestDao.getAssetsAllForDTO(userInfoId,1);
        //查询初始时间
        List<UserAssetsRestEntity> initDate = userAssetsRestDao.getAssetsAll(userInfoId, 2);
        UserAssetsRestEntity userAssetsRestEntity = new UserAssetsRestEntity();
        if(initDate.size()==0){
            //未设置初始时间 取用户注册时间开始计算
            String user = redisTemplateUtils.getForString(RedisPrefix.PREFIX_USER_LOGIN + shareCode);
            UserLoginRestEntity login = JSONObject.parseObject(user,UserLoginRestEntity.class);
            if(login!=null){
                userAssetsRestEntity.setInitDate(login.getRegisterDate());
                userAssetsRestDao.insertInitDate(login.getRegisterDate(),userInfoId);
            }
        }else{
            userAssetsRestEntity=initDate.get(0);
        }

        //统计记账收支 余额
        String chargeTotal = warterOrderRestDao.getTotalByDate(userAssetsRestEntity.getInitDate(),userInfoId);
        //统计资产 总额
        String assetsTotal = userAssetsRestDao.getAssetsTotal(userInfoId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("assets",assets);
        BigDecimal charge = new BigDecimal (chargeTotal==null?"0":chargeTotal);
        BigDecimal asset = new BigDecimal (assetsTotal==null?"0":assetsTotal);
        jsonObject.put("netAssets",charge.add(asset));
        jsonObject.put("initDate",userAssetsRestEntity.getInitDate());
        return jsonObject;
    }

    /**
     * 设置/修改资产
     * @param userInfoId
     * @param map
     */
    @Override
    public void saveOrUpdateAssets(String userInfoId, Map<String,Object> map) {
        int count = userAssetsRestDao.getAssetsByAssetsType(userInfoId,Integer.valueOf(map.get("assetsType")+""));
        if(count<1){
            //insert
            userAssetsRestDao.insertAssets(userInfoId,Integer.valueOf(map.get("assetsType")+""),new BigDecimal(map.get("money")+""));
        }else {
            //update
            userAssetsRestDao.updateMoney(new BigDecimal(map.get("money")+""),userInfoId,Integer.valueOf(map.get("assetsType")+""));
        }
    }

    /**
     * 修改初始时间
     * @param userInfoId
     * @param map
     */
    @Override
    public void updateInitDate(String userInfoId, Map<String, Object> map) {
        if(StringUtils.contains(map.get("initDate")+"","-")){
            userAssetsRestDao.updateInitDate(map.get("initDate")+"",userInfoId);
        }else{
            Instant instant = Instant.ofEpochMilli(Long.valueOf(map.get("initDate")+""));
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            userAssetsRestDao.updateInitDate(localDateTime.toLocalDate().toString(),userInfoId);
        }
    }
}