package com.zg.chatgpt.zego;

import android.app.Application;
import android.util.Log;

import com.zg.chatgpt.msg.Msg;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMGroupCreatedCallback;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.entity.ZIMGroupAdvancedConfig;
import im.zego.zim.entity.ZIMGroupFullInfo;
import im.zego.zim.entity.ZIMGroupInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMPushConfig;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMMessagePriority;

public class Zego {

    public static ZIM createZIM(Application app, ZIMEventHandler handler) {
        // 创建 ZIM 对象，传入 APPID 与 Android 中的 Application
        ZIM zim = ZIM.create(KeyCenter.APPID, app);
        zim.setEventHandler(handler);
        return zim;
    }

    public static void createGroup(ZIM zim, String groupId, String groupName, ArrayList<String> userList, ZIMGroupCreatedCallback cb) {
        // 创建一个群组
        ZIMGroupInfo groupInfo = new ZIMGroupInfo();
        groupInfo.groupID = groupId;
        groupInfo.groupName = groupName;

        ZIMGroupAdvancedConfig config = new ZIMGroupAdvancedConfig();
//        HashMap<String, String> attributes = new HashMap<>();
//        attributes.put("key_0", "value_0");
//        attributes.put("key_1", "value_1");
//        attributes.put("key_2", "value_2");
//        config.groupAttributes = attributes;


        zim.createGroup(groupInfo, userList, config, cb);
//        new ZIMGroupCreatedCallback() {
//            @Override
//            public void onGroupCreated(ZIMGroupFullInfo groupInfo, ArrayList<ZIMGroupMemberInfo> userIDs, ArrayList<ZIMErrorUserInfo> errorUserList, ZIMError errorInfo) {
//                // 通过 errorInfo.code 获取创建群的结果
//            }
//        });
    }

    public static void login(ZIM zim, String token, String userId, ZIMLoggedInCallback cb) {
        // 登录时，需要开发者 按照 "使用 Token 鉴权" 文档生成 token 即可
        // userID 和 userName，最大 32 字节的字符串。仅支持数字，英文字符 和 '~', '!',
        // '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '=', '-', '`',
        // ';', '’', ',', '.', '<', '>', '/', '\'。
        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userId;
        zimUserInfo.userName = userId;
        zim.login(zimUserInfo, token, cb);
    }

    public static void sendMsg(ZIM zim, Msg msg, ZIMMessageSentCallback cb) {
        // 发送“单聊”通信的信息

        ZIMTextMessage zimMessage = new ZIMTextMessage();
        zimMessage.message = msg.msg;

        ZIMMessageSendConfig config = new ZIMMessageSendConfig();
        // 消息优先级，取值为 低:1 默认,中:2,高:3
        config.priority = ZIMMessagePriority.LOW;
        // 设置消息的离线推送配置
        ZIMPushConfig pushConfig = new ZIMPushConfig();
        pushConfig.title = "离线推送的标题";
        pushConfig.content = "离线推送的内容";
        pushConfig.extendedData = "离线推送的扩展信息";
        config.pushConfig = pushConfig;
        if (msg.type == Msg.MsgType.P2P)
            zim.sendPeerMessage(zimMessage, msg.toUID, config, cb);
        else
            zim.sendGroupMessage(zimMessage, msg.toUID, config, cb);
    }

    /**
     * !!!!!!!!!!!!!
     * 注意，为了安全起见，token生成应当在服务器端生成，再通过网络传递给客户端。
     * 这里主要是为了演示，实际线上app强烈建议不要在端上执行生成token算法，以免泄露APPID和SERVER_SECRET
     * !!!!!!!!!!!!!!
     * **/
    public static String getToken(String userId) {

        TokenServerAssistant.VERBOSE = true;    // 调试时，置为 true, 可在控制台输出更多信息；正式运行时，最好置为 false

        try {
            TokenServerAssistant.TokenInfo token = TokenServerAssistant.generateToken(KeyCenter.APPID, userId, KeyCenter.SERVER_SECRET, 60 * 60);

            Log.e(">>>", token.data);

            return token.data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}
