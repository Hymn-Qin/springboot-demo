package com.example.demo.mqtt.config;

import com.example.demo.mqtt.data.model.MqttEventMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.util.StringUtils;

/**
 * MQTT 生产者
 */
@Configuration
public class MqttConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String url;

    @Value("${spring.mqtt.producer.clientId}")
    private String producerClientId;

    @Value("${spring.mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;

    @Value("${spring.mqtt.consumer.clientId}")
    private String consumerClientId;

    @Value("${spring.mqtt.consumer.defaultTopic}")
    private String consumerDefaultTopic;


//    private MqttPahoMessageDrivenChannelAdapter adapter;

    /**
     * MQTT连接器选项
     *
     * @return {@link org.eclipse.paho.client.mqttv3.MqttConnectOptions}
     */
    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName(username);
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        options.setWill("willTopic", WILL_DATA, 2, false);
        return options;
    }


    /**
     * MQTT客户端
     *
     * @return {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * MQTT信息通道（生产者）
     *
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                producerClientId,
                mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(producerDefaultTopic);
        return messageHandler;
    }

    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @return {@link org.springframework.integration.core.MessageProducer}
     */
    @Bean
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        consumerClientId, mqttClientFactory(),
                        StringUtils.split(consumerDefaultTopic, ","));
//        adapter = new MqttPahoMessageDrivenChannelAdapter(consumerClientId, mqttClientFactory(), "");
//        String[] topics = consumerDefaultTopic.split(",");
//        for (String topic : topics) {
//            if (!StringUtils.isEmpty(topic)) {
//                adapter.addTopic(topic, 1);
//            }
//        }
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        //（1）QoS 0(At most once)“至多一次”
        //消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。
        //这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
        //
        //（2）QoS 1(At least once)“至少一次”
        //确保消息到达，但消息重复可能会发生。
        //
        //（3）QoS 2(Exactly once)“只有一次”
        //确保消息到达一次。
        //这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果。
        //小型传输，开销很小（固定长度的头部是 2 字节），协议交换最小化，以降低网络流量。
        adapter.setQos(2);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

//    /**
//     * 添加监听主题
//     */
//    public void addListenerTopic(String[] topics) {
//        if (adapter == null) {
//            adapter = new MqttPahoMessageDrivenChannelAdapter(consumerClientId, mqttClientFactory(), "");
//        }
//        for (String topic : topics) {
//            if (!StringUtils.isEmpty(topic)) {
//                adapter.addTopic(topic, 1);
//            }
//        }
//        adapter.removeTopic();
//    }
//
//    /**
//     * 移除监听主题
//     */
//    public void removeListenerTopic(String topic) {
//        if (adapter == null) {
//            adapter = new MqttPahoMessageDrivenChannelAdapter(consumerClientId, mqttClientFactory(), "");
//        }
//        adapter.removeTopic(topic);
//    }

    /**
     * MQTT信息通道1（消费者）
     *
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * MQTT消息处理器1（消费者）
     *
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                String type = topic.substring(topic.lastIndexOf("/") + 1, topic.length());
                String qos = message.getHeaders().get("mqtt_receivedQos").toString();

                logger.info("topic: {}, type: {}, qos: {}, message: {}", topic, type, qos, message.getPayload());

                //触发事件
                eventPublisher.publishEvent(new MqttEventMessage(this, topic, message.getPayload().toString()));
            }
        };
    }
    //（1）QoS 0(At most once)“至多一次”
    //消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
    //
    //（2）QoS 1(At least once)“至少一次”
    //确保消息到达，但消息重复可能会发生。
    //
    //（3）QoS 2(Exactly once)“只有一次”
    //确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果。小型传输，开销很小（固定长度的头部是 2 字节），协议交换最小化，以降低网络流量。
}
