package com.fnjz.front.rabbit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.service.api.offlineSynchronized.OfflineSynchronizedRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 移动端离线同步消费
 * Created by yhang on 2018/9/14.
 */
@Transactional
class OfflinePushConsumerListener implements ChannelAwareMessageListener {

    private static final Logger logger = Logger.getLogger(OfflinePushConsumerListener.class);

    @Autowired
    private OfflineSynchronizedRestServiceI offlineSynchronizedRestServiceI;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String mq = new String(message.getBody());
            logger.info("消费消息 = " + mq);
            JSONObject jsonObject= JSONObject.parseObject(mq);
            if(StringUtils.isNotEmpty(mq)){
                //校验同步时间
                String synDate =jsonObject.get("synDate")+"";
                String mobileDevice =jsonObject.get("mobileDevice")+"";
                String userInfoId =jsonObject.get("userInfoId")+"";
                String clientId =jsonObject.get("clientId")+"";
                Date latelySynDate = offlineSynchronizedRestServiceI.getLatelySynDate(mobileDevice, userInfoId);
                if(latelySynDate!=null&&synDate!=null){
                    if(StringUtils.equals(DateUtils.convert2StringAll(latelySynDate),DateUtils.convert2StringAll(Long.valueOf(synDate)))) {
                        List<WarterOrderRestNewLabel> list = JSONObject.parseArray(JSON.toJSONString(jsonObject.get("synData")),WarterOrderRestNewLabel.class);
                        offlineSynchronizedRestServiceI.offlinePush(list,mobileDevice,userInfoId,clientId);
                    }
                }
            }
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