package com.example.demo.mqtt.data;

import com.example.demo.mqtt.data.model.MqttEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmqttJobListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 监听topic
     * @param mqttEvent
     */
    @EventListener(condition = "#mqttEvent.topic.equals(T(com.example.demo.mqtt.data.model.TopicName).ROLL_CALL_DEFAULT.getValue())")
    public void onEmqttCall(MqttEventMessage mqttEvent){

        logger.info("接收到消息："+mqttEvent.getMessage());

    }

    @EventListener(condition ="@ emqttPredicate.test(#mqttEvent)")
    public void onEmqttCallTest(MqttEventMessage mqttEvent){
        logger.info("测试通过："+mqttEvent.getMessage());
    }
}
