package com.fnjz.utils.rabbitmq;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

/**
 * exchange ack确认回调
 * Created by yhang on 2018/9/14.
 */
@Component
public class ConfirmCallBackListener implements RabbitTemplate.ConfirmCallback {

    private static final Logger logger = Logger.getLogger(ConfirmCallBackListener.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(!ack){
            //输出错误日志
            logger.error("发送mq exchange失败:"+correlationData+",cause:"+cause);
        }
    }
}
