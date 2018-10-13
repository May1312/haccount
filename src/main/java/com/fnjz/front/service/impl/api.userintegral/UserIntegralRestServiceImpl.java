package com.fnjz.front.service.impl.api.userintegral;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.FengFengTicketRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("userIntegralRestService")
@Transactional
public class UserIntegralRestServiceImpl extends CommonServiceImpl implements UserIntegralRestServiceI {

    @Autowired
    private FengFengTicketRestDao fengFengTicketRestDao;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Override
    public void signInIntegral(String userInfoId, String shareCode, Map<String, String> map) {
        //根据cycle 判断周数
        String cycle = map.get("cycle");
        if (StringUtils.isNotEmpty(cycle)) {
            //判断签到天数是否达标
            int signInDays = redisTemplateUtils.getForHashKey(RedisPrefix.PREFIX_SIGN_IN + shareCode, "signInDays");
            if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_7.getIndex() + "")) {
                //判断领取状态
                int signIn_7 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_7");
                if (signIn_7 != 2 && signInDays >= IntegralEnum.SIGNIN_7.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_7.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue(),ff.getAcquisitionMode(),AcquisitionModeEnum.SignIn.getDescription());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_7", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_14.getIndex() + "")) {
                int signIn_14 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_14");
                if (signIn_14 != 2 && signInDays >= IntegralEnum.SIGNIN_14.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_14.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue(),ff.getAcquisitionMode(),AcquisitionModeEnum.SignIn.getDescription());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_14", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_21.getIndex() + "")) {
                int signIn_21 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_21");
                if (signIn_21 != 2 && signInDays >= IntegralEnum.SIGNIN_21.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_21.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue(),ff.getAcquisitionMode(),AcquisitionModeEnum.SignIn.getDescription());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_21", 2);
                }
            } else if (StringUtils.equals(cycle, IntegralEnum.SIGNIN_28.getIndex() + "")) {
                int signIn_28 = redisTemplateUtils.getForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_28");
                if (signIn_28 != 2 && signInDays >= IntegralEnum.SIGNIN_28.getIndex()) {
                    FengFengTicketRestEntity ff = fengFengTicketRestDao.getFengFengTicket(IntegralEnum.CATEGORY_OF_BEHAVIOR_SIGN_IN.getDescription(), IntegralEnum.ACQUISITION_MODE_SIGN_IN.getDescription(), IntegralEnum.SIGNIN_28.getIndex());
                    userIntegralRestDao.insertSignInIntegral(userInfoId,ff.getId(),ff.getBehaviorTicketValue(),ff.getAcquisitionMode(),AcquisitionModeEnum.SignIn.getDescription());
                    //标记本次领取状态
                    redisTemplateUtils.updateForHashKey(RedisPrefix.USER_INTEGRAL_SIGN_IN_CYCLE_AWARE + shareCode, "signIn_28", 2);
                }
            }
        }
    }

    @Override
    public PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        if(curPage!=null){
            pageRest.setCurPage(curPage);
        }
        if(pageSize!=null){
            pageRest.setPageSize(pageSize);
        }
        List<UserIntegralRestDTO> listForPage = userIntegralRestDao.listForPage(userInfoId,pageRest.getStartIndex(),pageRest.getPageSize());
        //获取总条数
        Integer count = userIntegralRestDao.getCount(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }
}