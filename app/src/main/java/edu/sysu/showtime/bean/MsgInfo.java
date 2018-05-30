package edu.sysu.showtime.bean;

public class MsgInfo {
    public int senderLevel;         //发送者的等级
    public String senderId;         //发送者的id
    public String senderName;       //发送者的名字
    public String senderFaceUrl;    //发送者的头像
    public String chatContent;       //聊天的内容

    public MsgInfo(String userId, String userName, String faceUrl, String content){
        senderLevel = 1;
        senderId = userId;
        senderName = userName;
        senderFaceUrl = faceUrl;
        chatContent = content;
    }
}
