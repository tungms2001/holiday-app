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
import com.example.holiday.helper.Session;
import com.example.holiday.model.Tour;
import com.example.holiday.adapter.TourRecyclerViewAdapter;

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

public class HistoryFragment extends Fragment {
//lớp này tương tự TourFragment-khác chổ không thể thao tác nào được chỉ để đọc
    private Session session;
    private List<Tour> tours;

    private RecyclerView rvTours;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTours = view.findViewById(R.id.rv_tours);

        tours = new Vector<>();
        session = new Session(getActivity());
        refreshData();
    }

    private void refreshData() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=load_by_username&username=" + session.getUsername();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        tours.add(new Tour(
                                jsonObject.getString("tour_name"),
                                jsonObject.getString("type"),
                                jsonObject.getString("status"),
                                jsonObject.getString("during"),
                                jsonObject.getString("image")
                        ));
                    }
                    getActivity().runOnUiThread(() -> {
                        TourRecyclerViewAdapter adapter = new TourRecyclerViewAdapter(getActivity(), tours);
                        rvTours.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvTours.setAdapter(adapter);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}