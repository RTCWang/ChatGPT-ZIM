package com.zg.chatgpt.msg;


public interface MsgCenterListener {
    void onLogin(boolean isSucc, String errMsg);

    void onRenewTokenError(String errMsg);

    void onRcvMsg(Msg msg);

    void onSendMsg(boolean isSucc, Msg msg, String errMsg);

    void onJoinGroup(String groupId);
}
