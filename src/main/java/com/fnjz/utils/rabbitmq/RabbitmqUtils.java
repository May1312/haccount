package com.fnjz.utils.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq 工具类
 * Created by yhang on 2018/9/13.
 */
@Component
public class RabbitmqUtils {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发送消息
    public void publish(Object obj){
        rabbitTemplate.convertAndSend(obj);
    }
}
