package com.chaoya.aidlimone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoya.aidlim.IRemoteService;
import com.chaoya.aidlim.UserMessage;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private String TAG = this.getClass().getSimpleName();

    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.send)
    TextView send;
    @BindView(R.id.list_view)
    ListView listView;

    private LinkedList<String> list = new LinkedList<>();
    private ArrayAdapter<String> adapter;
    private IRemoteService remoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        Intent intent2 = new Intent().setComponent(new ComponentName(
                "com.chaoya.aidlimone",
                "com.chaoya.aidlimone.RemoteService"));
        bindService(intent2, mConnection2, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");
            remoteService = IRemoteService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection mConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");

            RemoteService.InnerIBinder innerIBinder = (RemoteService.InnerIBinder) service;
            RemoteService remoteService = (RemoteService) innerIBinder.getService();
            remoteService.setCallBack(new RemoteService.CallBack() {
                @Override
                public void showMessage(UserMessage message) {
                    list.addFirst("男孩说:" + message.messageContent);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @OnClick(R.id.send)
    public void onViewClicked() {
        if ("".equals(content.getText().toString().trim())) {
            Toast.makeText(this, "请输入信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (remoteService == null) {
            Toast.makeText(this, "服务正在启动...", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent().setComponent(new ComponentName(
                    "com.chaoya.aidlimtwo",
                    "com.chaoya.aidlimtwo.RemoteService"));
            bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
            return;
        }


        try {
            remoteService.sendMessage(new UserMessage(content.getText().toString().trim()));
            list.addFirst("女孩说:" + content.getText().toString().trim());
            content.setText("");
            adapter.notifyDataSetChanged();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
