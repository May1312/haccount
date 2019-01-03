package com.fnjz.front.service.impl.api.userinvite;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.dao.UserInfoRestDao;
import com.fnjz.front.dao.UserInviteRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.UserInviteRestDTO;
import com.fnjz.front.service.api.api.userinvite.UserInviteRestServiceI;
import com.fnjz.front.utils.RedisLockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yhang on 2018/10/17.
 */
@Service("userInviteRestService")
@Transactional
public class UserInviteRestServiceImpl implements UserInviteRestServiceI {

    @Autowired
    private UserInviteRestDao userInviteRestDao;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    /**
     * 获取邀请人数
     * @param userInfoId
     * @return
     */
    @Override
    public int getCountForInvitedUsers(String userInfoId) {
        return userInviteRestDao.getCountForInvitedUsers(userInfoId);
    }

    /**
     * 邀请历史记录列表
     * @param userInfoId
     * @param curPage
     * @param pageSize
     * @return
     */
    @Override
    public PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<UserInviteRestDTO> listForPage = userInviteRestDao.listForPage(userInfoId,pageRest.getStartIndex(),pageRest.getPageSize());
        //获取总条数
        Integer count = userInviteRestDao.getCountForInvitedUsers(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }

    @Autowired
    private RedisLockUtils redisLock;

    @Override
    public ResultBean insert(int userInfoId, int inviteUserInfoId) {
        //校验邀请码
        int i = userInfoRestDao.checkUserExists(userInfoId);
        if(i>0){
            redisLock.lock(inviteUserInfoId+"");
            int j = userInviteRestDao.checkExists(inviteUserInfoId);
            if(j<1){
                userInviteRestDao.insert(userInfoId, inviteUserInfoId);
                redisLock.unlock(inviteUserInfoId+"");
                return new ResultBean(ApiResultType.OK,null);
            }else{
                redisLock.unlock(inviteUserInfoId+"");
                return new ResultBean(ApiResultType.HAD_BIND,null);
            }
        }else{
            return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
        }
    }
}
