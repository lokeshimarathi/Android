package com.silicon.android.silicon.world.people;

public class PeopleList {
    String profileImage, name, userName, gender, usersAuthUserId;

    public PeopleList(String profileImage, String name, String userName, String gender, String usersAuthUserId) {
        this.profileImage = profileImage;
        this.name = name;
        this.userName = userName;
        this.gender = gender;
        this.usersAuthUserId = usersAuthUserId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getGender() {
        return gender;
    }

    public String getUsersAuthUserId() {
        return usersAuthUserId;
    }
}
