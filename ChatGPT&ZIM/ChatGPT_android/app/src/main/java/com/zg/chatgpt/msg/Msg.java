package com.zg.chatgpt.msg;

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

    public Msg(MsgType type, String msg, long time, String fromUID, String toUID) {
        this.msg = msg;
        this.time = time;
        this.fromUID = fromUID;
        this.toUID = toUID;
        this.type = type;
    }
}
