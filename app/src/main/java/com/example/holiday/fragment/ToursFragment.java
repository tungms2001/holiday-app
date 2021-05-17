package com.example.holiday.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.holiday.CreateTourActivity;
import com.example.holiday.R;
import com.example.holiday.TourDetailActivity;
import com.example.holiday.helper.RecyclerItemClickListener;
import com.example.holiday.helper.Session;
import com.example.holiday.helper.Tour;
import com.example.holiday.helper.TourRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class ToursFragment extends Fragment {

    Session session;
    private List<Tour> tours;
    private RecyclerView rvTours;
    private FloatingActionButton fabCreateTour;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tours, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTours = view.findViewById(R.id.rv_tours);
        fabCreateTour = view.findViewById(R.id.fab_create_tour);
        tours = new Vector<>();

        fabCreateTour.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateTourActivity.class);
            intent.putExtra("create", false);
            startActivity(intent);
        });

        rvTours.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvTours, (v, position) ->  {
            Intent intent = new Intent(getActivity(), TourDetailActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        tours.clear();
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=Tour&action=load_all";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Tour tour = new Tour();
                        tour.setImage(jsonObject.getString("image"));
                        tour.setTourName(jsonObject.getString("tour_name"));
                        tour.setType(jsonObject.getString("type"));
                        tour.setDuring(jsonObject.getString("during"));
                        tour.setStatus(jsonObject.getString("status"));
                        tours.add(tour);
                    }

                    TourRecyclerViewAdapter adapter = new TourRecyclerViewAdapter(getActivity(), tours);

                    getActivity().runOnUiThread(() -> {
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