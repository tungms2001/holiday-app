package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holiday.helper.Comment;
import com.example.holiday.helper.CommentRecyclerViewAdapter;
import com.example.holiday.helper.Session;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TourDetailActivity extends AppCompatActivity {

    private Session session;
    private String Id;

    private TextView tvName;
    private TextView tvType;
    private TextView tvStatus;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvDuring;
    private TextView tvNote;
    private ImageView ivAvatar;
    private Button btnApply;

    private RecyclerView rvComment;
    private EditText txtComment;
    private ImageButton ibComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        map();

        session = new Session(TourDetailActivity.this);
        int position = getIntent().getIntExtra("position", -1);
        String keyword = (getIntent().getStringExtra("keyword") != null)
                ? getIntent().getStringExtra("keyword") : "";

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=get_detail&position="
                + position + "&keyword=" + keyword;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                TourDetailActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Id = jsonObject.getString("id");
                        tvName.setText(jsonObject.getString("tour_name"));
                        tvType.setText(jsonObject.getString("type"));
                        tvStatus.setText(jsonObject.getString("status"));
                        tvFrom.setText(jsonObject.getString("departure"));
                        tvTo.setText(jsonObject.getString("destination"));
                        tvDuring.setText(jsonObject.getString("during"));
                        tvNote.setText(jsonObject.getString("note"));
                        String urlImage = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("image");
                        Picasso.get().load(urlImage).into(ivAvatar);
                        refreshComments();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        btnApply.setOnClickListener(v -> {
            if (btnApply.getText().toString().equals("Apply")) {
                String applyUrl = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=notification&action=apply&tour_id="
                        + Id + "&sender=" + session.getUsername();
                Request applyRequest = new Request.Builder()
                        .url(applyUrl)
                        .build();
                client.newCall(applyRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) { }
                });
            }
        });

        ibComment.setOnClickListener(v -> {
            if (!txtComment.getText().toString().isEmpty()) {
                RequestBody body = new FormBody.Builder()
                        .add("tour_id", Id)
                        .add("username", session.getUsername())
                        .add("content", txtComment.getText().toString())
                        .build();
                String commentUrl = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=comment&action=create";
                Request commentRequest = new Request.Builder()
                        .post(body)
                        .url(commentUrl)
                        .build();
                client.newCall(commentRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        TourDetailActivity.this.runOnUiThread(() -> {
                            txtComment.setText("");
                            txtComment.clearFocus();
                            refreshComments();
                        });
                    }
                });
            }
        });
    }

    private void map() {
        tvName = findViewById(R.id.tv_tour_name);
        tvType = findViewById(R.id.tv_tour_type);
        tvStatus = findViewById(R.id.tv_tour_status);
        tvFrom = findViewById(R.id.tv_tour_departure);
        tvTo = findViewById(R.id.tv_tour_destination);
        tvDuring = findViewById(R.id.tv_tour_during);
        tvNote = findViewById(R.id.tv_tour_note);
        ivAvatar = findViewById(R.id.iv_tour_image);
        btnApply = findViewById(R.id.btn_apply);
        rvComment = findViewById(R.id.rv_comment);
        txtComment = findViewById(R.id.txt_comment);
        ibComment = findViewById(R.id.ib_comment);
    }

    private void refreshComments() {
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=comment&action=load_all&tour_id=" + Id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                TourDetailActivity.this.runOnUiThread(() -> {
                    List<Comment> comments = new Vector<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            comments.add(new Comment(jsonObject.getString("avatar"), jsonObject.getString("fullname"), jsonObject.getString("content")));
                        }
                        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(TourDetailActivity.this, comments);
                        rvComment.setLayoutManager(new LinearLayoutManager(TourDetailActivity.this));
                        rvComment.setAdapter(adapter);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}