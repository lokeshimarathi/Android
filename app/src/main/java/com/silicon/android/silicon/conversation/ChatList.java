package com.silicon.android.silicon.conversation;

public class ChatList {
    String usersAuthUserId, authUserId, usersProfileImage, profileImage, msg, date, time, condition,msgType,mediaType,caption,media,deleteCondition,chatKey, messageKey;
    private boolean read; // indicates if the message is read
    private boolean sent; // indicates if the message is sent by the current user


    public ChatList(String usersAuthUserId, String authUserId, String usersProfileImage, String profileImage, String msg, String date, String time, boolean read, boolean sent, String condition, String msgType, String mediaType, String caption, String media, String deleteCondition, String chatKey, String messageKey ) {
        this.usersAuthUserId = usersAuthUserId;
        this.authUserId = authUserId;
        this.usersProfileImage = usersProfileImage;
        this.profileImage = profileImage;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.condition = condition;
        this.msgType = msgType;
        this.mediaType = mediaType;
        this.media = media;
        this.deleteCondition = deleteCondition;
        this.caption = caption;
        this.chatKey = chatKey;
        this.messageKey = messageKey;

    }

    public String getUsersAuthUserId() {
        return usersAuthUserId;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public String getUsersProfileImage() {
        return usersProfileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public boolean isRead() {
        return read;
    }

    public boolean isSent() {
        return sent;
    }
    public String getCondition(){
        return condition;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getCaption() {
        return caption;
    }

    public String getMedia() {
        return media;
    }

    public String getDeleteCondition() {
        return deleteCondition;
    }
    public String getChatKey() {
        return chatKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
