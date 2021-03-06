package com.example.holiday.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Session { //lớp này dùng để lưu trang thái đăng nhập
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Session(Context context) {
        preferences = context.getSharedPreferences("com.example.holiday.SESSION", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setSession(String username, String password, String role) {
        editor.clear();
        editor.putString("com.example.holiday.SESSION_USERNAME", username);
        editor.putString("com.example.holiday.SESSION_PASSWORD", password);
        editor.putString("com.example.holiday.SESSION_ROLE", role);
        editor.apply();
    }

    public void unsetSession() {
        editor.clear();
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString("com.example.holiday.SESSION_USERNAME", null);
    }

    public String getPassword() {
        return preferences.getString("com.example.holiday.SESSION_PASSWORD", null);
    }

    public void setPassword(String password) {//lưu mật khẩu
        editor.remove("com.example.holiday.SESSION_PASSWORD");
        editor.apply();
        editor.putString("com.example.holiday.SESSION_PASSWORD", password);
        editor.apply();
    }

    public String getRole() {//tạo quyền admin
        return preferences.getString("com.example.holiday.SESSION_ROLE", null);
    }
}