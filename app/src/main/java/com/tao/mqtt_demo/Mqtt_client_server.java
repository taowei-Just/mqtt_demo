package com.tao.mqtt_demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tao.mqtt_demo.Message.Mess;
import com.tao.mqtt_demo.Message.MqMssage;
import com.tao.mqtt_demo.Message.MqOperate;
import com.tao.mqtt_demo.Message.ServerMsg;
import com.tao.mqtt_demo.iml.IMq;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Mqtt_client_server extends Service implements IMq {


    private String serverUrl = "tcp://2p172088f2.iok.la:10000";
    private String clientid = "id1";
    private String userName = "usr1";
    private String password = "usr1";
    private String theme = "usr1";
    String[] topis = new String[]{theme, "usr2"};
    String TAG = getClass().getSimpleName();
    private MqttClient mqttClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    int messageId = 0;

    @Override
    public void send(MqMssage mssage) throws Exception {
        if (!isConnect) {
            sengUnConnect();
            return;
        }
        // mqtt消息
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setId(messageId++);
        mqttMessage.setPayload(mssage.message.getBytes());
        // 向指定主题发送消息
        mqttClient.publish(theme, mqttMessage);

    }

    private void sengUnConnect() {

        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq  un connect!";
        EventBus.getDefault().post(serverMsg);
    }


    boolean isConnect = false;

    @Override
    public void connect() throws Exception {
        if (isConnect) {
            sendAleradyConnect();
            Log.e(TAG, " mq 已经连接:");
            return;
        }

        //        String tmpDir = System.getProperty("java.io.tmpdir");
        // 创建 mqtt数据持久化文件路径
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(getFilesDir().getAbsolutePath());
        // mqtt 连接参数配置
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setConnectionTimeout(5 * 1000);

        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(password.toCharArray());

        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(20);
        // mqtt 客户端 1.服务端地址 Mqtt服务器地址(tcp://xxxx:1863)   2. 客户端id （唯一）
        mqttClient = new MqttClient(serverUrl, clientid, dataStore);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // 连接中断
                Log.e(TAG, "connectionLost" + cause.toString());
                isConnect = false;
                sendClose();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // 收到消息
                Log.e(TAG, "messageArrived" + message.toString() + " topic" + topic);

                sendReceiver(new String(message.getPayload()));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 消息发送完成
                try {
                    Log.e(TAG, "deliveryComplete" + token.getMessage());
                    sendComplete();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        // 连接服务端
        mqttClient.connect(mqttConnectOptions);
        isConnect = true;
        Log.e(TAG, " mq 连接成功 :");

    }

    private void sendReceiver(String s) {

        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq receiver data +\n " +s;
        EventBus.getDefault().post(serverMsg);
    }

    private void sendAleradyConnect() {

        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq alerady connect!";
        EventBus.getDefault().post(serverMsg);
    }

    @Override
    public void disconnect() throws Exception {

        Log.e(TAG, "关闭mq");
        // 关闭连接  此处必须先 断开连接 再关闭mq
        if (!isConnect)
            return;
        mqttClient.disconnect();
        mqttClient.close();

    }

    @Override
    public void sub(String theme) throws Exception {
        //订阅主题
        mqttClient.subscribe(theme);
    }

    @Subscribe()
    public void onMessageSend(MqMssage mqMssage) {
        Log.e(TAG, " 收到消息:" + mqMssage.toString());
        try {
            send(mqMssage);
        } catch (Exception e) {
            e.printStackTrace();
            sendFailed();
        }

    }

    private void sendFailed() {
        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq send data failed!";
        EventBus.getDefault().post(serverMsg);
    }

    @Subscribe()
    public void onOperate(MqOperate mqOperate) {
        Log.e(TAG, " 收到操作:" + mqOperate.toString());

        switch (mqOperate.type) {

            case connect:
                serverUrl = "tcp://" + mqOperate.msg.ip + ":" + mqOperate.msg.port;
                clientid = mqOperate.msg.clientId;
                theme = mqOperate.msg.theme;
                try {
                    connect();
                    sub(mqOperate.msg.sub);
                    sendCennect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case reconnect:
                break;

            case close:

                try {
                    disconnect();
                    isConnect = false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sendClose();
                }
                break;
        }

    }

    private void sendComplete() {
        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq send data complete!";
        EventBus.getDefault().post(serverMsg);
    }

    private void sendCennect() {

        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq connect success!";
        EventBus.getDefault().post(serverMsg);
    }

    private void sendClose() {

        ServerMsg serverMsg = new ServerMsg();
        serverMsg.from = ServerMsg.Type.server;
        serverMsg.msg = "mq connect closed !";
        EventBus.getDefault().post(serverMsg);
    }


}
