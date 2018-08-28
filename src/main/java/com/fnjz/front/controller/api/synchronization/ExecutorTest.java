package com.fnjz.front.controller.api.synchronization;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 线程池测试类
 * Created by yhang on 2018/8/27.
 */

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class ExecutorTest {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping("/executor")
    @ResponseBody
    public ResultBean run(){
        System.out.println("执行主线程。。。");
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("执行子线程中。。。");
            }
        });
        System.out.println("执行主线程结束。。。");
        return new ResultBean(ApiResultType.OK,null);
    }
}
