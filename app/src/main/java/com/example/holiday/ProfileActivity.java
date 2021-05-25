package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holiday.helper.CircleTransform;
import com.example.holiday.helper.Session;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private Session session;

    private ImageView ivAvatar;
    private TextView tvFullname;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnUpdate;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        map();

        session = new Session(ProfileActivity.this);
        refreshData();

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            session.unsetSession();
            finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=user&action=get_detail&username=" + session.getUsername();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ProfileActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String url = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("avatar");
                        Picasso.get().load(url).transform(new CircleTransform()).into(ivAvatar);
                        tvFullname.setText(jsonObject.getString("fullname"));
                        tvUsername.setText(String.format(getString(R.string.at), session.getUsername()));
                        tvEmail.setText(jsonObject.getString("email"));
                        tvPhone.setText(jsonObject.getString("phone"));
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void map() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvFullname = findViewById(R.id.tv_fullname);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        btnUpdate = findViewById(R.id.btn_update);
        btnLogout = findViewById(R.id.btn_logout);
    }
}