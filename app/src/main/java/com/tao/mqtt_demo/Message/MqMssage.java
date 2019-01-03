package com.tao.mqtt_demo.Message;

import java.io.Serializable;

public class MqMssage implements Serializable {

    public Type type;
    public String message;

    public enum Type implements Serializable {
        send, receiver
    }

    @Override
    public String toString() {
        return "MqMssage{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
