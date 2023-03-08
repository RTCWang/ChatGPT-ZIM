package com.zg.chatgpt.zego;

public class KeyCenter {
    // 控制台地址: https://console.zego.im/dashboard
    // 可以在控制台中获取APPID，并将APPID设置为long型，例如：APPID = 123456789L.
    public static long APPID = ;  //这里填写APPID
    // 在控制台找到ServerSecret，并填入如下
    public static String SERVER_SECRET = ; //这里填写服务器端密钥
    /**
     * !!!!!!!!!!!!!
     * 注意，APPID和SERVER_SECRET不应该在客户端暴露，而应该在服务器端使用
     * 这里暴露了主要是为了演示生成token，实际线上app强烈建议不要在端上执行生成token算法，以免泄露APPID和SERVER_SECRET
     * !!!!!!!!!!!!!!
     * **/
}
