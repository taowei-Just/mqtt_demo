package com.tao.mqtt_demo.Message;

public class Mess {

    public Type type;
    public String msg;
    public String data;

    public enum Type {
        server, mq_operate, mq_message
    }

    @Override
    public String toString() {
        return "Mess{" +
                "type=" + type +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
