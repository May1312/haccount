package com.fnjz.front.service.impl.api.userintegral;

import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.enums.IntegralEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("userIntegralRestService")
@Transactional
public class UserIntegralRestServiceImpl extends CommonServiceImpl implements UserIntegralRestServiceI {

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Override
    public void signInIntegral(String userInfoId, String shareCode,Map<String,String> map) {
        //根据cycle 判断周数
        String cycle = map.get("cycle");
        if(StringUtils.isNotEmpty(cycle)){
            if(StringUtils.equals(cycle,IntegralEnum.SIGNIN_7.getIndex()+"")){
                //userIntegralRestDao
            }else if(StringUtils.equals(cycle,IntegralEnum.SIGNIN_14.getIndex()+"")){

            }else if(StringUtils.equals(cycle,IntegralEnum.SIGNIN_21.getIndex()+"")){

            }else if(StringUtils.equals(cycle,IntegralEnum.SIGNIN_28.getIndex()+"")){

            }
        }
    }
}