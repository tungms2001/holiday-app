package com.example.holiday.helper;

public class Comment {
    private String avatar;
    private String fullname;
    private String content;

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

