# ChatGPT-ZIM
基于ChatGPT和ZIM生成群聊机器人，群聊中@ChatGPT即可向其提问.

# 标题
当我把ChatGPT拉进群聊里，我的朋友都玩疯了

# 前言
近期ChatGPT可以说是太火了，问答、写论文、写诗、写代码，只要输入精确的prompt，他的表现总是让人惊喜。本着打不过就加入的原则。要是把ChatGPT拉入群聊中，会是怎样一番场景？说做就做，花了1个晚上捣鼓了一个小Demo【ChatGPT群聊助手】，凭借它的“聪明才智”，应该可以搞定我的网友、女朋友、老妈的提问...

**温馨提示：如果你从没体验过ChatGPT，给大家准备了一个新手体验Demo,免注册!免登陆!免代理!!!!!!,拉到文末可以快速查看噢。**


# 使用效果
效果可看下图

![对话效果图](./images/1.png)

 # 应用前景
虽Demo仅在小范围的群聊中测试，但ChatGPT语义理解和交互能力确实强大，不仅能联系对话的上下文，还能及时纠正代码bug。不经让人想到，若能将ChatGPT应用于聊天机器人软件，完成回答问题、提供服务、甚至解决问题的任务，帮助人们解决重复性或大量的人工工作，代替传统聊天机器人应用于客服、电商、教育和金融等行业。
相对于传统聊天机器人，ChatGPT可根据用户的要求和特性，及时调整回答的策略以便更准确的回答问题，有更人性化的体验。现在被广泛使用的智能客服还不够智能，ChatGPT所具备的能力，正是客服领域所需要的。


# 1 准备工作
在国内无法注册ChatGPT账户，因此需要准备如下：

**能接收短信的国外手机号：** 只需花几块钱，使用国外虚拟号码在线接收短信。可以去一些第三方平台如：http://sms-activate.org/cn。

**国外IP：** 可以通过一些VPN来实现，如通过VPN使用美国节点IP。

**这里需要注意的是,sms-activate.org选取手机号码国家的时候，建议选择印度，如果选择印度尼西亚，会在openAI报如下错误：**
```
You’ve made too many phone verification requests. Please try again later or contact us through our help center at help.openai.com
```

![选取国家](./images/4.png)

