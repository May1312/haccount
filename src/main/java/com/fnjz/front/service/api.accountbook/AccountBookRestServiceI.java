package com.fnjz.front.service.api.accountbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import io.swagger.models.auth.In;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;
import java.util.Map;

public interface AccountBookRestServiceI extends CommonService{

    /**
     * 获取账本对应成员数
     * @return
     */
    JSONArray checkABMembers(String userInfoId);

    /**
     * 首页调用获取账本内成员头像
     * @param abId
     * @return
     */
    JSONObject getABMembers(Integer abId,String userInfoId);

    /**
     * 获取用户下所有账本
     * @param userInfoId
     * @return
     */
    List<AccountBookRestDTO> getABAll(String userInfoId);

    /**
     * 删除账本
     * @param abId
     */
    void deleteAB(Integer abId,String userInfoId);

    /**
     * 创建账本
     * @param accountBookRestEntity
     * @return
     */
    int createAB(AccountBookRestEntity accountBookRestEntity);

    /**
     * 修改账本名称
     * @param abName
     * @param abId
     */
    void updateAB(String abName, String abId);

    /**
     * 成员管理页获取账本内成员详情
     * @param abId
     * @param userInfoId
     * @return
     */
    JSONObject membersInfo(Integer abId, String userInfoId);

    /**
     * 删除成员
     * @param map
     * @param userInfoId
     */
    void deleteMembers(Map<String,Object> map, String userInfoId);
    /**
     * 功能描述: 发送移除通知
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 20:52
     */
    void removeTheNotification(Map<String, Object> map,String userInfoId);
    /**
     * 功能描述:邀请记账
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/17 15:01
     */
    JSONObject invitationToAccount(String adminUserInfoId,String accountBookId,String invitedId);
    /**
     * 功能描述: 获取当前账本人数
     *
     * @param: 账本创建者id 账本id
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 16:26
     */
    Integer getAccountNumber(String accountBookId);
}
