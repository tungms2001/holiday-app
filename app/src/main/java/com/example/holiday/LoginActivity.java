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

    private static final int LOGIN = 1;
    private static final int SIGNUP  = 2;
    private Button btnLogin;
    private EditText txtAccount;
    private EditText txtPassword;
    private Button btnSignup;

    private Session session;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        map();

        session = new Session(LoginActivity.this);//lưu trạng thái đăng nhập
        username = session.getUsername();
        if (username != null) {//kiểm tra trạng thái đăng nhập
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(intent, LOGIN);//nếu đã lưu rồi thì ta nhảy thẳng vào trong nội dung app
        }
//còn nếu trả kết quả về là null thì thực hiện phần dưới đây
        btnLogin.setOnClickListener(v -> {//bấm vào nút login thì dữ liệu sẽ được gửi lên server
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
                    try {//dữ liệu được trả về khi đăng nhập thành công
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success")) {
                            session.setSession(
                                    jsonObject.getString("username"),
                                    txtPassword.getText().toString(),
                                    jsonObject.getString("role"));
                            username = jsonObject.getString("username");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivityForResult(intent, LOGIN);//bắt sự kiện qua main_activity
                        }
                        else {//báo lỗi nếu thông tin không đúng
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Account or password incorrect!", Toast.LENGTH_SHORT).show());
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        btnSignup.setOnClickListener(v -> {//bắt sự kiện bấm nút tạo tài khoãn sẽ qua activity singup
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivityForResult(intent, SIGNUP);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//thực hiện cú pháp đăng kí, nếu thành công thông tin sẽ tự điền vào Login
        if (resultCode == RESULT_OK) {//sau đó tự bấm vào đăng nhập
            if (requestCode == SIGNUP) {
                txtAccount.setText(data.getStringExtra("account"));
                txtPassword.setText(data.getStringExtra("password"));
                btnLogin.performClick();
            }
            else {
                String close = data.getData().toString();
                if (close.equals("close"))
                    finishAndRemoveTask();
            }
        }
    }

    private void map() {//ánh xạ với xml
        btnLogin = findViewById(R.id.btn_login);
        txtAccount = findViewById(R.id.txt_account);
        txtPassword = findViewById(R.id.txt_password);
        btnSignup = findViewById(R.id.btn_signup);
    }
}