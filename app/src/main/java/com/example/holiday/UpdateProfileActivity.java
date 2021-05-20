package com.example.holiday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holiday.helper.CircleTransform;
import com.example.holiday.helper.Session;
import com.google.android.gms.common.util.Base64Utils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private Bitmap bitmap;
    private Session session;

    private ImageView ivUpdateAvatar;
    private EditText txtUpdateFullname;
    private TextView tvUsername;
    private EditText txtUpdateEmail;
    private EditText txtUpdatePhone;
    private CheckBox chkUpdatePassword;
    private RelativeLayout rlUpdatePassword;
    private EditText txtOldPassword;
    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        init();

        ivUpdateAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        });

        chkUpdatePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rlUpdatePassword.setVisibility(View.VISIBLE);
                ((RelativeLayout.LayoutParams) rlUpdatePassword.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.chk_update_password);
                ((RelativeLayout.LayoutParams) btnSave.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.rl_update_password);
            }
            else {
                rlUpdatePassword.setVisibility(View.GONE);
                ((RelativeLayout.LayoutParams) btnSave.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.chk_update_password);
            }
        });

        btnSave.setOnClickListener(v -> {
            String base64Data = "";
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapData = stream.toByteArray();
                base64Data = Base64Utils.encode(bitmapData);
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody body = null;

            if (session.getPassword().equals(txtOldPassword.getText().toString()) || !chkUpdatePassword.isChecked()) {
                if(txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                    body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("avatar", base64Data)
                            .addFormDataPart("username", session.getUsername())
                            .addFormDataPart("fullname", txtUpdateFullname.getText().toString())
                            .addFormDataPart("email", txtUpdateEmail.getText().toString())
                            .addFormDataPart("phone", txtUpdatePhone.getText().toString())
                            .addFormDataPart("new_password", txtNewPassword.getText().toString())
                            .build();
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "Password not matches!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(UpdateProfileActivity.this, "Old password incorrect!", Toast.LENGTH_SHORT).show();
            }

            if (body != null) {
                String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=user&action=update";
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) { }
                });
            }
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ivUpdateAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        ivUpdateAvatar = findViewById(R.id.iv_update_avatar);
        txtUpdateFullname = findViewById(R.id.txt_update_fullname);
        tvUsername = findViewById(R.id.tv_username);
        txtUpdateEmail = findViewById(R.id.txt_update_email);
        txtUpdatePhone = findViewById(R.id.txt_update_phone);
        rlUpdatePassword = findViewById(R.id.rl_update_password);
        chkUpdatePassword = findViewById(R.id.chk_update_password);
        txtOldPassword = findViewById(R.id.txt_update_old_password);
        txtNewPassword = findViewById(R.id.txt_update_new_password);
        txtConfirmPassword = findViewById(R.id.txt_update_confirm_password);
        btnSave = findViewById(R.id.btn_profile_save);

        session = new Session(UpdateProfileActivity.this);
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
                UpdateProfileActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String url = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("avatar");
                        Picasso.get().load(url).transform(new CircleTransform()).into(ivUpdateAvatar);
                        txtUpdateFullname.setText(jsonObject.getString("fullname"));
                        tvUsername.setText(String.format(getString(R.string.at), session.getUsername()));
                        txtUpdateEmail.setText(jsonObject.getString("email"));
                        txtUpdatePhone.setText(jsonObject.getString("phone"));
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}