package com.fnjz.front.rabbit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * Created by yhang on 2018/9/14.
 */
@Transactional
class OfflinePushConsumerListener implements ChannelAwareMessageListener {

    @Autowired
    private OfflineSynchronizedRestServiceI offlineSynchronizedRestServiceI;

    @Override
    public void onMessage(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String mq = new String(message.getBody());
            System.out.println("消费消息 = " + mq);
            JSONObject jsonObject= JSONObject.parseObject(mq);
            if(StringUtils.isNotEmpty(mq)){
                //校验同步时间
                String synDate =jsonObject.get("synDate")+"";
                String mobileDevice =jsonObject.get("mobileDevice")+"";
                String userInfoId =jsonObject.get("userInfoId")+"";
                Date latelySynDate = offlineSynchronizedRestServiceI.getLatelySynDate(mobileDevice, userInfoId);
                if(StringUtils.equals(DateUtils.convert2StringAll(latelySynDate),DateUtils.convert2StringAll(Long.valueOf(synDate)))) {
                    List<WarterOrderRestEntity> list = JSONObject.parseArray(JSON.toJSONString(jsonObject.get("synData")),WarterOrderRestEntity.class);
                    offlineSynchronizedRestServiceI.offlinePush(list,mobileDevice,userInfoId);
                    //确认
                    channel.basicAck(deliveryTag, false);
                }else{
                    //丢掉消息---->确认消息
                    channel.basicAck(deliveryTag, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //否认   手动提交事务  channel.basicNack(deliveryTag, false, true);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}