package com.example.holiday.model;

public class Comment { //Một comment thường gồm 3 thành phần: ảnh đại diện, tên, và nội dung
    private final String avatar;
    private final String fullname;
    private final String content;

    public Comment(String avatar, String fullname, String content) {//mảng comment
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