package com.fnjz.utils.rabbitmq;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * queue ack确认回调
 * Created by yhang on 2018/9/14.
 */
@Component
public class ReturnCallBackListener implements RabbitTemplate.ReturnCallback {

    private static final Logger logger = Logger.getLogger(ReturnCallBackListener.class);

    //只有exchange发送到queue失败时才会触发
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //输出错误日志
        logger.error("发送mq queue失败:"+new String(message.getBody())+",replyCode:"+replyCode+",replyText:"+replyText+",exchange:"+exchange+",routingKey:"+routingKey);
    }
}
