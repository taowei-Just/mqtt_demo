package com.tao.mqtt_demo.iml;

import com.tao.mqtt_demo.Message.MqMssage;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface IMq {

    void send(MqMssage mssage) throws MqttException, Exception;
    void connect() throws MqttException, Exception;
    void disconnect() throws MqttException, Exception;
    void sub(String theme) throws MqttException, Exception;
}
