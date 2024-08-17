package com.silicon.android.silicon.conversation;

public class MyPeopleRequestList {
    String profileImage, userName, name, authUserId, gender;

    public MyPeopleRequestList(String profileImage, String userName, String name, String gender, String authUserId) {
        this.profileImage = profileImage;
        this.userName = userName;
        this.name = name;
        this.gender = gender;
        this.authUserId = authUserId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAuthUserId() {
        return authUserId;
    }
}
