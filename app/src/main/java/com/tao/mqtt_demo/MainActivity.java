package com.tao.mqtt_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tao.mqtt_demo.Message.Mess;
import com.tao.mqtt_demo.Message.MqMssage;
import com.tao.mqtt_demo.Message.MqOperate;
import com.tao.mqtt_demo.Message.ServerMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_ip)
    EditText etIP;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.et_mess)
    EditText etMess;
    @BindView(R.id.et_theme)
    EditText etTheme;
    @BindView(R.id.et_id)
    EditText etID;
    @BindView(R.id.et_sub)
    EditText etSub;
    @BindView(R.id.tv_rec)
    TextView tvRec;

    StringBuilder sbStr = new StringBuilder();

    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        startService(new Intent(this, Mqtt_client_server.class));
    }

    @OnClick(R.id.bt_conn)
    public void conn() {

        updataText("连接mqtt..");
        MqOperate mqOperate = new MqOperate();
        mqOperate.type = MqOperate.Type.connect;
        mqOperate.msg = new MqOperate.Message();

        mqOperate.msg.clientId = etID.getText().toString();
        mqOperate.msg.ip = etIP.getText().toString();
        mqOperate.msg.port = etPort.getText().toString();
        mqOperate.msg.sub = etSub.getText().toString();
        mqOperate.msg.theme = etTheme.getText().toString();

        EventBus.getDefault().post(mqOperate);

    }

    private void updataText(String s) {

        if (TextUtils.isEmpty(s))
            return;
        sbStr.append(s + "\n ");
        tvRec.setText(sbStr.toString());
    }

    @OnClick(R.id.bt_send)
    public void send() {
        if (TextUtils.isEmpty(etMess.getText().toString())) {
            Toast.makeText(this, "没有内容", Toast.LENGTH_SHORT).show();
            return;
        }
        updataText("发送 mq消息..");
        MqMssage mqMssage = new MqMssage();
        mqMssage.type = MqMssage.Type.send;
        mqMssage.message = etMess.getText().toString();
        EventBus.getDefault().post(mqMssage);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void msg(Mess msg) {
        if (msg == null)
            return;
        Log.e(getClass().getSimpleName(), " 收到消息：" + msg.toString());
        if (msg.type == Mess.Type.mq_message) {

            sbStr.append("编号" + "" + "收到：" + msg.data);
            return;
        }

        if (msg.type == Mess.Type.server) {
            sbStr.append("收到服务消息：" + msg.toString());

            return;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMss(ServerMsg msg) {

        if (msg == null)
            return;
        updataText(msg.msg);
    }

    @OnClick(R.id.bt_close)
    public void close() {

        updataText("关闭mq");
        MqOperate mqOperate = new MqOperate();
        mqOperate.type = MqOperate.Type.close;
        mqOperate.msg = new MqOperate.Message();
        EventBus.getDefault().post(mqOperate);

    }
}
