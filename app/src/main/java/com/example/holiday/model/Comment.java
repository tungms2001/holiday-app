package com.example.holiday.model;

public class Comment {
    private final String avatar;
    private final String fullname;
    private final String content;

    public Comment(String avatar, String fullname, String content) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullname() {
        return fullname;
    }

    public String getContent() {
        return content;
    }
}