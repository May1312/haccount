package com.fnjz.front.service.impl.api.userinvite;

import com.fnjz.front.dao.UserInviteRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.UserInviteRestDTO;
import com.fnjz.front.service.api.api.userinvite.UserInviteRestServiceI;
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
        Integer count = userInviteRestDao.getCount(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }
}
