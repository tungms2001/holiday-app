package com.example.holiday.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.holiday.R;
import com.example.holiday.model.Notification;
import com.example.holiday.adapter.NotificationRecyclerViewAdapter;
import com.example.holiday.helper.Session;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationFragment extends Fragment {

    private Session session;
    private List<Notification> notifications;
    private NotificationRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvNotification = view.findViewById(R.id.rv_my_notifications);
        session = new Session(getActivity());
        notifications = new Vector<>();//tạo vector rổng
        adapter = new NotificationRecyclerViewAdapter(getActivity(), notifications);

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=notification&action=load_all&username=" + session.getUsername();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//lấy tất cả những dữ liệu này về
                        notifications.add(new Notification(//truyền nó vào notification
                                jsonObject.getInt("tour_id"),
                                jsonObject.getString("avatar"),
                                jsonObject.getString("sender_id"),
                                jsonObject.getString("sender_name"),
                                jsonObject.getString("tour_name"),
                                jsonObject.getString("status")
                        ));
                    }
                    adapter = new NotificationRecyclerViewAdapter(getActivity(), notifications);
                    getActivity().runOnUiThread(() -> {//đưa ra luồn hiển thị ra màn hình
                        rvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvNotification.setAdapter(adapter);

                        adapter.setOnItemClickListener(position -> {//bắt sự kiện chấp nhận thêm vào tour
                            String urlAccept = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=accept&username="
                                    + notifications.get(position).getCreatorId() + "&id=" + notifications.get(position).getTourId();
                            Request acceptRequest = new Request.Builder()
                                    .url(urlAccept)
                                    .build();
                            client.newCall(acceptRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) { }
                            });
                        });
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}