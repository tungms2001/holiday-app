package com.example.holiday.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holiday.R;
import com.example.holiday.UpdateProfileActivity;
import com.example.holiday.helper.Session;
import com.google.android.gms.common.util.Base64Utils;
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

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvFullname;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnUpdate;
    private Button btnLogout;

    private Session session;
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvFullname = view.findViewById(R.id.tv_fullname);
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnLogout = view.findViewById(R.id.btn_logout);

        refreshData();

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            session.unsetSession();
            getActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        session = new Session(getActivity());
        username = session.getUsername();

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=User&action=detail&username=" + username;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(() -> {
                        try {
                            String url = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("avatar");
                            Picasso.get().load(url).into(ivAvatar);
                            tvFullname.setText(jsonObject.getString("fullname"));
                            tvUsername.setText(username);
                            tvEmail.setText(jsonObject.getString("email"));
                            tvPhone.setText(jsonObject.getString("phone"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}