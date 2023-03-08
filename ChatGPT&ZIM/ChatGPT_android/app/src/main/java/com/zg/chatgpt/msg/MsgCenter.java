package com.zg.chatgpt.msg;

import android.app.Application;
import android.util.Log;


import com.zg.chatgpt.zego.Zego;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMGroupCreatedCallback;
import im.zego.zim.callback.ZIMGroupJoinedCallback;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.callback.ZIMTokenRenewedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.entity.ZIMGroupFullInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;

public class MsgCenter {
    private static String TAG = "MsgCenter";
    private static volatile MsgCenter instance = null;
    private LinkedBlockingQueue<Msg> msgQueue = new LinkedBlockingQueue<>();
    private ZIM zim;
    private long startTime = new Date().getTime();
    private long lastSendTime = 0;
    private Set<MsgCenterListener> lsArr = new HashSet<>();

    private String myUserId;


    private MsgCenter(Application application, String myUserId) {

        if (myUserId != null)
            this.myUserId = myUserId;
        zim = Zego.createZIM(application, handler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                procMsgQueue();
            }
        }).start();
    }


    /**
     * 收到消息
     */
    private void onRcvMsg(ArrayList<ZIMMessage> messageList) {
        if (lsArr == null) return;
        for (ZIMMessage zimMessage : messageList) {
            if (zimMessage instanceof ZIMTextMessage) {
                ZIMTextMessage zimTextMessage = (ZIMTextMessage) zimMessage;
                if (zimMessage.getTimestamp() < this.startTime)
                    continue;
                String fromUID = zimTextMessage.getSenderUserID();
                ZIMConversationType ztype = zimTextMessage.getConversationType();
                String toUID = zimTextMessage.getConversationID();
                Msg.MsgType type = Msg.MsgType.P2P;
                if (ztype == ZIMConversationType.PEER) type = Msg.MsgType.P2P;
                else if (ztype == ZIMConversationType.GROUP) type = Msg.MsgType.GROUP;
                String data = zimTextMessage.message;
                Msg msg = new Msg(type, data, zimMessage.getTimestamp(), fromUID, toUID);
                for (MsgCenterListener l : lsArr) l.onRcvMsg(msg);
            }
        }
    }

    public void logout() {
        zim.logout();
    }

    private void procMsgQueue() {
        while (true) {

            try {
                Msg msg = msgQueue.take();
                if (new Date().getTime() - lastSendTime < 300) {
                    Thread.sleep(300);
                }
                Log.e(TAG, "准备发送消息..." + msg.toUID + "," + msg);

                Zego.sendMsg(zim, msg, new ZIMMessageSentCallback() {
                    @Override
                    public void onMessageSent(ZIMMessage zimMessage, ZIMError error) {
                        if (error.getCode() == ZIMErrorCode.SUCCESS) {
                            Log.e(TAG, "发送成功..." + msg.toUID + "," + msg.msg);
                            for (MsgCenterListener l : lsArr) l.onSendMsg(true, msg, null);
                        } else {
                            for (MsgCenterListener l : lsArr)
                                l.onSendMsg(false, msg, error.getMessage());
                        }
                        lastSendTime = new Date().getTime();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 发送消息
     */
    public void sendZegoMsg(Msg msg) {
        try {
            msgQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 执行登录，所有操作之前必须执行登录
     */
    public void login(String userId) {

        String token = Zego.getToken(userId);
        Zego.login(zim, token, userId, new ZIMLoggedInCallback() {
            @Override
            public void onLoggedIn(ZIMError errorInfo) {

                if (errorInfo.getCode() != ZIMErrorCode.SUCCESS) {

                    for (MsgCenterListener l : lsArr) l.onLogin(false, errorInfo.getMessage());
                } else {
                    for (MsgCenterListener l : lsArr) l.onLogin(true, null);
//                    hasLogin = true;
//                    reqFriendsList(0);
//                    toast("登录成功！");
                }
            }
        });
    }

    /**
     * 加入群组
     **/
    public void joinGroup(String groupId) {
        zim.joinGroup(groupId, new ZIMGroupJoinedCallback() {
            @Override
            public void onGroupJoined(ZIMGroupFullInfo groupInfo, ZIMError errorInfo) {
                for (MsgCenterListener l : lsArr)
                    l.onJoinGroup(groupId);
            }
        });
//        Zego.createGroup(zim, groupId, groupName, userList, new ZIMGroupCreatedCallback() {
//            @Override
//            public void onGroupCreated(ZIMGroupFullInfo groupInfo, ArrayList<ZIMGroupMemberInfo> userList, ArrayList<ZIMErrorUserInfo> errorUserList, ZIMError errorInfo) {
//                if (errorInfo.code != ZIMErrorCode.SUCCESS) {//如果创建失败，说明已经存在群组，直接加入
//                    Log.e(TAG, "成功创建群组");
//                    zim.joinGroup(groupId, new ZIMGroupJoinedCallback() {
//                        @Override
//                        public void onGroupJoined(ZIMGroupFullInfo groupInfo, ZIMError errorInfo) {
//                            for (MsgCenterListener l : lsArr)
//                                l.onJoinGroup(groupId);
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "群组已存在");
//                    for (MsgCenterListener l : lsArr) l.onJoinGroup(groupId);
//                }
//            }
//        });
    }

    private ZIMEventHandler handler = new ZIMEventHandler() {

        @Override
        public void onReceivePeerMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromUserID) {
            onRcvMsg(messageList);
        }



        @Override
        public void onReceiveGroupMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromGroupID) {
            onRcvMsg(messageList);
        }

        @Override
        public void onTokenWillExpire(ZIM zim, int second) {
            onRenewToken();
        }
    };

    /**
     * token快过期了
     */
    private void onRenewToken() {
        zim.renewToken(Zego.getToken(myUserId), new ZIMTokenRenewedCallback() {
            @Override
            public void onTokenRenewed(String s, ZIMError zimError) {
                if (zimError.getCode() != ZIMErrorCode.SUCCESS) {
                    for (MsgCenterListener l : lsArr)
                        l.onRenewTokenError(zimError.getMessage());
                }

            }
        });
    }

    public void addListener(MsgCenterListener listener) {
        this.lsArr.add(listener);
    }

    public void rmListener(MsgCenterListener listener) {
        this.lsArr.remove(listener);
    }

    public void destroy() {
        zim.destroy();
    }

    public static MsgCenter getInstance(Application app, String myUserId) {
        if (instance == null) {
            synchronized (MsgCenter.class) {
                if (instance == null)
                    instance = new MsgCenter(app, myUserId);
            }
        }
        return instance;
    }
}
