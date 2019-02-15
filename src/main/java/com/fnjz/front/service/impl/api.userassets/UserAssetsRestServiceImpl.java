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
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
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
     *
     * @param userInfoId
     * @return
     */
    @Override
    public JSONObject getAssets(String userInfoId, String shareCode, String flag) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(flag)) {
            //定义 type 1为资产类 2为初始时间
            //查询资产
            List<UserAssetsRestDTO> assets = userAssetsRestDao.getAssetsAllForDTO(userInfoId, 1);
            //查询初始时间
            List<UserAssetsRestEntity> initDate = userAssetsRestDao.getAssetsAll(userInfoId, 2);
            UserAssetsRestEntity userAssetsRestEntity = new UserAssetsRestEntity();
            if (initDate.size() == 0) {
                //未设置初始时间 取用户注册时间开始计算
                String user = redisTemplateUtils.getForString(RedisPrefix.PREFIX_USER_LOGIN + shareCode);
                UserLoginRestEntity login = JSONObject.parseObject(user, UserLoginRestEntity.class);
                if (login != null) {
                    userAssetsRestEntity.setInitDate(login.getRegisterDate());
                    userAssetsRestDao.insertInitDate(login.getRegisterDate(), userInfoId);
                }
            } else {
                userAssetsRestEntity = initDate.get(0);
            }
            //统计记账收支 余额
            String chargeTotal = warterOrderRestDao.getTotalByDate(userAssetsRestEntity.getInitDate(), userInfoId);
            //统计资产 总额
            String assetsTotal = userAssetsRestDao.getAssetsTotal(userInfoId);
            jsonObject.put("assets", assets);
            BigDecimal charge = new BigDecimal(chargeTotal == null ? "0" : chargeTotal);
            BigDecimal asset = new BigDecimal(assetsTotal == null ? "0" : assetsTotal);
            jsonObject.put("netAssets", charge.add(asset));
            jsonObject.put("initDate", userAssetsRestEntity.getInitDate());
            return jsonObject;
        } else {
            //查询资产
            List<UserAssetsRestDTO> assets = userAssetsRestDao.getAssetsAllForDTO(userInfoId);
            jsonObject.put("assets", assets);
            return jsonObject;
        }
    }

    @Override
    public JSONObject getAssetsv2(String userInfoId, String shareCode, String flag) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(flag)&&StringUtils.isNotEmpty(userInfoId)) {
            //定义 type 1为资产类 2为初始时间
            //查询用户拥有资产
            List<UserAssetsRestDTO> assets = userAssetsRestDao.getAssetsAllForDTO(userInfoId, 1);
            //查询系统所有资产
            List<UserAssetsRestDTO> allAssets = userAssetsRestDao.getSYSAssetsAll();
            allAssets = getList(assets, allAssets);
            //查询初始时间
            /*List<UserAssetsRestEntity> initDate = userAssetsRestDao.getAssetsAll(userInfoId, 2);
            UserAssetsRestEntity userAssetsRestEntity = new UserAssetsRestEntity();
            if (initDate.size() == 0) {
                //未设置初始时间 取用户注册时间开始计算
                String user = redisTemplateUtils.getForString(RedisPrefix.PREFIX_USER_LOGIN + shareCode);
                UserLoginRestEntity login = JSONObject.parseObject(user, UserLoginRestEntity.class);
                if (login != null) {
                    userAssetsRestEntity.setInitDate(login.getRegisterDate());
                    userAssetsRestDao.insertInitDate(login.getRegisterDate(), userInfoId);
                }
            } else {
                userAssetsRestEntity = initDate.get(0);
            }*/
            //统计记账收支 余额
            //String chargeTotal = warterOrderRestDao.getTotalByDate(userAssetsRestEntity.getInitDate(), userInfoId);
            //统计资产 总额
            String assetsTotal = userAssetsRestDao.getAssetsTotal(userInfoId);
            jsonObject.put("assets", allAssets);
            //BigDecimal charge = new BigDecimal(chargeTotal == null ? "0" : chargeTotal);
            BigDecimal asset = new BigDecimal(assetsTotal == null ? "0" : assetsTotal);
            jsonObject.put("netAssets", asset);
            //jsonObject.put("initDate", userAssetsRestEntity.getInitDate());
            return jsonObject;
        } else {
            //查询资产
            List<UserAssetsRestDTO> assets = userAssetsRestDao.getAssetsAllForDTO(userInfoId);
            //查询系统所有资产
            List<UserAssetsRestDTO> allAssets = userAssetsRestDao.getSYSAssetsAll();
            allAssets = getList(assets, allAssets);
            jsonObject.put("assets", allAssets);
            return jsonObject;
        }
    }

    /**
     * list1 自有资产类
     * list2 系统资产类
     *
     * @param list1
     * @param list2
     * @return
     */
    private List<UserAssetsRestDTO> getList(List<UserAssetsRestDTO> list1, List<UserAssetsRestDTO> list2) {
        list1.forEach(v -> {
            if (list2.contains(v)) {
                //获取脚标
                int i = list2.indexOf(v);
                UserAssetsRestDTO userAssetsRestDTO = list2.get(i);
                userAssetsRestDTO.setAssetsName(v.getAssetsName());
                userAssetsRestDTO.setMark(v.getMark());
                userAssetsRestDTO.setMoney(v.getMoney());
                userAssetsRestDTO.setUpdateDate(v.getUpdateDate());
                userAssetsRestDTO.setCreateDate(v.getCreateDate());
                list2.set(i, userAssetsRestDTO);
            }
        });
        //排序 优先显示默认账户
        Collections.sort(list2, Comparator.comparing(UserAssetsRestDTO::getMark).reversed().thenComparing(UserAssetsRestDTO::getPriority));
        return list2;
    }

    /**
     * 设置/修改资产
     *
     * @param userInfoId
     * @param map
     */
    @Override
    public void saveOrUpdateAssets(String userInfoId, Map<String, Object> map) {
        int count = userAssetsRestDao.getAssetsByAssetsType(userInfoId, Integer.valueOf(map.get("assetsType") + ""));
        if (count < 1) {
            //insert
            userAssetsRestDao.insertAssets(userInfoId, Integer.valueOf(map.get("assetsType") + ""), new BigDecimal(map.get("money") + ""));
        } else {
            //update
            userAssetsRestDao.updateMoney(new BigDecimal(map.get("money") + ""), userInfoId, Integer.valueOf(map.get("assetsType") + ""));
        }
    }

    @Override
    public void saveOrUpdateAssetsv2(String userInfoId, Map<String, Object> map) {
        int count = userAssetsRestDao.getAssetsByAssetsType(userInfoId, Integer.valueOf(map.get("assetsType") + ""));
        if (count < 1) {
            //insert
            userAssetsRestDao.insertAssetsv2(userInfoId, Integer.valueOf(map.get("assetsType") + ""),map.get("assetsName") + "", new BigDecimal(map.get("money") + ""));
        } else {
            //update
            userAssetsRestDao.updateMoneyv2(new BigDecimal(map.get("money") + ""),map.get("assetsName") + "", userInfoId, Integer.valueOf(map.get("assetsType") + ""));
        }
    }

    /**
     * 修改初始时间
     *
     * @param userInfoId
     * @param map
     */
    @Override
    public void updateInitDate(String userInfoId, Map<String, Object> map) {
        if (StringUtils.contains(map.get("initDate") + "", "-")) {
            userAssetsRestDao.updateInitDate(map.get("initDate") + "", userInfoId);
        } else {
            Instant instant = Instant.ofEpochMilli(Long.valueOf(map.get("initDate") + ""));
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            userAssetsRestDao.updateInitDate(localDateTime.toLocalDate().toString(), userInfoId);
        }
    }

    @Override
    public void addAT2Mark(String userInfoId, Map<String, Object> map) {
        JSONArray arrays = JSONArray.fromObject(map.get("ats"));
        arrays.forEach(v -> {
            int count = userAssetsRestDao.getAssetsByAssetsType(userInfoId, Integer.valueOf(v + ""));
            if (count < 1) {
                //insert
                userAssetsRestDao.addAT2Mark(userInfoId, v + "");
            } else {
                //update
                userAssetsRestDao.updateAT2Mark(userInfoId, v + "", 1);
            }
        });
    }

    @Override
    public void deleteAT2Mark(String userInfoId, Map<String, Object> map) {
        JSONArray arrays = JSONArray.fromObject(map.get("ats"));
        arrays.forEach(v -> {
            userAssetsRestDao.updateAT2Mark(userInfoId, v + "", 0);
        });
    }
}