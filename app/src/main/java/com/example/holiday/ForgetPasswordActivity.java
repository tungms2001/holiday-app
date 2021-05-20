package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText txtInput;
    private Button btnSubmit;
    private TextView tvResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        map();

        btnSubmit.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient();
            if (btnSubmit.getText().toString().equals("Send")) {
                if (isValidEmail(txtInput.getText())) {
                    RequestBody body = new FormBody.Builder()
                            .add("emmail", txtInput.getText().toString())
                            .build();
                    String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=user&action=reset";
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            ForgetPasswordActivity.this.runOnUiThread(() -> {
                                txtInput.setText("");
                                btnSubmit.setText("Submit");
                            });
                        }
                    });
                }
            } else {
                if (!txtInput.getText().toString().isEmpty()) {
                    RequestBody body = new FormBody.Builder()
                            .add("code", txtInput.getText().toString())
                            .build();
                    String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=User&action=verify";
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            ForgetPasswordActivity.this.runOnUiThread(() -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    if (jsonObject.getBoolean("success")) {
                                        tvResetPassword.setText("Your new password is " + jsonObject.getString("new_password"));
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void map() {
        txtInput = findViewById(R.id.txt_input);
        btnSubmit = findViewById(R.id.btn_submit);
        tvResetPassword = findViewById(R.id.tv_reset_password);
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}