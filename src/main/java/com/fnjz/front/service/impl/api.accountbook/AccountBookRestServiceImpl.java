package com.fnjz.front.service.impl.api.accountbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.controller.api.message.MessageContentFactory;
import com.fnjz.front.controller.api.message.MessageType;
import com.fnjz.front.dao.AccountBookRestDao;
import com.fnjz.front.dao.UserAccountBookRestDao;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.accountbook.AccountBookRestServiceI;
import com.fnjz.front.service.api.message.MessageServiceI;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        if (totalMember>=1){
            //包含多组员  查询创建者  当前用户  其他组员
            List<Map<String, Object>> abMembers = accountBookRestDao.getABMembers(abId);
            abMembers.forEach(v -> {
                if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "0")) {
                    //当前请求用户为账本所有者
                    jsonObject.put("yourSelf", v.get("avatarurl"));
                } else if (StringUtils.equals(v.get("userinfoid") + "", userInfoId) && StringUtils.equals(v.get("usertype") + "", "2")) {
                    jsonObject.put("yourSelf",v.get("avatarurl"));
                }else if(StringUtils.equals(v.get("userinfoid")+"",userInfoId)&&StringUtils.equals(v.get("usertype")+"","1")){
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

    @Override
    public void deleteAB(Integer abId, String userInfoId) {
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
    public void removeTheNotification(Map<String, Object> map, String userInfoId) {
        ArrayList<Integer> integers = new ArrayList<>();
        JSONArray memberIds = JSONArray.parseArray(JSON.toJSONString(map.get("memberIds")));
        for (Object memberId : memberIds) {
            UserAccountBookRestEntity entity = userAccountBookRestService.getEntity(UserAccountBookRestEntity.class, Integer.parseInt(memberId.toString()));
            integers.add(entity.getUserInfoId());
        }
        //修改账本名称
        String ABtypeName = accountBookRestDao.getTypeNameByABId(Integer.valueOf(map.get("abId") + ""));
        String messageContent = MessageContentFactory.getMessageContent(MessageType.removeTheNotification, ABtypeName, "管理员", null, null);
        messageService.addUserMessage(messageContent, Integer.parseInt(userInfoId), integers);
    }

    @Override
    public JSONObject invitationToAccount(String adminUserInfoId, String accountBookId, String invitedId) {
        Boolean result = false;
        String message = "";
        JSONObject jsonObject = new JSONObject();
        //确认管理员权限
        UserAccountBookRestEntity userAccountBook = userAccountBookRestDao.getUserAccountBookByUserInfoIdAndAccountBookId(Integer.parseInt(adminUserInfoId), Integer.parseInt(accountBookId));
        if (userAccountBook != null){
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
                    int insert = userAccountBookRestDao.insert(userAccountBookRestEntity);
                    if (insert > 0) {
                        message = "邀请成功";
                        result = true;
                    }
                } else {
                    message = "人数达到上限";
                }
            } else {
                message = "没有邀请权限";
            }
        }else {
            message = "此账本不存在，请核实创建者id，账本id";
        }

        jsonObject.put("success", result);
        jsonObject.put("msg", message);
        return jsonObject;
    }

    @Override
    public Integer getAccountNumber(String accountBookId) {
        int totalMember = accountBookRestDao.getTotalMember(accountBookId);
        return totalMember;
    }
}