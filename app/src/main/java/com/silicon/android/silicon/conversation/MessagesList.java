package com.silicon.android.silicon.conversation;

public class MessagesList {
 private final   String getUserTwo, name, lastMessage, usersProfileImage, getLastMessageDate, getLastMessageTime, chatKey;
 private final   int unseenMessages;

    public MessagesList(String getUserTwo, String name, String lastMessage, String usersProfileImage, String getLastMessageDate, String getLastMessageTime, String chatKey, int unseenMessages) {
        this.getUserTwo = getUserTwo;
        this.name = name;
        this.lastMessage = lastMessage;
        this.usersProfileImage = usersProfileImage;
        this.getLastMessageDate = getLastMessageDate;
        this.getLastMessageTime = getLastMessageTime;
        this.chatKey = chatKey;
        this.unseenMessages = unseenMessages;
    }

    public String getGetUserTwo() {
        return getUserTwo;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getUsersProfileImage() {
        return usersProfileImage;
    }

    public String getGetLastMessageDate() {
        return getLastMessageDate;
    }

    public String getGetLastMessageTime() {
        return getLastMessageTime;
    }

    public String getChatKey() {
        return chatKey;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }
}
