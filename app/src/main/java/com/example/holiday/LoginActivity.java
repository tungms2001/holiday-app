package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText txtAccount;
    private EditText txtPassword;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String username;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        map();

        preferences = getSharedPreferences("com.example.holiday.SESSION", MODE_PRIVATE);
        editor = preferences.edit();

        username = preferences.getString("com.example.holiday.SESSION_USERNAME", null);
        if(username != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("account", txtAccount.getText().toString())
                    .add("password", txtPassword.getText().toString())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try{
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.getBoolean("success")){
                            editor.putString("com.example.holiday.SESSION_USERNAME", jsonObject.getString("username"));
                            editor.putString("com.example.holiday.SESSION_ROLE", jsonObject.getString("role"));
                            editor.apply();
                            username = jsonObject.getString("username");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Account or password incorrect!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        });
    }
    
    private void map(){
        btnLogin = findViewById(R.id.btn_login);
        txtAccount = findViewById(R.id.txt_account);
        txtPassword = findViewById(R.id.txt_password);
    }
}