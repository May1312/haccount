package com.fnjz.front.service.impl.api.checkrest;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.dao.SystemParamRestDao;
import com.fnjz.front.dao.SystemTypeRestDao;
import com.fnjz.front.dao.UserCommUseTypeOfflineCheckRestDao;
import com.fnjz.front.entity.api.check.SystemParamCheckRestDTO;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.systemparam.SystemParamRestEntity;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.entity.api.usercommusetypeofflinecheck.UserCommUseTypeOfflineCheckRestEntity;
import com.fnjz.front.service.api.checkrest.CheckRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * app启动检查类目更新情况
 * Created by yhang on 2018/8/31.
 */
@Service("CheckRestServiceI")
@Transactional
public class CheckRestServiceImpl implements CheckRestServiceI {

    //系统类目表
    @Autowired
    private SystemTypeRestDao systemTypeRestDao;
    //系统参数检查表
    @Autowired
    private SystemParamRestDao systemParamRestDao;
    //用户常用类目/排序关系检查表
    @Autowired
    private UserCommUseTypeOfflineCheckRestDao userCommUseTypeOfflineCheckRestDao;

    private static final String SPEND_TYPE = "spend_type";
    private static final String INCOME_TYPE = "income_type";
    private static final String TYPE_PRIORITY = "type_priority";

    /**
     * 获取系统类目/用户常用类目/类目排序关系接口
     *
     * @return
     */
    @Override
    public Map<String, Object> getSysAndUserSpendAndSynInterval(String userInfoId, String accountBookId) {
        //获取系统支出表
        List<SpendTypeRestEntity> allSysSpendType = systemTypeRestDao.getAllSysSpendType();
        //获取系统收入表
        List<IncomeTypeRestEntity> allSysIncomeType = systemTypeRestDao.getAllSysIncomeType();
        Map<String, Object> map = new HashMap();
        //不需要查询用户常用类目情况
        if (StringUtils.isNotEmpty(userInfoId)) {
            //获取用户常用支出类目
            List<UserCommUseSpendRestEntity> allUserCommUseSpendType = systemTypeRestDao.getAllUserCommUseSpendType(userInfoId);
            //获取用户常用收入类目
            List<UserCommUseIncomeRestEntity> allUserCommUseIncomeType = systemTypeRestDao.getAllUserCommUseIncomeType(userInfoId);
            //获取用户常用类目排序关系
            List<UserCommTypePriorityRestEntity> allUserCommUseTypePriority = systemTypeRestDao.getAllUserCommUseTypePriority(userInfoId);
            //获取离线-用户常用类目/排序关系检查表数据
            List<UserCommUseTypeOfflineCheckRestEntity> userCommUseTypeOfflineCheck = userCommUseTypeOfflineCheckRestDao.getUserCommUseTypeOfflineCheck(accountBookId);
            //第一次调用追加用户个 人常用版本
            if (userCommUseTypeOfflineCheck.size() == 0) {
                //个人常用类目打版本标签
                userCommUseTypeOfflineCheckRestDao.insert(accountBookId, SPEND_TYPE);
                JSONObject jo = new JSONObject();
                jo.put("allUserCommUseSpendTypeArrays", allUserCommUseSpendType);
                jo.put("version", "v1");
                map.put("allUserCommUseSpendType", jo);
                //个人常用类目打版本标签
                JSONObject jo2 = new JSONObject();
                jo2.put("allUserCommUseIncomeTypeArrays", allUserCommUseIncomeType);
                jo2.put("version", "v1");
                map.put("allUserCommUseIncomeType", jo2);
                userCommUseTypeOfflineCheckRestDao.insert(accountBookId, INCOME_TYPE);
                //个人常用类目打版本标签
                JSONObject jo3 = new JSONObject();
                jo3.put("allUserCommUseTypePriorityArrays", allUserCommUseTypePriority);
                jo3.put("version", "v1");
                map.put("allUserCommUseTypePriority", jo3);
                userCommUseTypeOfflineCheckRestDao.insert(accountBookId, TYPE_PRIORITY);
            } else {
                //更换终端   即参数全为null情况  但是并不是第一次查询  表中已存在版本
                if (userCommUseTypeOfflineCheck.size() > 0) {
                    //遍历离线-用户数据集合
                    for (UserCommUseTypeOfflineCheckRestEntity entity : userCommUseTypeOfflineCheck) {
                        if (StringUtils.equals(entity.getType(), "spend_type")) {
                            JSONObject userCommUseSpendType = new JSONObject();
                            userCommUseSpendType.put("version", entity.getVersion());
                            userCommUseSpendType.put("allUserCommUseSpendTypeArrays", allUserCommUseSpendType);
                            map.put("allUserCommUseSpendType", userCommUseSpendType);
                        } else if (StringUtils.equals(entity.getType(), "income_type")) {
                            JSONObject userCommUseIncomeType = new JSONObject();
                            userCommUseIncomeType.put("version", entity.getVersion());
                            userCommUseIncomeType.put("allUserCommUseIncomeTypeArrays", allUserCommUseIncomeType);
                            map.put("allUserCommUseIncomeType", userCommUseIncomeType);
                        } else if (StringUtils.equals(entity.getType(), "type_priority")) {
                            JSONObject userCommUseTypePriority = new JSONObject();
                            userCommUseTypePriority.put("version", entity.getVersion());
                            userCommUseTypePriority.put("allUserCommUseTypePriorityArrays", allUserCommUseTypePriority);
                            map.put("allUserCommUseTypePriority", userCommUseTypePriority);
                        }
                    }
                }

            }
        }

        //获取系统参数检查表数据
        List<SystemParamRestEntity> systemParam = systemParamRestDao.getSystemParam();
        if (systemParam.size() > 0) {
            for (SystemParamRestEntity systemParamRestEntity : systemParam) {
                if (StringUtils.equals(systemParamRestEntity.getParamType(), "spend_type")) {
                    JSONObject jo = new JSONObject();
                    jo.put("allSysSpendTypeArrays", allSysSpendType);
                    jo.put("version", systemParamRestEntity.getVersion());
                    map.put("allSysSpendType", jo);
                } else if (StringUtils.equals(systemParamRestEntity.getParamType(), "income_type")) {
                    JSONObject jo = new JSONObject();
                    jo.put("allSysIncomeTypeArrays", allSysIncomeType);
                    jo.put("version", systemParamRestEntity.getVersion());
                    map.put("allSysIncomeType", jo);
                } else if (StringUtils.equals(systemParamRestEntity.getParamType(), "syn_interval")) {
                    //追加同步时间间隔
                    map.put("synInterval", systemParamRestEntity.getVersion());
                }
            }

        }
        if (StringUtils.isNotEmpty(userInfoId)) {
            //添加 userinfoid accountbookid
            map.put("userInfo", userInfoId);
            map.put("accountBookId", accountBookId);
        }
        return map;
    }

