package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText txtFullname;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        map();

        btnSignup.setOnClickListener(v -> {
            List<String> listPrompts = new Vector<>();

            if (txtFullname.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.full_name));
            if (txtUsername.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.username));
            if (txtEmail.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.email));
            if (txtPhone.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.phone));
            if (txtPassword.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.password));
            if (txtConfirmPassword.getText().toString().isEmpty())
                listPrompts.add(getString(R.string.confirm_password));

            if (listPrompts.size() == 0) {
                if (isValidEmail(txtEmail.getText())) {
                    if (txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("fullname", txtFullname.getText().toString())
                                .add("username", txtUsername.getText().toString())
                                .add("email", txtEmail.getText().toString())
                                .add("phone", txtPhone.getText().toString())
                                .add("password", txtPassword.getText().toString())
                                .build();
                        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=User&action=signup";
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                SignupActivity.this.runOnUiThread(() -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        if (jsonObject.getBoolean("success")) {
                                            Intent intent = new Intent();
                                            intent.putExtra("account", txtUsername.getText().toString());
                                            intent.putExtra("password", txtPassword.getText().toString());
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                        Toast.makeText(SignupActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        });
                    }
                    else {
                        Toast.makeText(SignupActivity.this, "Password not matches", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SignupActivity.this, "Please type a valid email", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                String emptyPrompt = "Please type ";
                for (String item: listPrompts)
                    emptyPrompt += item + ", ";
                emptyPrompt = emptyPrompt.substring(0, emptyPrompt.length() - 2);
                Toast.makeText(SignupActivity.this, emptyPrompt, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void map() {
        txtFullname = findViewById(R.id.txt_fullname);
        txtUsername = findViewById(R.id.txt_username);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}