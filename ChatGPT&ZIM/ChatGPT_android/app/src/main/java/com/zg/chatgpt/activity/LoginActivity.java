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
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.zego.zim.callback.ZIMGroupCreatedCallback;
import im.zego.zim.callback.ZIMGroupJoinedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.entity.ZIMGroupAdvancedConfig;
import im.zego.zim.entity.ZIMGroupFullInfo;
import im.zego.zim.entity.ZIMGroupInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;


public class LoginActivity extends BaseActivity implements MsgCenterListener, View.OnClickListener {

    //机器人chat gpt的id
    public static final String ChatGPT_ID = "chatgpt";
    public static final String ChatGPT_GROUP = "group_chatgpt";

    private MsgCenter msgCenter;
    private EditText nickNameEt;
    private TextView tipTV;
    private Button loginBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        loginBtn = findViewById(R.id.joinRoomBtn);
        nickNameEt = findViewById(R.id.nickNameEt);
        tipTV = findViewById(R.id.nameTip);
        loginBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        tipTV.setVisibility(View.INVISIBLE);
        String name = nickNameEt.getText().toString().trim();
        if (name.length() <= 0) {
            tipTV.setText("请输入昵称");
            tipTV.setVisibility(View.VISIBLE);
        } else if (name.toLowerCase().equals("chatgpt")) {
            tipTV.setText("昵称已被占用");
            tipTV.setVisibility(View.VISIBLE);
        } else {
            msgCenter = MsgCenter.getInstance(getApplication(), name);
            msgCenter.addListener(this);
            msgCenter.login(name);
        }
    }

    private void openChatActivity() {
        String name = nickNameEt.getText().toString().trim();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("name", name);
        intent.putExtra("toUID", "group_chatgpt");
        intent.putExtra("isGroupType", true);
        startActivity(intent);
    }

    @Override
    public void onLogin(boolean isSucc, String errMsg) {
        if (!isSucc) {
            toast("昵称已被占用" + errMsg);
            return;
        }
        String name = nickNameEt.getText().toString().trim();
        ArrayList<String> userList = new ArrayList<>();
        userList.add(name);
        userList.add(ChatGPT_ID);
        msgCenter.joinGroup(ChatGPT_GROUP);
    }

    @Override
    public void onJoinGroup(String groupId) {//创建群组或加入群组成功
        openChatActivity();

    }

    @Override
    public void onRenewTokenError(String errMsg) {

    }

    @Override
    public void onRcvMsg(Msg msg) {

    }

    @Override
    public void onSendMsg(boolean isSucc, Msg msg, String errMsg) {

    }
}