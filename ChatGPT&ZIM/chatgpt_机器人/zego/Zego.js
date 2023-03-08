
var fs = require('fs');
//先清理缓存
fs.readdirSync('./local_storage').forEach(function (fileName) {
    fs.unlinkSync('./local_storage/' + fileName);
});

const KEY_CENTER = require("./KeyCenter.js");
const APPID = KEY_CENTER.APPID, SERVER_SECRET = KEY_CENTER.SERVER_SECRET;
const generateToken04 = require('./TokenUtils.js').generateToken04;
var LocalStorage = require('node-localstorage').LocalStorage;
localStorage = new LocalStorage('./local_storage');
var indexedDB = require("fake-indexeddb/auto").indexedDB;
const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const dom = new JSDOM(``, {
    url: "http://localhost/",
    referrer: "http://localhost/",
    contentType: "text/html",
    includeNodeLocations: true,
    storageQuota: 10000000
});
window = dom.window;
document = window.document;
navigator = window.navigator;
location = window.location;
WebSocket = window.WebSocket;
XMLHttpRequest = window.XMLHttpRequest;
const ZIM = require('./index.js').ZIM;
// console.log("<<<",window.WebSocket)

function newToken(userId) {
    const token = generateToken04(APPID, userId, SERVER_SECRET, 60 * 60 * 24, '');
    return token;
}

function createZIM(onError, onRcvMsg, onTokenWillExpire) {
    var zim = ZIM.create(APPID);
    zim.on('error', onError);
    zim.on('receivePeerMessage', function (zim, msgObj) {
        console.log("收到P2P消息")
        onRcvMsg(false, zim, msgObj)
    });
    // 收到群组消息的回调
    zim.on('receiveGroupMessage', function (zim, msgObj) {
        console.log("收到群组消息")
        onRcvMsg(true, zim, msgObj)
    });

    zim.on('tokenWillExpire', onTokenWillExpire);
    return zim;
}
function login(zim, userId, token, cb) {
    var userInfo = { userID: userId, userName: userId };

    zim.login(userInfo, token)
        .then(function () {
            cb(true, null);
        })
        .catch(function (err) {
            cb(false, err);
        });
}
function sendP2PMsg(zim, toUserId, text, cb = null) {
    var config = {
        priority: 1 // 消息优先级，取值为 低:1 默认,中:2,高:3
    };
    var msgTxtObj = { type: 1, message: text };
    zim.sendPeerMessage(msgTxtObj, toUserId, config)
        .then(function ({ message }) {
            cb && cb(true, null);
        })
        .catch(function (err) {
            cb && cb(false, err);
        });
}
function sendGroupMessage(zim, toGroup, text, cb = null) {
    console.log("准备发送群消息：", text)
    var config = {
        priority: 1 // 消息优先级，取值为 低:1 默认,中:2,高:3
    };
    var msgTxtObj = { type: 1, message: text };
    zim.sendGroupMessage(msgTxtObj, toGroup, config)
        .then(function ({ message }) {
            cb && cb(true, null);
            console.log("群消息发送成功")
        })
        .catch(function (err) {
            cb && cb(false, err);
        });

}

// var ___clientUID = null;
function sendMsg(zim, isGroup, msg, toUID, cb) {
    if (isGroup) sendGroupMessage(zim, toUID, msg, cb);
    else sendP2PMsg(zim, toUID, msg, cb);

    // var toConversationID = toUID; // 对方 userID
    // var conversationType = isGroup ? 2 : 0; // 会话类型，取值为 单聊：0，房间：1，群组：2
    // var config = {
    //     priority: 1, // 设置消息优先级，取值为 低：1（默认），中:2，高：3
    // };

    // var messageTextObj = { type: 1, message: msg, };
    // var notification = {
    //     onMessageAttached: function (message) {
    //         // todo: Loading
    //     }
    // }

    // zim.sendMessage(messageTextObj, toConversationID, conversationType, config, notification)
    //     .then(function ({ message }) {
    //         // 发送成功
    //         cb && cb(true, null);
    //     })
    //     .catch(function (err) {
    //         // 发送失败
    //         cb && cb(false, err);
    //     });


}
function initZego(onError, onRcvMsg, myUID) {
    // ___clientUID = clientUId;
    var token = newToken(myUID);
    var startTimestamp = new Date().getTime();
    function _onError(zim, err) {
        onError(err);
    }
    function _onRcvMsg(isFromGroup, zim, msgObj) {
        var msgList = msgObj.messageList;
        var fromConversationID = msgObj.fromConversationID;
        msgList.forEach(function (msg) {
            if (msg.timestamp - startTimestamp >= 0) { //过滤掉离线消息
                console.log(msg)
                onRcvMsg(isFromGroup, msg, fromConversationID);
            }
        })
    }
    function onTokenWillExpire(zim, second) {
        token = newToken(userId);
        zim.renewToken(token);
    }
    var zim = createZIM(_onError, _onRcvMsg, onTokenWillExpire);
    login(zim, myUID, token, function (succ, data) {
        if (succ) {
            console.log("登录成功！")
            //直接加入指定的群，为了简化，这里直接硬编码要加入的群ID
            zim.joinGroup("group_chatgpt").catch(function (err) {
                console.log("已加入群")
            });
        } else {
            console.log("登录失败！", data)
        }
    })
    return zim;
}
module.exports = {
    initZego: initZego,
    sendMsg: sendMsg
}