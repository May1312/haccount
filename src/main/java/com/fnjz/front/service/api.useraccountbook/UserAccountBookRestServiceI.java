package com.fnjz.front.service.api.useraccountbook;

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

public interface UserAccountBookRestServiceI extends CommonService{

    /**
     * 获取当前用户下的账本ids
     * @param userInfoId
     * @return
     */
    List<String> listForABIdSByUserInfoId(String userInfoId);

    /**
     * 获取用户默认账本
     * @param userInfoId
     * @return
     */
    UserAccountBookRestEntity getUserAccountBookByUserInfoId(int userInfoId);
    /**
     * 功能描述: 判断用户是否已经加入此账本
     *
     * @param: 用户id  账本id
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 17:06
     */
    UserAccountBookRestEntity checkUserisExistAccount(String userInfoId,String accountBookId);
}