    @Override
    public Map<String, Object> checkParamVersion(SystemParamCheckRestDTO systemParamCheckRestDTO, String accountBookId, String userInfoId) {
        //获取系统参数检查表数据
        List<SystemParamRestEntity> systemParam = systemParamRestDao.getSystemParam();
        Map<String, Object> map = new HashMap();
        if (StringUtils.isNotEmpty(accountBookId)) {
            //获取离线-用户常用类目/排序关系检查表数据
            List<UserCommUseTypeOfflineCheckRestEntity> userCommUseTypeOfflineCheck = userCommUseTypeOfflineCheckRestDao.getUserCommUseTypeOfflineCheck(accountBookId);
            //获取离线-用户常用类目/排序关系 支出是否更新标识
            JSONObject userCommUseSpendType = new JSONObject();
            userCommUseSpendType.put("flag", false);
            //获取离线-用户常用类目/排序关系 收入是否更新标识
            JSONObject userCommUseIncomeType = new JSONObject();
            userCommUseIncomeType.put("flag", false);
            //获取离线-用户常用类目/排序关系 排序关系是否更新标识
            JSONObject userCommUseTypePriority = new JSONObject();
            userCommUseTypePriority.put("flag", false);
            if (userCommUseTypeOfflineCheck.size() > 0) {
                //遍历离线-用户数据集合
                for (UserCommUseTypeOfflineCheckRestEntity entity : userCommUseTypeOfflineCheck) {
                    if (StringUtils.equals(entity.getType(), "spend_type")) {
                        //比对支出版本
                        if (StringUtils.isNotEmpty(systemParamCheckRestDTO.getUserCommUseSpendTypeVersion())) {
                            boolean result = (entity.getVersion().compareTo(systemParamCheckRestDTO.getUserCommUseSpendTypeVersion())) > 0 ? true : false;
                            userCommUseSpendType.put("flag", result);
                            userCommUseSpendType.put("version", entity.getVersion());
                        }
                    } else if (StringUtils.equals(entity.getType(), "income_type")) {
                        //比对收入版本
                        if (StringUtils.isNotEmpty(systemParamCheckRestDTO.getUserCommUseIncomeTypeVersion())) {
                            boolean result = (entity.getVersion().compareTo(systemParamCheckRestDTO.getUserCommUseIncomeTypeVersion())) > 0 ? true : false;
                            userCommUseIncomeType.put("flag", result);
                            userCommUseSpendType.put("version", entity.getVersion());
                        }
                    } else if (StringUtils.equals(entity.getType(), "type_priority")) {
                        //比对类目排序版本
                        if (StringUtils.isNotEmpty(systemParamCheckRestDTO.getUserCommTypePriorityVersion())) {
                            boolean result = (entity.getVersion().compareTo(systemParamCheckRestDTO.getUserCommTypePriorityVersion())) > 0 ? true : false;
                            userCommUseTypePriority.put("flag", result);
                            userCommUseTypePriority.put("version", entity.getVersion());
                        }
                    }
                }
            }
            //离线-用户常用支出类目需要更新
            if (userCommUseSpendType.getBoolean("flag")) {
                //获取离线-用户常用支出类目
                List<UserCommUseSpendRestEntity> allUserCommUseSpendType = systemTypeRestDao.getAllUserCommUseSpendType(userInfoId);
                userCommUseSpendType.remove("flag");
                userCommUseSpendType.put("allUserCommUseSpendTypeArrays", allUserCommUseSpendType);
                map.put("allUserCommUseSpendType", userCommUseSpendType);
            }
            //离线-用户常用收入类目需要更新
            if (userCommUseIncomeType.getBoolean("flag")) {
                //获取离线-用户常用收入类目
                List<UserCommUseIncomeRestEntity> allUserCommUseIncomeType = systemTypeRestDao.getAllUserCommUseIncomeType(userInfoId);
                userCommUseIncomeType.remove("flag");
                userCommUseIncomeType.put("allUserCommUseIncomeTypeArrays", allUserCommUseIncomeType);
                map.put("allUserCommUseIncomeType", userCommUseIncomeType);
            }
            //离线-用户常用类目排序需要更新
            if (userCommUseTypePriority.getBoolean("flag")) {
                //获取离线-用户常用类目排序
                List<UserCommTypePriorityRestEntity> allUserCommUseTypePriority = systemTypeRestDao.getAllUserCommUseTypePriority(userInfoId);
                userCommUseTypePriority.remove("flag");
                userCommUseTypePriority.put("allUserCommUseTypePriorityArrays", allUserCommUseTypePriority);
                map.put("allUserCommUseTypePriority", userCommUseTypePriority);
            }
        }
        //系统支出是否更新标识
        JSONObject sysSpendTypeVersion = new JSONObject();
        sysSpendTypeVersion.put("flag", false);
        //系统收入是否更新标识
        JSONObject sysIncomeTypeVersion = new JSONObject();
        sysIncomeTypeVersion.put("flag", false);
        //遍历系统参数集合
        if (systemParam.size() > 0) {
            //遍历系统参数集合
            for (SystemParamRestEntity entity : systemParam) {
                if (StringUtils.equals(entity.getParamType(), "spend_type")) {
                    //比对支出版本
                    if (StringUtils.isNotEmpty(systemParamCheckRestDTO.getSysSpendTypeVersion())) {
                        boolean result = (entity.getVersion().compareTo(systemParamCheckRestDTO.getSysSpendTypeVersion())) > 0 ? true : false;
                        sysSpendTypeVersion.put("flag", result);
                        sysSpendTypeVersion.put("version", entity.getVersion());
                    }
                } else if (StringUtils.equals(entity.getParamType(), "income_type")) {
                    //比对收入版本
                    if (StringUtils.isNotEmpty(systemParamCheckRestDTO.getSysIncomeTypeVersion())) {
                        boolean result = (entity.getVersion().compareTo(systemParamCheckRestDTO.getSysIncomeTypeVersion())) > 0 ? true : false;
                        sysIncomeTypeVersion.put("flag", result);
                        sysIncomeTypeVersion.put("version", entity.getVersion());
                    }
                } else if (StringUtils.equals(entity.getParamType(), "syn_interval")) {
                    //追加同步时间间隔
                    map.put("synInterval", entity.getVersion());
                }
            }
        }

        //系统支出类目需更新
        if (sysSpendTypeVersion.getBoolean("flag")) {
            //获取系统支出表
            List<SpendTypeRestEntity> allSysSpendType = systemTypeRestDao.getAllSysSpendType();
            sysSpendTypeVersion.remove("flag");
            sysSpendTypeVersion.put("allSysSpendTypeArrays", allSysSpendType);
            map.put("allSysSpendType", sysSpendTypeVersion);
        }
        //系统收入类目需要更新
        if (sysIncomeTypeVersion.getBoolean("flag")) {
            //获取系统收入表
            List<IncomeTypeRestEntity> allSysIncomeType = systemTypeRestDao.getAllSysIncomeType();
            sysIncomeTypeVersion.remove("flag");
            sysIncomeTypeVersion.put("allSysIncomeTypeArrays", allSysIncomeType);
            map.put("allSysIncomeType", sysIncomeTypeVersion);
        }
        if (StringUtils.isNotEmpty(accountBookId)) {
            //添加 userinfoid accountbookid
            map.put("userInfo", userInfoId);
            map.put("accountBookId", accountBookId);
        }
        //数据处理完成
        return map;
    }
}
