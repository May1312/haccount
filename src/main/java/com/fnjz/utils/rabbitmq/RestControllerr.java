package com.fnjz.utils.rabbitmq;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.RedisPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * rest测试接口
 * Created by yhang on 2018/9/13.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
class RestControllerr {

    @Autowired
    private RabbitmqUtils rabbitmqUtils;

    @RequestMapping("/mq")
    public @ResponseBody ResultBean run() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","fengniaojizhang");
        rabbitmqUtils.publish(map);
        return null;
    }
}
