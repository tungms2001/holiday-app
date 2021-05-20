package com.example.holiday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holiday.helper.Session;

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
    private TextView tvForgotPassword;
    private Button btnSignup;

    private Session session;
    private String username;
    private static int REQUEST_CODE = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        map();

        session = new Session(LoginActivity.this);
        username = session.getUsername();
        if (username != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        btnLogin.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("account", txtAccount.getText().toString())
                    .add("password", txtPassword.getText().toString())
                    .build();

            String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=user&action=login";
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success")) {
                            session.setSession(
                                    jsonObject.getString("username"),
                                    txtPassword.getText().toString(),
                                    jsonObject.getString("role"));
                            username = jsonObject.getString("username");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Account or password incorrect!", Toast.LENGTH_SHORT).show());
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                txtAccount.setText(data.getStringExtra("account"));
                txtPassword.setText(data.getStringExtra("password"));
                btnLogin.performClick();
            }
        }
    }

    private void map() {
        btnLogin = findViewById(R.id.btn_login);
        txtAccount = findViewById(R.id.txt_account);
        txtPassword = findViewById(R.id.txt_password);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        btnSignup = findViewById(R.id.btn_signup);
    }
}