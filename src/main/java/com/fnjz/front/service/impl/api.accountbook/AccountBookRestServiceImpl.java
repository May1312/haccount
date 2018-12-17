package com.fnjz.front.service.impl.api.accountbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.controller.api.message.MessageContentFactory;
import com.fnjz.front.controller.api.message.MessageType;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateIncomeLabelRestDTO;
import com.fnjz.front.entity.api.userprivatelabel.UserPrivateSpendLabelRestDTO;
import com.fnjz.front.service.api.accountbook.AccountBookRestServiceI;
import com.fnjz.front.service.api.message.MessageServiceI;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import com.fnjz.front.service.api.userprivatelabel.UserPrivateLabelRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import com.fnjz.front.utils.WXAppletPushUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Service("accountBookRestService")
@Transactional
public class AccountBookRestServiceImpl extends CommonServiceImpl implements AccountBookRestServiceI {

    @Autowired
    private AccountBookRestDao accountBookRestDao;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;

    @Autowired
    private MessageServiceI messageService;

    @Autowired
    private UserAccountBookRestServiceI userAccountBookRestService;

    @Autowired
    private UserPrivateLabelRestService userPrivateLabelRestService;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Autowired
    private OfflineSynchronizedRestDao offlineSynchronizedRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;

    @Autowired
    private UserInfoAddFieldRestDao userInfoAddFieldRestDao;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    @Override
    public JSONArray checkABMembers(String userInfoId) {
        JSONArray jsonArray = new JSONArray();
        List<Map<String, Integer>> map = accountBookRestDao.checkABMembers(userInfoId);
        if (map != null) {
            if (map.size() > 0) {
                map.forEach(v -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("members", v.get("members"));
                    jsonObject.put("accountBookId", v.get("accountbookid"));
                    jsonArray.add(jsonObject);
                });
            }
        }
        return jsonArray;
    }

    @Override
    public JSONObject getABMembers(Integer abId, String userInfoId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray memberArray = new JSONArray();
        int totalMember = accountBookRestDao.getTotalMember(abId + "");
        //判断当前请求用户是否为账本所有者
        if (totalMember >= 1) {
            //包含多组员  查询创建者  当前用户  其他组员
            List<Map<String, Object>> abMembers = accountBookRestDao.getABMembers(abId);
            abMembers.forEach(v -> {
                if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    //当前请求用户为账本所有者
                    jsonObject.put("yourSelf", v.get("avatarurl"));
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "2")) {
                    jsonObject.put("yourSelf", v.get("avatarurl"));
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "1")) {
                    //当前用户作为成员
                    jsonObject.put("yourSelf", v.get("avatarurl"));
                } else if (!StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    jsonObject.put("owner", v.get("avatarurl"));
                } else {
                    memberArray.add(v.get("avatarurl"));
                }
            });
        } else {
            //不需要头像
            return jsonObject;
        }
        jsonObject.put("members", memberArray);
        return jsonObject;
    }

    @Override
    public List<AccountBookRestDTO> getABAll(String userInfoId) {
        List<AccountBookRestDTO> list = accountBookRestDao.getABAll(userInfoId);
        return list;
    }

    /**
     * 删除账本
     *
     * @param userInfoId
     */
    @Override
    public void deleteAB(String type, Map<String, String> map, String userInfoId) {
        Integer abId = Integer.valueOf(map.get("abId"));
        //判断当前用户身份
        Integer userType = accountBookRestDao.checkUserType(userInfoId, abId);
        if (userType != null) {
            //当前用户类型 0:owner 1:reader 2:writer
            if (userType == 0) {
                //管理员退出操作
                //判断当前账本组员数
                int members = accountBookRestDao.getTotalMember(abId + "");
                if (members > 1) {
                    //解除绑定关系
                    accountBookRestDao.deleteUserAB(userInfoId, abId);
                    //根据账本创建时间 选择下一位组员
                    accountBookRestDao.setOwner(abId);
                    //账本人数-1
                    accountBookRestDao.updateABMember(abId, -1);
                } else {
                    //解除绑定关系
                    accountBookRestDao.deleteUserAB(userInfoId, abId);
                    //删除账本
                    accountBookRestDao.deleteAB(abId);
                    // 删除流水记录
                    warterOrderRestDao.deleteWaterOrderByABId(abId);
                }
            } else {
                //成员退出
                //解除绑定关系
                accountBookRestDao.deleteUserAB(userInfoId, abId);
                //账本人数-1
                accountBookRestDao.updateABMember(abId, -1);
            }
            //更新移动端本次同步时间
            if (StringUtils.equals(type, "android") || StringUtils.equals(type, "ios")) {
                if (StringUtils.isNotEmpty(map.get("mobileDevice"))) {
                    //生成本次同步记录  todo ??问题！！
                    offlineSynchronizedRestDao.insert(map.get("mobileDevice"), userInfoId);
                }
            }
            //记账总笔数置为0
            redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_MY_COUNT + ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), "chargeTotal", 0);
        }
    }

    @Override
    public int createAB(AccountBookRestEntity accountBookRestEntity) {
        int abId = accountBookRestDao.createAB(accountBookRestEntity);
        //用户账本绑定关系
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(accountBookRestEntity.getCreateBy());
        uabre.setAccountBookId(abId);
        uabre.setCreateBy(accountBookRestEntity.getCreateBy());
        uabre.setUserType(0);
        userAccountBookRestDao.insert(uabre);
        return abId;
    }

    @Override
    public void updateAB(String abName, String abId) {
        accountBookRestDao.updateAB(abName, abId);
    }

    @Override
    public JSONObject membersInfo(Integer abId, String userInfoId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray memberArray = new JSONArray();
        int totalMember = accountBookRestDao.getTotalMember(abId + "");
        //判断当前请求用户是否为账本所有者
        if (totalMember > 1) {
            //包含多组员  查询创建者  当前用户  其他组员
            List<Map<String, Object>> abMembers = accountBookRestDao.membersInfo(abId);
            abMembers.forEach(v -> {
                if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    //当前请求用户为账本所有者
                    JSONObject jsonObject1 = new JSONObject();
                    //头像
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //昵称
                    jsonObject1.put("nickName", v.get("nickname"));
                    //创建时间
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("owner", jsonObject1);
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "2")) {
                    //当前用户作为成员
                    JSONObject jsonObject1 = new JSONObject();
                    //头像
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //昵称
                    jsonObject1.put("nickName", v.get("nickname"));
                    //创建时间
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("yourSelf", jsonObject1);
                } else if (!StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    JSONObject jsonObject1 = new JSONObject();
                    //头像
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //昵称
                    jsonObject1.put("nickName", v.get("nickname"));
                    //创建时间
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("owner", jsonObject1);
                } else {
                    JSONObject jsonObject1 = new JSONObject();
                    //头像
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //昵称
                    jsonObject1.put("nickName", v.get("nickname"));
                    //创建时间
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    memberArray.add(jsonObject1);
                }
            });
        } else {
            //不需要头像
            return jsonObject;
        }
        jsonObject.put("members", memberArray);
        return jsonObject;
    }

    /**
     * 删除成员
     *
     * @param map
     * @param userInfoId
     */
    @Override
    public void deleteMembers(Map<String, Object> map, String userInfoId) {
        //判断当前用户是否由此权限
        Integer userType = userAccountBookRestDao.getUserTypeByUserInfoIdAndABId(userInfoId, map.get("abId") + "");
        if (userType == 0) {
            JSONArray memberIds = JSONArray.parseArray(JSON.toJSONString(map.get("memberIds")));
            memberIds.forEach(v -> {
                accountBookRestDao.deleteUserABById(v + "", Integer.valueOf(map.get("abId") + ""));
                //账本人数-1
                accountBookRestDao.updateABMember(Integer.valueOf(map.get("abId") + ""), -1);
            });
        }
    }

    /**
     * 功能描述:删除群记账成员通知
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 20:41
     */
    @Override
    public void removeTheNotification(Map<String, Object> map, String userInfoId,String type) {
        ArrayList<Integer> integers = new ArrayList<>();
        JSONArray memberIds = JSONArray.parseArray(JSON.toJSONString(map.get("memberIds")));
        for (Object memberId : memberIds) {
            UserAccountBookRestEntity entity = userAccountBookRestService.getEntity(UserAccountBookRestEntity.class, Integer.parseInt(memberId.toString()));
            integers.add(entity.getUserInfoId());
        }
        //修改账本名称
        String ABtypeName = accountBookRestDao.getTypeNameByABId(Integer.valueOf(map.get("abId") + ""));
        String messageContent = MessageContentFactory.getMessageContent(MessageType.removeTheNotification, ABtypeName, "管理员", null, null);
        //引入小程序服务通知
        wxappletPush(integers,ABtypeName,userInfoId,messageContent);
        messageService.addUserMessage(messageContent, Integer.parseInt(userInfoId), integers,type);
    }

    /**
     * 移除组员小程序服务通知
     */
    private void wxappletPush(List<Integer> list,String abName,String userInfoId,String messageContent){
        //获取被删除用户openId
        list.forEach(v -> {
            String openId = userInfoAddFieldRestDao.getByUserInfoId(v.toString());
            if (StringUtils.isNotEmpty(openId)) {
                //获取formId
                Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH+openId + "*");
                if (keys.size() > 0) {
                    Object[] arrays = keys.toArray();
                    Arrays.sort(arrays,Collections.reverseOrder());
                    String formId =(String) redisTemplateUtils.popListRight(arrays[0]+"");
                    WXAppletMessageBean bean = new WXAppletMessageBean();
                    //设置账本名称
                    bean.getKeyword1().put("value",abName);
                    //设置操作人昵称
                    bean.getKeyword2().put("value",userInfoRestDao.getUserNameByUserId(Integer.valueOf(userInfoId)));
                    //设置移除时间
                    bean.getKeyword3().put("value",LocalDate.now().toString());
                    //设置备注
                    bean.getKeyword4().put("value",messageContent);
                    wxAppletPushUtils.wxappletPush(WXAppletPushUtils.removeMemberId, openId, formId,WXAppletPushUtils.removeMemberPage,bean);
                }
            }
        });
    }

    @Override
    public JSONObject invitationToAccount(String adminUserInfoId, String accountBookId, String invitedId) {
        Boolean result = false;
        String message = "";
        JSONObject jsonObject = new JSONObject();
        int code = 2000;
        //确认是否已经在此账本中
        UserAccountBookRestEntity invitedUserAccountBook = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(invitedId), Integer.parseInt(accountBookId));
        if (invitedUserAccountBook == null) {
            //确认管理员权限
            UserAccountBookRestEntity userAccountBook = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(adminUserInfoId), Integer.parseInt(accountBookId));
            if (userAccountBook != null) {
                //确认 0_管理员 1_成员
                if (userAccountBook.getUserType() == 0) {
                    //当前账本人数是否小于五人
                    int totalMember = accountBookRestDao.getTotalMember(accountBookId);
                    if (totalMember <= 5) {
                        UserAccountBookRestEntity userAccountBookRestEntity = new UserAccountBookRestEntity();
                        userAccountBookRestEntity.setAccountBookId(Integer.parseInt(accountBookId));
                        userAccountBookRestEntity.setUserInfoId(Integer.parseInt(invitedId));
                        userAccountBookRestEntity.setUserType(1);
                        userAccountBookRestEntity.setCreateDate(new Date());
                        userAccountBookRestEntity.setBindFlag(1);
                        userAccountBookRestEntity.setDelflag(0);
                        Serializable save = this.save(userAccountBookRestEntity);
                        Integer integer = (Integer) save;
                        if ((Integer) save > 0) {
                            message = "邀请成功";
                            result = true;
                            //参加人数+1
                            accountBookRestDao.updateABMember(Integer.parseInt(accountBookId), 1);
                        }
                    } else {
                        code = 2001;
                        message = "人数达到上限,请联系管理员";
                    }
                } else {
                    code = 2004;
                    message = "没有邀请权限";
                }
            } else {
                code = 2002;
                message = "此账本不存在，请核实创建者id，账本id";
            }
        } else {
            code = 2003;
            message = "已经加入过了";
        }
        jsonObject.put("success", result);
        jsonObject.put("code", code);
        jsonObject.put("msg", message);
        return jsonObject;
    }

    @Override
    public Integer getAccountNumber(String accountBookId) {
        int totalMember = accountBookRestDao.getTotalMember(accountBookId);
        return totalMember;
    }

    @Override
    public AccountBookRestDTO getDefaultAB(String userInfoId) {
        return accountBookRestDao.getDefaultAB(userInfoId);
    }

    @Override
    public Map<String, List<?>> createABForMobiel(AccountBookRestEntity accountBookRestEntity) {
        int abId = accountBookRestDao.createAB(accountBookRestEntity);
        //用户账本绑定关系
        //创建用户---账本关联记录
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(accountBookRestEntity.getCreateBy());
        uabre.setAccountBookId(abId);
        uabre.setCreateBy(accountBookRestEntity.getCreateBy());
        uabre.setUserType(0);
        userAccountBookRestDao.insert(uabre);
        //判断用户是否拥有此类型账本
        boolean b = userPrivateLabelRestService.checkUserPrivateLabel(accountBookRestEntity.getCreateBy() + "", RedisPrefix.SPEND, accountBookRestEntity.getAccountBookTypeId());
        userPrivateLabelRestService.checkUserPrivateLabel(accountBookRestEntity.getCreateBy() + "", RedisPrefix.INCOME, accountBookRestEntity.getAccountBookTypeId());
        //分派标签
        List<UserPrivateIncomeLabelRestDTO> income = new ArrayList<>();
        List<UserPrivateSpendLabelRestDTO> spend = new ArrayList<>();
        if (!b) {
            //分派标签
            income = userPrivateLabelRestDao.selectLabelByAbId2(accountBookRestEntity.getCreateBy() + "", accountBookRestEntity.getAccountBookTypeId(), 2);
            spend = userPrivateLabelRestDao.selectLabelByAbId(accountBookRestEntity.getCreateBy() + "", accountBookRestEntity.getAccountBookTypeId(), 1);
        }
        Map<String, List<?>> map = new HashMap<>();
        map.put("spend", spend);
        map.put("income", income);
        return map;
    }
}