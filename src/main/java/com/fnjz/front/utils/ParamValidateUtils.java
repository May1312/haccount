package com.fnjz.front.utils;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.statistics.StatisticsParamsRestDTO;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.enums.LoginEnum;
import com.fnjz.front.enums.StatisticsEnum;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * controller层参数校验
 * Created by yhang on 2018/7/11.
 */
public class ParamValidateUtils {

    /**
     * 账号密码/验证码登录/修改手机号校验
     *
     * @param map
     * @return
     */
    public static ResultBean checkeLongin(Map<String, String> map, LoginEnum login) {
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
        }
        //密码登录校验密码
        if (login.getIndex() == 1) {
            if (StringUtils.isEmpty(map.get("password"))) {
                return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
            }
        }
        //验证码登录校验验证码
        else if (login.getIndex() == 2) {
            if (StringUtils.isEmpty(map.get("verifycode"))) {
                return new ResultBean(ApiResultType.VERIFYCODE_IS_NULL, null);
            }
        }
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR, null);
        }
        return null;
    }

    /**
     * 验证手机号格式
     *
     * @param map
     * @return
     */
    public static ResultBean checkeMobile(Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_IS_NULL, null);
        }

        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR, null);
        }
        return null;
    }

    /**
     * 微信/小程序登录校验
     *
     * @param map
     * @param login
     * @return
     */
    public static ResultBean checkeLoginByWechat(Map<String, String> map, LoginEnum login) {
        if (login.getIndex() == 3) {
            if (StringUtils.isEmpty(map.get("code"))) {
                return new ResultBean(ApiResultType.WECHAT_CODE_ISNULL, null);
            }
        } else if (login.getIndex() == 4) {
            if (StringUtils.isEmpty(map.get("code"))) {
                return new ResultBean(ApiResultType.WXAPPLET_CODE_ISNULL, null);
            }
        }
        return null;
    }

    /**
     * 小程序注册校验
     *
     * @param map
     * @return
     */
    public static ResultBean checkRegisterByWXApplet(Map<String, String> map) {
        //encryptedData 加密数据
        if (StringUtils.isEmpty(map.get("encryptedData"))) {
            return new ResultBean(ApiResultType.encryptedData_IS_NULL, null);
        }
        if (StringUtils.isEmpty(map.get("iv"))) {
            return new ResultBean(ApiResultType.IV_IS_NULL, null);
        }
        if (StringUtils.isEmpty(map.get("key"))) {
            return new ResultBean(ApiResultType.KEY_IS_NULL, null);
        }
        return null;
    }

    /**
     * 重置密码/微信绑定手机号/手机号注册 校验
     *
     * @param map
     * @return
     */
    public static ResultBean checkResetpwd(Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
        }

        if (StringUtils.isEmpty(map.get("password"))) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
        }

        if (StringUtils.isEmpty(map.get("verifycode"))) {
            return new ResultBean(ApiResultType.VERIFYCODE_IS_NULL, null);
        }
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR, null);
        }
        return null;
    }

    /**
     * 修改密码校验
     *
     * @param map
     * @return
     */
    public static ResultBean checkUpdatepwd(Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("oldpwd"))) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        if (StringUtils.isEmpty(map.get("newpwd"))) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        return null;
    }

    /**
     * 记账对象校验
     *
     * @param charge
     * @return
     */
    public static ResultBean checkToCharge(WarterOrderRestEntity charge) {
        //校验记账时间
        if (charge.getChargeDate() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_SPENDDATE_ERROR, null);
        }
        //校验金额
        if (charge.getMoney() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_MONEY_IS_NULL, null);
        }
        /*if (!ValidateUtils.checkDecimal(charge.getMoney() + "")) {
            return new ResultBean(ApiResultType.ACCOUNT_MONEY_ERROR, null);
        }*/
        //判断支出收入类型
        if (charge.getOrderType() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_TYPE_ERROR, null);
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(charge.getTypePid())) {
            return new ResultBean(ApiResultType.ACCOUNT_PARAMS_ERROR, null);
        }
        if (StringUtils.isEmpty(charge.getTypeId())) {
            return new ResultBean(ApiResultType.ACCOUNT_PARAMS_ERROR, null);
        }
        if (charge.getOrderType() == 1) {
            //支出类型判断即时和分期类型
            if (charge.getIsStaged() == null) {
                return new ResultBean(ApiResultType.ACCOUNT_TYPE_ERROR, null);
            }
        }
        return null;
    }

    /**
     * 手机号注册参数校验
     * @param userInfo
     * @param map
     * @return
     */
    public static UserInfoRestEntity checkRegisterParams(UserInfoRestEntity userInfo, Map<String, String> map) {

        //设置手机号
        userInfo.setMobile(map.get("mobile"));
        //设置密码
        userInfo.setPassword(map.get("password"));

        if (StringUtils.isNotEmpty(map.get("mobileSystem"))) {
            //终端系统
            userInfo.setMobileSystem(map.get("mobileSystem"));
        }
        if (StringUtils.isNotEmpty(map.get("mobileSystemVersion"))) {
            //系统版本号
            userInfo.setMobileSystemVersion(map.get("mobileSystemVersion"));
        }
        if (StringUtils.isNotEmpty(map.get("mobileManufacturer"))) {
            //终端厂商
            userInfo.setMobileManufacturer(map.get("mobileManufacturer"));
        }
        if (StringUtils.isNotEmpty(map.get("mobileDevice"))) {
            //终端设备号
            userInfo.setMobileDevice(map.get("mobileDevice"));
        }
        return userInfo;
    }

    /**
     * 统计参数校验
     * @param statisticsParamsRestDTO
     * @return
     */
    public static ResultBean checkStatistics(StatisticsParamsRestDTO statisticsParamsRestDTO,StatisticsEnum statistics){
        if (StringUtils.isEmpty(statisticsParamsRestDTO.getFlag())) {
            return new ResultBean(ApiResultType.TYPE_FLAG_IS_NULL,null);
        }
        if(StringUtils.equals(statistics.getIndex(),StatisticsEnum.STATISTICS_FOR_TOP.getIndex())){
            //排行榜统计接口
            if(StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(),statisticsParamsRestDTO.getFlag())){
                //日统计
                if (statisticsParamsRestDTO.getDayTime() == null) {
                    return new ResultBean(ApiResultType.TIME_IS_NULL,null);
                }
            }
            if(StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(),statisticsParamsRestDTO.getFlag()) || StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(),statisticsParamsRestDTO.getFlag())){
                //周统计
                if (StringUtils.isEmpty(statisticsParamsRestDTO.getTime())) {
                    return new ResultBean(ApiResultType.TIME_IS_NULL,null);
                }
            }
        }
        return null;
    }

    /**
     * 手势密码参数校验
     * @param map
     * @return
     */
    public static ResultBean checkGesture(Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("gesturePwType"))) {
            return new ResultBean(ApiResultType.GESTURE_PARAMS_ERROR,null);
        }
        if (map.get("gesturePwType").length() > 1) {
            return new ResultBean(ApiResultType.GESTURE_PARAMS_LENGTH_ERROR,null);
        }
        return null;
    }

    /**
     * 验证码校验
     * @param map
     * @return
     */
    public static ResultBean checkVerifycode(Map<String, String> map,String code) {
        if (StringUtils.isEmpty(code)) {
            return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT,null);
        }
        if (StringUtils.equals(code, map.get("verifycode"))) {
            return new ResultBean(ApiResultType.OK,null);
        } else {
            return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR,null);
        }
    }

    /**
     * app检查更新校验
     * @param map
     * @param type
     * @return
     */
    public static ResultBean checkApp(Map<String, String> map,String type) {
        if(StringUtils.isEmpty(type)){
            return new ResultBean(ApiResultType.SYSTEM_TYPE_IS_NULL,null);
        }
        //判断版本号
        if (StringUtils.isEmpty(map.get("version"))) {
            return new ResultBean(ApiResultType.VERSION_IS_NULL,null);
        }
        return null;
    }

    /**
     * 用户常用类目排序参数校验
     * @param map
     * @return
     */
    public static ResultBean checkUserTypePriority(Map<String, Object> map) {
        if(StringUtils.isEmpty(map.get("type")+"")){
            return new ResultBean(ApiResultType.TYPE_IS_NULL,null);
        }
        if(map.get("relation")==null){
            return new ResultBean(ApiResultType.TYPE_RELATION_IS_NULL,null);
        }
        return null;
    }

    /**
     * 用户常用收入类目删除校验
     * @param map
     * @return
     */
    public static ResultBean checkDeleteCommIncomeType(Map<String, List<String>> map) {
        if(map.get("incomeTypeIds")==null){
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL,null);
        }
        if(map.get("incomeTypeIds").size()<1){
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL,null);
        }
        return null;
    }

    /**
     * 用户常用支出类目删除校验
     * @param map
     * @return
     */
    public static ResultBean checkDeleteCommSpendType(Map<String, List<String>> map) {
        if(map.get("spendTypeIds")==null){
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL,null);
        }
        if(map.get("spendTypeIds").size()<1){
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL,null);
        }
        return null;
    }
}
