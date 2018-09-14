package com.fnjz.utils.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Created by yhang on 2018/9/13.
 */
@Transactional
class ConsumerListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String mq = new String(message.getBody());
            System.out.println("消息消费者 = " + mq);
            //确认
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            //否认   手动提交事务  channel.basicNack(deliveryTag, false, true);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
