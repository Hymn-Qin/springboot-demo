package com.example.demo.mqtt.data.model;

public enum TopicName {
    ROLL_CALL_DEFAULT(1,"topic1");

    private final Integer key;
    private final String value;

    private TopicName(Integer key,String value){
        this.key = key;
        this.value = value;
    }
    public Integer getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
