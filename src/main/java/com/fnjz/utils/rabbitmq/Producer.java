package com.fnjz.utils.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.lang.SerializationUtils;
import java.io.IOException;
import java.io.Serializable;

/**
 * 消息生产者
 */
public class Producer extends EndPoint{

    public Producer(String endpointName, String hostIP, String username, String password) throws IOException {
        super(endpointName, hostIP, username, password);
    }

    public void sendMessage(Serializable object) throws IOException
    {
        channel.basicPublish("", endPointName, null, SerializationUtils.serialize(object));
    }
}