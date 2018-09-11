package com.fnjz.utils.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Consumer;
import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费者以线程方式运行，
 * 对于不同的事件有不同的回调函数，
 * 其中最主要的是处理新消息到来的事件。
 */
public class QueueConsumer extends EndPoint implements Runnable, Consumer {

    public QueueConsumer(String endpointName, String hostIP, String username, String password) throws IOException {
        super(endpointName, hostIP, username, password);
    }

    @Override
    public void run() {
        try
        {
            // start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true, this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag)
    {
        System.out.println("QueueConsumer " + consumerTag + " registered");
    }

    /**
     * Called when new message is available.
     */
    public void handleDelivery(String consumerTag, Envelope env, BasicProperties props, byte[] body) throws IOException
    {
        Map map = (HashMap) SerializationUtils.deserialize(body);
        System.out.println("Message Number " + map.get("message number") + " received.");
    }

    public void handleCancel(String consumerTag)
    {
    }

    public void handleCancelOk(String consumerTag)
    {
    }

    public void handleRecoverOk(String consumerTag)
    {
    }

    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1)
    {
    }

    /**
     * for test
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String queueName = "queue";
        String hostIP = "39.107.89.16";
        String username = "admin";
        String password = "admin";

        // 创建消费者，即消息接收者，并启动线程
        QueueConsumer consumer = new QueueConsumer(queueName,hostIP,username,password);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        // 创建生产者，即消息发送者
        Producer producer = new Producer(queueName,hostIP,username,password);

        // 循环发送消息
        for (int i = 0; i < 20; i++)
        {
            HashMap message = new HashMap();
            message.put("message number", i);
            producer.sendMessage(message);
            System.out.println("Message Number " + i + " sent.");
        }
    }
}

/*
几个关键概念:

Broker：  简单来说就是消息队列服务器实体。
Exchange：消息交换机，它指定消息按什么规则，路由到哪个队列。
Queue：   消息队列载体，每个消息都会被投入到一个或多个队列。
Binding： 绑定，它的作用就是把exchange和queue按照路由规则绑定起来。
Routing Key：路由关键字，exchange根据这个关键字进行消息投递。
vhost：      虚拟主机，一个broker里可以开设多个vhost，用作不同用户的权限分离。
producer：   消息生产者，就是投递消息的程序。
consumer：   消息消费者，就是接受消息的程序。
channel：    消息通道，在客户端的每个连接里，可建立多个channel，每个channel代表一个会话任务。

由Exchange，Queue，RoutingKey三个才能决定一个从Exchange到Queue的唯一的线路。

消息队列的使用过程大概如下：

（1）客户端连接到消息队列服务器，打开一个channel。
（2）客户端声明一个exchange，并设置相关属性。
（3）客户端声明一个queue，并设置相关属性。
（4）客户端使用routing key，在exchange和queue之间建立好绑定关系。
（5）客户端投递消息到exchange。
*/