package com.tao.mqtt_demo.Message;

public class ServerMsg {

    public  Type from;
    public  String msg;

    public enum Type {
        server , ui
    }
}
