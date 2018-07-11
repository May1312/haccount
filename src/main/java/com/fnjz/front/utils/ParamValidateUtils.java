package com.fnjz.front.utils;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.enums.LoginEnum;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * controller层参数校验
 * Created by yhang on 2018/7/11.
 */
public class ParamValidateUtils {

    /**
     * 账号密码/验证码登录校验
     * @param map
     * @return
     */
    public static ResultBean checkeLongin(Map<String, String> map, LoginEnum login){
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL,null);
        }
        //密码登录校验密码
        if(login.getIndex()==1){
            if(StringUtils.isEmpty(map.get("password"))){
                return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL,null);
            }
        }
        //验证码登录校验验证码
        else if(login.getIndex()==2){
            if(StringUtils.isEmpty(map.get("verifycode"))){
                return new ResultBean(ApiResultType.VERIFYCODE_IS_NULL,null);
            }
        }
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR,null);
        }
        return null;
    }

    /**
     * 验证手机号格式
     * @param map
     * @return
     */
    public static ResultBean checkeMobile(Map<String, String> map){
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_IS_NULL,null);
        }

        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR,null);
        }
        return null;
    }

    /**
     * 微信/小程序登录校验
     * @param map
     * @param login
     * @return
     */
    public static ResultBean checkeLoginByWechat(Map<String, String> map,LoginEnum login){
        if(login.getIndex()==3){
            if (StringUtils.isEmpty(map.get("code"))) {
                return new ResultBean(ApiResultType.WECHAT_CODE_ISNULL,null);
            }
        }else if(login.getIndex()==4){
            if (StringUtils.isEmpty(map.get("code"))) {
                return new ResultBean(ApiResultType.WXAPPLET_CODE_ISNULL,null);
            }
        }
        return null;
    }

    /**
     * 小程序注册校验
     * @param map
     * @return
     */
    public static ResultBean checkRegisterByWXApplet(Map<String, String> map){
        //encryptedData 加密数据
        if (StringUtils.isEmpty(map.get("encryptedData"))) {
            return new ResultBean(ApiResultType.encryptedData_IS_NULL,null);
        }
        if (StringUtils.isEmpty(map.get("iv"))) {
            return new ResultBean(ApiResultType.IV_IS_NULL,null);
        }
        if (StringUtils.isEmpty(map.get("key"))) {
            return new ResultBean(ApiResultType.KEY_IS_NULL,null);
        }
        return null;
    }

    /**
     * 重置密码/微信绑定手机号 校验
     * @param map
     * @return
     */
    public static ResultBean checkResetpwd(Map<String, String> map){
        if (StringUtils.isEmpty(map.get("mobile"))) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL,null);
        }

        if(StringUtils.isEmpty(map.get("password"))){
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL,null);
        }

        if(StringUtils.isEmpty(map.get("verifycode"))){
            return new ResultBean(ApiResultType.VERIFYCODE_IS_NULL,null);
        }
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            return new ResultBean(ApiResultType.MOBILE_FORMAT_ERROR,null);
        }
        return null;
    }

    /**
     * 修改密码校验
     * @param map
     * @return
     */
    public static ResultBean checkUpdatepwd(Map<String, String> map){
        if (StringUtils.isEmpty(map.get("oldpwd"))) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        if(StringUtils.isEmpty(map.get("newpwd"))){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        return null;
    }

    /**
     * 记账对象校验
     * @param charge
     * @return
     */
    public static ResultBean checkToCharge(WarterOrderRestEntity charge){
        //校验记账时间
        if (charge.getChargeDate() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_SPENDDATE_ERROR,null);
        }
        //校验金额
        if (charge.getMoney() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_MONEY_IS_NULL,null);
        }
        if (!ValidateUtils.checkDecimal(charge.getMoney() + "")) {
            return new ResultBean(ApiResultType.ACCOUNT_MONEY_ERROR,null);
        }
        //判断支出收入类型
        if (charge.getOrderType() == null) {
            return new ResultBean(ApiResultType.ACCOUNT_TYPE_ERROR,null);
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(charge.getTypePid())) {
            return new ResultBean(ApiResultType.ACCOUNT_PARAMS_ERROR,null);
        }
        if (StringUtils.isEmpty(charge.getTypeId())) {
            return new ResultBean(ApiResultType.ACCOUNT_PARAMS_ERROR,null);
        }
        if (charge.getOrderType() == 1) {
            //支出类型判断即时和分期类型
            if (charge.getIsStaged() == null) {
                return new ResultBean(ApiResultType.ACCOUNT_TYPE_ERROR,null);
            }
        }
        return null;
    }
}
