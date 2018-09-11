package com.fnjz.utils.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 将产生产者和消费者统一为 EndPoint类型的队列。
 * 不管是生产者还是消费者，
 * 连接队列的代码都是一样的，
 * 这样可以通用一些。
 */
public abstract class EndPoint {

    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName, String hostIP, String username, String password) throws IOException
    {
        this.endPointName = endpointName;
        // Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        // 与RabbitMQ Server建立连接
        // 连接到的broker在本机localhost上
        factory.setHost(hostIP);
        factory.setUsername(username);
        factory.setPassword(password);
        //factory.setVirtualHost(virtualHost);
        //factory.setPort(portNumber);
        // 启用自动连接恢复
        factory.setAutomaticRecoveryEnabled(true);
        //如果恢复因异常失败(如. RabbitMQ节点仍然不可达),它会在固定时间间隔后进行重试(默认是5秒).
        factory.setNetworkRecoveryInterval(10000);

        //当提供了address列表时,将会在所有address上逐个进行尝试:
        //Address[] addresses = {new Address("192.168.1.4"), new Address("192.168.1.5")};
        //factory.newConnection(addresses);

        // getting a connection
        connection = factory.newConnection();
        // creating a channel
        channel = connection.createChannel();
        // declaring a queue for this channel. If queue does not exist,
        // it will be created on the server.
        // queueDeclare的参数：queue 队列名；durable true为持久化；exclusive 是否排外，true为队列只可以在本次的连接中被访问，
        // autoDelete true为connection断开队列自动删除；arguments 用于拓展参数
        channel.queueDeclare(endpointName, false, false, false, null);
    }

    /**
     * 关闭channel和connection。并非必须，因为隐含是自动调用的。
     * @throws IOException
     */
    public void close() throws IOException
    {
        this.channel.close();
        this.connection.close();
    }
}

/*
使用 Exchanges 和 Queues
采用交换器和队列工作的客户端应用程序,是AMQP高级别构建模块。在使用前，必须先声明.声明每种类型的对象都需要确保名称存在，如果有必要须进行创建.
继续上面的例子,下面的代码声明了一个交换器和一个队列，然后再将它们进行绑定.

channel.exchangeDeclare(exchangeName, "direct", true);
String queueName = channel.queueDeclare().getQueue();
channel.queueBind(queueName, exchangeName, routingKey);
这实际上会声明下面的对象，它们两者都可以可选参数来定制. 在这里，它们两个都没有特定参数。

一个类型为direct，且持久化，非自动删除的交换器
采用随机生成名称，且非持久化，私有的，自动删除队列
上面的函数然后使用给定的路由键来绑定队列和交换器.

注意，当只有一个客户端时，这是一种典型声明队列的方式:它不需要一个已知的名称，其它的客户端也不会使用它(exclusive),并会被自动清除(autodelete).
如果多个客户端想共享带有名称的队列，下面的代码应该更适合:

channel.exchangeDeclare(exchangeName, "direct", true);
channel.queueDeclare(queueName, true, false, false, null);
channel.queueBind(queueName, exchangeName, routingKey);
这实际上会声明:

一个类型为direct，且持久化，非自动删除的交换器
一个已知名称，且持久化的，非私有，非自动删除队列
注意，Channel API 的方法都是重载的。这些 exchangeDeclare, queueDeclare 和queueBind 都使用的是预设行为.
这里也有更多参数的长形式，它们允许你按需覆盖默认行为，允许你完全控制。
*/