以上是必须的前提工作，有了以上准备工作后，就可以去[https://chat.openai.com/auth/login](https://chat.openai.com/auth/login)注册账号了。

# 2 实现思路
## 2.1 技术现状
`chatGPT`提供了基于`Web`版的交互界面，不便于编程式调用。于是，我们可以通过模拟浏览器去登录，然后把交互过程封装成`API`接口。

## 2.2 实现过程
`ChatGPT`作为一个机器人角色加入群聊，需要在PC端转发`ChatGPT`问答。因此，我们可以在PC电脑上完成ChatGPT接口的封装，并加入群聊。然后通过[即构IM](https://doc-zh.zego.im/article/11598)（群聊）将数据实时传输，实现群聊里面与`ChatGPT`聊天。
![实现pipeline](./images/2.png)

# 3 PC端封装代码实现
# 3.1 封装chatGPT调用
我们使用[chatgpt-api](https://github.com/transitive-bullshit/chatgpt-api)库来封装调用chatGPT，因此先要安装好依赖库：
```cmd
npm install chatgpt
```
安装好chtgpt库后，使用起来就非常简单了：
```javascript
var ChatGPT, ConversationId, ParentMessageId;
var API_KEY = ;//这里填写KEY
(async () => {
    const { ChatGPTAPI } = await import('chatgpt');
    ChatGPT = new ChatGPTAPI({ apiKey: API_KEY})
})();
//向ChatGPT发出提问
function chat(text, cb) {
    console.log("正在向ChatGPT发送提问:",text)
    ChatGPT.sendMessage(text, {
        conversationId: ConversationId,
        parentMessageId: ParentMessageId
    }).then(
        function (res) {
            ConversationId = res.conversationId
            ParentMessageId = res.id
            cb && cb(true, res.text)
            console.log(res)
        }
    ).catch(function (err) {
        cb && cb(false, err);
    });
}
```
注意到，在第二行需要填写`API_KEY`，登录OpenAI后，打开链接[https://platform.openai.com/account/api-keys](https://platform.openai.com/account/api-keys)即可获取，如下图所示

![获取API Key](./images/3.png)

 


# 3.2 收发群聊消息
关于即构IM，如果大家感兴趣可以进入官网[https://doc-zh.zego.im](https://doc-zh.zego.im)了解更多。总所周知，在即时聊天和实时音视频方面，[即构IM](https://doc-zh.zego.im/article/11598)是个人开发者或者中小型企业首选。因为我们只关注一对一私聊或者群聊，因此，在官方提供的SDK的基础上，我们做了二次封装。具体的封装代码请看附件，这里只贴出封装后的使用代码：

```javascript
const Zego = require('./zego/Zego.js');

var zim;
function onError(err) {
    console.log("on error", err);
} 
//发送消息
function sendZegoMsg(isToGroup, text, toID){
    Zego.sendMsg(zim, isToGroup, text, toID, function (succ, err) {
        if (!succ) {
            console.log("回复即构消息发送失败:", msg, err);
        }
    }) 
}
//收到消息回调
function onRcvZegoMsg(isFromGroup, msg, fromUID) { 
    var rcvText = msg.message ;
    
}
function main() {
    let zegoChatGPTUID = "chatgpt"
    zim = Zego.initZego(onError, onRcvZegoMsg, zegoChatGPTUID);

}
main();
```
在收到消息时，判断是否有`@chatgpt`关键字，如果有的话提取消息内容，然后去调用`chatGPT`封装好的接口等待`ChatGPT`回复，并将回复的内容往聊天群里发送。



# 4 手机端加入群聊与ChatGPT聊天
有了`PC`端实现后，接下来在手机端只需通过[即构IM SDK](https://doc-zh.zego.im)向群里面@chatgpt发送提问消息即可，当然了，也可以在一对一私聊的时候@chatgpt然后调用chatGPT接口。这些都是可以根据实际需求定制开发，篇幅原因，这里我们只将群聊。


同样的，我们只关注收发消息，因此对[即构官方](https://doc-zh.zego.im/article/11568)提供的SDK做了二次封装。如果想了解更多细节可以前往[官方文档](https://doc-zh.zego.im/article/11568)阅读。

对登录ZIM、创建Token等代码这里不详细描述，感兴趣读者可以查看代码附件，代码很简单容易看懂。

首先封装Msg对象，表示消息实体类：
```java
public class Msg {
    public String msg;
    public long time;
    public String toUID;
    public String fromUID;
    public MsgType type;

    public enum MsgType {
        P2P,
        GROUP
    }
}
```

发送消息二次封装，同一群聊和一对一聊天接口：
```java
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
```

二次封装接收消息，统一通过`onRcvMsg`函数接收消息。
```java
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
```
需要注意的是，因为我们目前场景只需关注文本消息，因此没有图片、文件之类的消息做过多考虑。如果有类似需求的读者可以根据官方文档进一步封装。

另外，为了简化，避免每次用户主动拉`chatgpt`进入一个新群，我们先约好一个超大群ID:`group_chatgpt`。每次新用户登录就加入这个大群就好。如果有更加细粒度控制需求，可以根据不同用户来创建不同群，然后向`chatgpt`机器人发送群ID，在PC端开发对应的自动加入对应群功能就好。

对于加群逻辑，也做了二次封装：
```java
public void joinGroup(String groupId) {
    zim.joinGroup(groupId, new ZIMGroupJoinedCallback() {
        @Override
        public void onGroupJoined(ZIMGroupFullInfo groupInfo, ZIMError errorInfo) {
            for (MsgCenterListener l : lsArr)
                l.onJoinGroup(groupId);
        }
});
```
至此，整个流程开发完成，尽情享受ChatGPT吧。

# 5 开发者福利

除ChatGPT之外，Demo中使用的开发者工具ZIM SDK也是提升工作效率的利器，ZIM SDK提供了全面的 IM 能力，满足文本、图片、语音等多种消息类型，在线人数无上限，支持亿量级消息并发。同时支持安全审核机制，确保消息安全合规。

ZIM SDK提供了快速集成、接口丰富、成熟的即时通讯解决方案。满足多种业务场景通讯需求，适用于打造大型直播、语聊房、客服系统等场景。即构即时通讯产品 IM 开春钜惠低至1折，限时折扣专业版1200元https://www.zego.im/activity/zegoland，也可搭配元宇宙和直播间其他产品组合使用。感兴趣的开发者可到即构官网去注册体验https://doc-zh.zego.im/article/11591


# 6 完整代码

- PC端：http://xxxx.xxx
- android：http://xxxx.xxx
