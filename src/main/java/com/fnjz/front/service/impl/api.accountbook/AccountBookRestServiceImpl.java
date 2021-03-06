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
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.newWeChat.WXAppletPushUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
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

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;

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

    private static String defaultLogo="https://head.image.fengniaojizhang.cn/duck/logo.png";
    @Override
    public JSONObject getABMembers(Integer abId, String userInfoId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray memberArray = new JSONArray();
        int totalMember = accountBookRestDao.getTotalMember(abId + "");
        //????????????????????????????????????????????????
        if (totalMember >= 1) {
            //???????????????  ???????????????  ????????????  ????????????
            List<Map<String, Object>> abMembers = accountBookRestDao.getABMembers(abId);
            abMembers.forEach(v -> {
                if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    //????????????????????????????????????
                    jsonObject.put("yourSelf", v.get("avatarurl")==null?defaultLogo:v.get("avatarurl"));
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "2")) {
                    jsonObject.put("yourSelf", v.get("avatarurl")==null?defaultLogo:v.get("avatarurl"));
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "1")) {
                    //????????????????????????
                    jsonObject.put("yourSelf", v.get("avatarurl")==null?defaultLogo:v.get("avatarurl"));
                } else if (!StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    jsonObject.put("owner", v.get("avatarurl")==null?defaultLogo:v.get("avatarurl"));
                } else {
                    memberArray.add(v.get("avatarurl")==null?defaultLogo:v.get("avatarurl"));
                }
            });
        } else {
            //???????????????
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
     * ????????????
     *
     * @param userInfoId
     */
    @Override
    public void deleteAB(String type, Map<String, String> map, String userInfoId) {
        Integer abId = Integer.valueOf(map.get("abId"));
        //????????????????????????
        Integer userType = accountBookRestDao.checkUserType(userInfoId, abId);
        if (userType != null) {
            //?????????????????? 0:owner 1:reader 2:writer
            if (userType == 0) {
                //?????????????????????
                //???????????????????????????
                int members = accountBookRestDao.getTotalMember(abId + "");
                if (members > 1) {
                    //??????????????????
                    accountBookRestDao.deleteUserAB(userInfoId, abId);
                    //???????????????????????? ?????????????????????
                    accountBookRestDao.setOwner(abId);
                    //????????????-1
                    accountBookRestDao.updateABMember(abId, -1);
                } else {
                    //??????????????????
                    accountBookRestDao.deleteUserAB(userInfoId, abId);
                    //????????????
                    accountBookRestDao.deleteAB(abId);
                    // ??????????????????
                    warterOrderRestDao.deleteWaterOrderByABId(abId);
                }
            } else {
                //????????????
                //??????????????????
                accountBookRestDao.deleteUserAB(userInfoId, abId);
                //????????????-1
                accountBookRestDao.updateABMember(abId, -1);
            }
            //?????????????????????????????????
            if (StringUtils.equals(type, "android") || StringUtils.equals(type, "ios")) {
                if (StringUtils.isNotEmpty(map.get("mobileDevice"))) {
                    //????????????????????????  todo ??????????????
                    offlineSynchronizedRestDao.insert(map.get("mobileDevice"), userInfoId);
                }
            }
            //???????????????
            int chargeTotal = warterOrderRestServiceI.chargeTotalv2(userInfoId);
            redisTemplateUtils.updateForHashKey(RedisPrefix.PREFIX_MY_COUNT + ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)), "chargeTotal", chargeTotal);
        }
    }

    @Override
    public int createAB(AccountBookRestEntity accountBookRestEntity) {
        int abId = accountBookRestDao.createAB(accountBookRestEntity);
        //????????????????????????
        //????????????---??????????????????
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
        //????????????????????????????????????????????????
        if (totalMember > 1) {
            //???????????????  ???????????????  ????????????  ????????????
            List<Map<String, Object>> abMembers = accountBookRestDao.membersInfo(abId);
            abMembers.forEach(v -> {
                if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    //????????????????????????????????????
                    JSONObject jsonObject1 = new JSONObject();
                    //??????
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //??????
                    jsonObject1.put("nickName", v.get("nickname"));
                    //????????????
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("owner", jsonObject1);
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "2")) {
                    //????????????????????????
                    JSONObject jsonObject1 = new JSONObject();
                    //??????
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //??????
                    jsonObject1.put("nickName", v.get("nickname"));
                    //????????????
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("yourSelf", jsonObject1);
                } else if (!StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    JSONObject jsonObject1 = new JSONObject();
                    //??????
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //??????
                    jsonObject1.put("nickName", v.get("nickname"));
                    //????????????
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    jsonObject.put("owner", jsonObject1);
                } else {
                    JSONObject jsonObject1 = new JSONObject();
                    //??????
                    jsonObject1.put("avatarUrl", v.get("avatarurl"));
                    //??????
                    jsonObject1.put("nickName", v.get("nickname"));
                    //????????????
                    jsonObject1.put("createDate", v.get("createdate"));
                    //id
                    jsonObject1.put("id", v.get("id"));
                    memberArray.add(jsonObject1);
                }
            });
        } else {
            //???????????????
            return jsonObject;
        }
        jsonObject.put("members", memberArray);
        return jsonObject;
    }

    /**
     * ????????????
     *
     * @param map
     * @param userInfoId
     */
    @Override
    public void deleteMembers(Map<String, Object> map, String userInfoId) {
        //????????????????????????????????????
        Integer userType = userAccountBookRestDao.getUserTypeByUserInfoIdAndABId(userInfoId, map.get("abId") + "");
        if (userType == 0) {
            JSONArray memberIds = JSONArray.parseArray(JSON.toJSONString(map.get("memberIds")));
            memberIds.forEach(v -> {
                accountBookRestDao.deleteUserABById(v + "", Integer.valueOf(map.get("abId") + ""));
                //????????????-1
                accountBookRestDao.updateABMember(Integer.valueOf(map.get("abId") + ""), -1);
            });
        }
    }

    /**
     * ????????????:???????????????????????????
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
        //??????????????????
        String ABtypeName = accountBookRestDao.getTypeNameByABId(Integer.valueOf(map.get("abId") + ""));
        String messageContent = MessageContentFactory.getMessageContent(MessageType.removeTheNotification, ABtypeName, "?????????", null, null);
        //???????????????????????????
        wxappletPush(integers,ABtypeName,userInfoId,messageContent);
        messageService.addUserMessage(messageContent, Integer.parseInt(userInfoId), integers,type);
    }

    /**
     * ?????????????????????????????????
     */
    private void wxappletPush(List<Integer> list,String abName,String userInfoId,String messageContent){
        //?????????????????????openId
        list.forEach(v -> {
            String openId = userInfoAddFieldRestDao.getByUserInfoId(v.toString());
            if (StringUtils.isNotEmpty(openId)) {
                //??????formId
                Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH+openId + "*");
                if (keys.size() > 0) {
                    Object[] arrays = keys.toArray();
                    Arrays.sort(arrays,Collections.reverseOrder());
                    String formId =(String) redisTemplateUtils.popListRight(arrays[0]+"");
                    WXAppletMessageBean bean = new WXAppletMessageBean();
                    //??????????????????
                    bean.getKeyword1().put("value",abName);
                    //?????????????????????
                    bean.getKeyword2().put("value",userInfoRestDao.getUserNameByUserId(Integer.valueOf(userInfoId)));
                    //??????????????????
                    bean.getKeyword3().put("value",LocalDate.now().toString());
                    //????????????
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
        //?????????????????????????????????
        UserAccountBookRestEntity invitedUserAccountBook = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(invitedId), Integer.parseInt(accountBookId));
        if (invitedUserAccountBook == null) {
            //?????????????????????
            UserAccountBookRestEntity userAccountBook = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(adminUserInfoId), Integer.parseInt(accountBookId));
            if (userAccountBook != null) {
                //?????? 0_????????? 1_??????
                if (userAccountBook.getUserType() == 0) {
                    //????????????????????????????????????
                    int totalMember = accountBookRestDao.getTotalMember(accountBookId);
                    if (totalMember < 5) {
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
                            message = "????????????";
                            result = true;
                            //????????????+1
                            accountBookRestDao.updateABMember(Integer.parseInt(accountBookId), 1);
                        }
                    } else {
                        code = 2001;
                        message = "??????????????????,??????????????????";
                    }
                } else {
                    code = 2004;
                    message = "??????????????????";
                }
            } else {
                code = 2002;
                message = "???????????????????????????????????????id?????????id";
            }
        } else {
            code = 2003;
            message = "??????????????????";
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
        //????????????????????????
        //????????????---??????????????????
        UserAccountBookRestEntity uabre = new UserAccountBookRestEntity();
        uabre.setUserInfoId(accountBookRestEntity.getCreateBy());
        uabre.setAccountBookId(abId);
        uabre.setCreateBy(accountBookRestEntity.getCreateBy());
        uabre.setUserType(0);
        userAccountBookRestDao.insert(uabre);
        //???????????????????????????????????????
        boolean b = userPrivateLabelRestService.checkUserPrivateLabel(accountBookRestEntity.getCreateBy() + "", RedisPrefix.SPEND, accountBookRestEntity.getAccountBookTypeId());
        userPrivateLabelRestService.checkUserPrivateLabel(accountBookRestEntity.getCreateBy() + "", RedisPrefix.INCOME, accountBookRestEntity.getAccountBookTypeId());
        //????????????
        List<UserPrivateIncomeLabelRestDTO> income = new ArrayList<>();
        List<UserPrivateSpendLabelRestDTO> spend = new ArrayList<>();
        if (!b) {
            //????????????
            income = userPrivateLabelRestDao.selectLabelByAbId2(accountBookRestEntity.getCreateBy() + "", accountBookRestEntity.getAccountBookTypeId(), 2);
            spend = userPrivateLabelRestDao.selectLabelByAbId(accountBookRestEntity.getCreateBy() + "", accountBookRestEntity.getAccountBookTypeId(), 1);
        }
        Map<String, List<?>> map = new HashMap<>();
        map.put("spend", spend);
        map.put("income", income);
        return map;
    }
}