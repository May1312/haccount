package com.fnjz.front.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointRestEntity;
import com.fnjz.front.service.api.buriedpoint.BuriedPointServiceI;
import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;

/**
 * 埋点消费
 * Created by yhang on 2019/1/4.
 */
@Transactional
class BuriedPointConsumerListener implements ChannelAwareMessageListener {

    private static final Logger logger = Logger.getLogger(BuriedPointConsumerListener.class);

    @Autowired
    private BuriedPointServiceI buriedPointServiceI;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String mq = new String(message.getBody());
            logger.info("消费消息 = " + mq);
            BuriedPointRestEntity entity = JSONObject.parseObject(mq,BuriedPointRestEntity.class);
            buriedPointServiceI.insert(entity);
        } catch (Exception e) {
            logger.error(e.toString());
            //否认   手动提交事务  channel.basicNack(deliveryTag, false, true);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }finally {
            //无论是否正确消费 都确认
            channel.basicAck(deliveryTag, false);
        }
    }
}