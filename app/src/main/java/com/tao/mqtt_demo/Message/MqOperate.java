package com.tao.mqtt_demo.Message;

import java.io.Serializable;

public class MqOperate implements Serializable {

    public  Type type ;
    public Message msg;
    public enum  Type  implements Serializable {
        connect ,close ,reconnect,
    }

    public static class  Message implements Serializable {

        public  String ip ;
        public   String port ;
        public    String sub ;
        public   String theme ;
        public   String clientId ;
        public   String userName ;
        public   String password ;

        @Override
        public String toString() {
            return "Message{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
                    ", sub='" + sub + '\'' +
                    ", theme='" + theme + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MqOperate{" +
                "type=" + type +
                ", msg=" + msg +
                '}';
    }
}
