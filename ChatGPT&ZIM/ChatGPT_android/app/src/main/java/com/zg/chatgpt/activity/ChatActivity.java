package com.zg.chatgpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zg.chatgpt.R;
import com.zg.chatgpt.msg.Msg;
import com.zg.chatgpt.msg.MsgCenter;
import com.zg.chatgpt.msg.MsgCenterListener;
import com.zg.chatgpt.utils.ChatAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatActivity extends BaseActivity implements MsgCenterListener, View.OnClickListener {
    private final static String TAG = "MainActivity";
    private String toUserId;
    private String myUserId;
    private Msg.MsgType msgType;

    protected EditText inputEt;
    protected TextView msgListTV;
    private int screenHeight = 0;
    private int keyHeight = 0;
    private List<Msg> msgs = new ArrayList<>();
    private MsgCenter msgCenter;
    private FrameLayout rootView;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private Button sendBtn;
    private EditText sendET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.myUserId = intent.getStringExtra("name");
        this.toUserId = intent.getStringExtra("toUID");
        this.msgType = intent.getBooleanExtra("isGroupType", true) ? Msg.MsgType.GROUP : Msg.MsgType.P2P;


        setContentView(R.layout.activity_main);
//        inputEt = findViewById(R.id.inputEt);
//        msgListTV = findViewById(R.id.msgList);
        msgCenter = MsgCenter.getInstance(getApplication(), myUserId);
        msgCenter.addListener(this);
//        msgCenter.login(myUserId);
        initView();
        updateMsg(true);

    }


    private void initView() {
        rootView = findViewById(R.id.root);

        initListener();
        initOtherData();

        adapter = new ChatAdapter(this, this.myUserId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rclView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        sendBtn = findViewById(R.id.sendBtn);
        sendET = findViewById(R.id.sendEt);
        sendBtn.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    recyclerView.scrollBy(0, keyHeight);
                    updateMsg(true);
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    recyclerView.scrollBy(0, -keyHeight);
                    updateMsg(true);
                }
            }
        });
    }


    private void initOtherData() {
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        keyHeight = screenHeight / 3;
    }

    public void updateMsg(boolean scrollBottom) {
        adapter.setData(msgs);
//        recyclerView.getLayoutManager().scrollToPosition(msgs.size() - 1);
//        if (scrollBottom)
        recyclerView.scrollToPosition(msgs.size() - 1);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRcvMsg(Msg msg) {
        msgs.add(msg);
        updateMsg(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msgCenter.logout();
    }

    @Override
    public void onClick(View view) {

        String content = sendET.getText().toString().trim();
        if (content.length() <= 0) return;
        Msg msg = new Msg(msgType, content, new Date().getTime(), myUserId, toUserId);
        msgCenter.sendZegoMsg(msg);
        sendET.setText("@chatgpt");
        msgs.add(msg);
        updateMsg(true);
    }

    @Override
    public void onLogin(boolean isSucc, String errMsg) {

    }

    @Override
    public void onRenewTokenError(String errMsg) {

    }

    @Override
    public void onJoinGroup(String groupId) {

    }

    @Override
    public void onSendMsg(boolean isSucc, Msg msg, String errMsg) {
        if (!isSucc) {
            Log.e(TAG, "消息发送失败:" + errMsg);
            toast(errMsg);
        }
    }
}