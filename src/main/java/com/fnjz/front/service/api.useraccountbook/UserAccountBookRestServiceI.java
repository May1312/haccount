package com.fnjz.front.service.api.useraccountbook;

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;
import java.util.Map;

public interface UserAccountBookRestServiceI extends CommonService{

    /**
     * 获取当前用户下的账本ids
     * @param userInfoId
     * @return
     */
    //List<String> listForABIdSByUserInfoId(String userInfoId);

    List<Map<String,Integer>> listForABTypeIdSByUserInfoId(String userInfoId);

    List<Map<String,Integer>> listForABIdSByUserInfoId(String userInfoId);

    /**
     * 获取用户默认账本
     * @param userInfoId
     * @return
     */
    UserAccountBookRestEntity getUserAccountBookByUserInfoId(int userInfoId);

    UserAccountBookRestEntity checkUserisExistAccount(String userInfoId, String accountBookId);
}
