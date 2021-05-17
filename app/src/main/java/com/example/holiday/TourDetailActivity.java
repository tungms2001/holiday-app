package com.example.holiday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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

    private OkHttpClient client;
    private Session session;
    private String Id;
    private ArrayAdapter<CharSequence> typeAdapter;
    private ArrayAdapter<CharSequence> statusAdapter;

    private EditText txtTourName;
    private Spinner spnTourType;
    private Spinner spnTourStatus;
    private EditText txtTourDeparture;
    private EditText txtTourDestination;
    private EditText txtTourDuring;
    private EditText txtTourNote;
    private Button btnTourImage;
    private ImageView ivTourImage;
    private Button btnSubmit;
    private RecyclerView rvComment;
    private EditText txtComment;
    private Button btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        init();
        constructData();

        btnComment.setOnClickListener(v -> {
            client = new OkHttpClient();
            if (!txtComment.getText().toString().isEmpty()) {
                RequestBody body = new FormBody.Builder()
                        .add("tour_id", Id)
                        .add("username", session.getUsername())
                        .add("content", txtComment.getText().toString())
                        .build();
                String commentUrl = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=Tour&action=comment";
                Request commentRequest = new Request.Builder()
                        .post(body)
                        .url(commentUrl)
                        .build();
                client.newCall(commentRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

    private void init() {
        txtTourName = findViewById(R.id.txt_tour_name);
        spnTourType = findViewById(R.id.spn_tour_type);
        spnTourStatus = findViewById(R.id.spn_tour_status);
        txtTourDeparture = findViewById(R.id.txt_tour_departure);
        txtTourDestination = findViewById(R.id.txt_tour_destination);
        txtTourDuring = findViewById(R.id.txt_tour_during);
        txtTourNote = findViewById(R.id.txt_tour_note);
        btnTourImage = findViewById(R.id.btn_tour_image);
        ivTourImage = findViewById(R.id.iv_tour_image);
        btnSubmit = findViewById(R.id.btn_tour_submit);
        rvComment = findViewById(R.id.rv_comment);
        txtComment = findViewById(R.id.txt_comment);
        btnComment = findViewById(R.id.btn_comment);

        typeAdapter = ArrayAdapter.createFromResource(TourDetailActivity.this, R.array.tour_type, R.layout.support_simple_spinner_dropdown_item);
        spnTourType.setAdapter(typeAdapter);
        statusAdapter = ArrayAdapter.createFromResource(TourDetailActivity.this, R.array.tour_status, R.layout.support_simple_spinner_dropdown_item);
        spnTourStatus.setAdapter(statusAdapter);
        client = new OkHttpClient();
    }

    private void constructData() {
        session = new Session(TourDetailActivity.this);
        int position = getIntent().getIntExtra("position", -1);
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=Tour&action=load_by_position&position="
                + String.valueOf(position);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                TourDetailActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Id = jsonObject.getString("id");
                        txtTourName.setText(jsonObject.getString("tour_name"));
                        spnTourType.setSelection(typeAdapter.getPosition(jsonObject.getString("type")));
                        spnTourStatus.setSelection(statusAdapter.getPosition(jsonObject.getString("status")));
                        txtTourDeparture.setText(jsonObject.getString("departure"));
                        txtTourDestination.setText(jsonObject.getString("destination"));
                        txtTourDuring.setText(jsonObject.getString("during"));
                        txtTourNote.setText(jsonObject.getString("note"));
                        String urlImage = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("image");
                        Picasso.get().load(urlImage).into(ivTourImage);

                        String creator = jsonObject.getString("creator");
                        if (session.getUsername().equals(jsonObject.getString("creator"))) {
                            btnSubmit.setText("Update");
                        }
                        else {
                            txtTourName.setEnabled(false);
                            spnTourType.setEnabled(false);
                            spnTourStatus.setEnabled(false);
                            txtTourDeparture.setEnabled(false);
                            txtTourDestination.setEnabled(false);
                            txtTourDuring.setEnabled(false);
                            txtTourNote.setEnabled(false);
                            btnSubmit.setText("Apply");
                        }
                        submit(creator);
                        refreshComments();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void submit(String creator) {
        btnSubmit.setOnClickListener(v -> {
            if (btnSubmit.getText().toString().equals("Apply")) {
                String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=Notification&action=apply&tour_id="
                        + Id + "&sender=" + session.getUsername() + "&receiver=" + creator;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException { }
                });
            }
            else {

            }
        });
    }


    private void refreshComments() {
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=Tour&action=load_comments&id=" + Id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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