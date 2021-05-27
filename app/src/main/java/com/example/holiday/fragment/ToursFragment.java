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
import androidx.appcompat.widget.SearchView;

import com.example.holiday.CreateTourActivity;
import com.example.holiday.R;
import com.example.holiday.TourDetailActivity;
import com.example.holiday.helper.RecyclerItemClickListener;
import com.example.holiday.model.Tour;
import com.example.holiday.adapter.TourRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ToursFragment extends Fragment {
//Lớp này chứa mọi thông tin về các chuyến đi và những tính năng của nó
    private List<Tour> tours;//danh sách các chuyến đi
    private TourRecyclerViewAdapter adapter;//chuyến đi vừa mới tạo

    private SearchView svTour;//thanh tìm kiếm
    private RecyclerView rvTours;//chứa một danh sách các tour khác nhau
    private FloatingActionButton fabCreateTour;//nút dùng để tạo ra chuyến đi mới

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tours, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tours = new Vector<>();
        adapter = new TourRecyclerViewAdapter(getActivity(), tours);//khởi tạo

        svTour = view.findViewById(R.id.sv_tour);
        rvTours = view.findViewById(R.id.rv_tours);
        fabCreateTour = view.findViewById(R.id.fab_create_tour);//ánh xạ qua xml

        fabCreateTour.setOnClickListener(v -> {//bấm nào nút này nó intent qua activity tạo chuyến đi mới
            Intent intent = new Intent(getActivity(), CreateTourActivity.class);
            intent.putExtra("mode", "create");
            startActivity(intent);
        });

        rvTours.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvTours, (v, position) ->  {//bấm vào từng chuyến thì ta sẽ xem được chi tiết của một chuyến đi
            Intent intent = new Intent(getActivity(), TourDetailActivity.class);
            if (!svTour.getQuery().toString().isEmpty())//nếu là người tạo ra ta sẽ có thêm chổ thay đổi thông tin
                intent.putExtra("keyword", svTour.getQuery().toString());
            intent.putExtra("position", position);
            startActivity(intent);
        }));

        svTour.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                if (newText.isEmpty())//mổi khi thanh tiềm kiếm thay đổi thì nó sẽ tự cập nhật dữ liệu
                    loadAll();
                return false;
            }
        });
    }

    @Override
    public void onResume() {//sau khi tạo chuyến xong nó sẽ tự load lại dữ liệu ở fragment này
        super.onResume();
        loadAll();
    }

    private void loadAll() {//lấy tất cả chuyến đi về
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=load_all";
        Request request = new Request.Builder()
                .url(url)
                .build();
        refreshData(request);
    }

    private void search(String query) {
        RequestBody body = new FormBody.Builder()
                .add("keyword", query)
                .build();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=search";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        refreshData(request);
    }

    private void refreshData(Request request) {
        tours.clear();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {//thực hiện phương thức bất đồng bộ-hàm lồng hàm
            @Override//Nó sẽ sinh ra 2 luồn: 1 là lấy dữ liệu từ server.
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());//lấy tất cả dữ liệu về ở dạng màng, sau đó parse ra đưa vào tour
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//dùng tính năng của isonObject để lấy dữ liệu ra view
                        tours.add(new Tour(//add trược tiếp để rút gọn hơn
                                jsonObject.getString("tour_name"),
                                jsonObject.getString("type"),
                                jsonObject.getString("status"),
                                jsonObject.getString("during"),
                                jsonObject.getString("image")
                        ));
                    }
                    getActivity().runOnUiThread(() -> {//luồn thứ 2 là luồn đồ họa, để hiển thị ra màn hình(Ui: giao diện người dùng)
                        adapter = new TourRecyclerViewAdapter(getActivity(), tours);//sau đó đưa danh sách tour vào adapter
                        rvTours.setLayoutManager(new LinearLayoutManager(getActivity()));//và set các dữ liệu đó bỏ vào các recycler bên dưới
                        rvTours.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}