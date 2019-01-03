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
        startService(new Intent(this, Mqtt_client_server.class));
    }

    @OnClick(R.id.bt_conn)
    public void conn() {
        Mess event = new Mess();
        event.type = Mess.Type.mq_operate;
        event.msg = "conn";
        EventBus.getDefault().post(event);
    }

    @OnClick(R.id.bt_send)
    public void send() {
        if (TextUtils.isEmpty(etMess.getText().toString())) {
            Toast.makeText(this, "没有内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Mess event = new Mess();
        event.type = Mess.Type.mq_operate;
        event.msg = "send";
        event.data =etMess.getText().toString();
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void msg(Mess msg) {
        if (msg == null)
            return;
        Log.e(getClass().getSimpleName(), " 收到消息：" +msg.toString());
        if (msg.type == Mess.Type.mq_message) {

            sbStr.append("编号" + "" + "收到：" + msg.data);
            return;
        }

        if (msg.type == Mess.Type.server) {
            sbStr.append( "收到服务消息：" +msg.toString());

            return;
        }
    }
}
