package com.example.holiday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holiday.adapter.MemberPresentationRecyclerViewAdapter;
import com.example.holiday.model.Comment;
import com.example.holiday.adapter.CommentRecyclerViewAdapter;
import com.example.holiday.helper.Session;
import com.example.holiday.model.Member;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private String tourId;
    private String creator;
    private String urlImage;

    private int position;
    private String keyword;
    private List<Member> members;

    private TextView tvName;
    private TextView tvType;
    private TextView tvStatus;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvDuring;
    private TextView tvNote;
    private RecyclerView rvMembers;
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
        position = getIntent().getIntExtra("position", -1);
        keyword = (getIntent().getStringExtra("keyword") != null)
                ? getIntent().getStringExtra("keyword") : "";
        refreshDetail();

        // event to apply a tour
        btnApply.setOnClickListener(v -> {
            if (btnApply.getText().toString().equals("Apply")) {
                String applyUrl = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=notification&action=apply&tour_id="
                        + tourId + "&sender=" + session.getUsername();
                Request applyRequest = new Request.Builder()
                        .url(applyUrl)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(applyRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success"))
                                TourDetailActivity.this.runOnUiThread(() -> {//khi đã bấm vào rồi thì không thể bấm lần nữa
                                    btnApply.setText(getString(R.string.applied));
                                    btnApply.setEnabled(false);
                                });
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // event to make a comment
        ibComment.setOnClickListener(v -> {//bấm nút comment để commnet
            if (!txtComment.getText().toString().isEmpty()) {
                RequestBody body = new FormBody.Builder()
                        .add("tour_id", tourId)
                        .add("username", session.getUsername())
                        .add("content", txtComment.getText().toString())
                        .build();
                String commentUrl = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=comment&action=create";
                Request commentRequest = new Request.Builder()
                        .post(body)
                        .url(commentUrl)
                        .build();
                OkHttpClient client = new OkHttpClient();
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshDetail();
    }

    // create option menu when current user as tour's creator
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (session.getUsername().equals(creator)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tour_option_menu, menu);
        }
        return true;
    }

    // choose what to process when touch option menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {//load hết dữ liệu hiện có khi ở chế độ update
            case R.id.update:
                Intent intent = new Intent(TourDetailActivity.this, CreateTourActivity.class);
                intent.putExtra("mode", "update");
                intent.putExtra("id", tourId);
                intent.putExtra("name", tvName.getText().toString());
                intent.putExtra("type", tvType.getText().toString());
                intent.putExtra("status", tvStatus.getText().toString());
                intent.putExtra("from", tvFrom.getText().toString());
                intent.putExtra("to", tvTo.getText().toString());
                intent.putExtra("during", tvDuring.getText().toString());
                intent.putExtra("note", tvNote.getText().toString());
                intent.putExtra("image", urlImage);
                ArrayList<String> usernames =new ArrayList<>();
                for (int i = 0; i < members.size(); i++)
                    usernames.add(members.get(i).getUsername());
                intent.putStringArrayListExtra("usernames", usernames);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // map between java and xml views
    private void map() {
        tvName = findViewById(R.id.tv_tour_name);
        tvType = findViewById(R.id.tv_tour_type);
        tvStatus = findViewById(R.id.tv_tour_status);
        tvFrom = findViewById(R.id.tv_tour_departure);
        tvTo = findViewById(R.id.tv_tour_destination);
        tvDuring = findViewById(R.id.tv_tour_during);
        tvNote = findViewById(R.id.tv_tour_note);
        rvMembers = findViewById(R.id.rv_members);
        ivAvatar = findViewById(R.id.iv_tour_image);
        btnApply = findViewById(R.id.btn_apply);

        rvComment = findViewById(R.id.rv_comment);
        txtComment = findViewById(R.id.txt_comment);
        ibComment = findViewById(R.id.ib_comment);
    }

    // refresh tour's detail, data from server
    private void refreshDetail() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=get_detail&position="
                + position + "&keyword=" + keyword + "&username=" + session.getUsername();
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
                        tourId = jsonObject.getString("id");
                        creator = jsonObject.getString("creator");
                        tvName.setText(jsonObject.getString("tour_name"));
                        tvType.setText(jsonObject.getString("type"));
                        tvStatus.setText(jsonObject.getString("status"));
                        tvFrom.setText(jsonObject.getString("departure"));
                        tvTo.setText(jsonObject.getString("destination"));
                        tvDuring.setText(jsonObject.getString("during"));
                        tvNote.setText(jsonObject.getString("note"));

                        urlImage = "http://10.0.2.2:8080/holidayapp/server/" + jsonObject.getString("image");
                        Picasso.get().load(urlImage).into(ivAvatar);

                        // load members
                        boolean isMember = false;
                        JSONArray jsonArray = jsonObject.getJSONArray("members");
                        members = new Vector<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject member = jsonArray.getJSONObject(i);
                            members.add(new Member(
                                    member.getString("avatar"),
                                    member.getString("username")
                            ));
                            if (session.getUsername().equals(member.getString("username")))
                                isMember = true;
                        }
                        MemberPresentationRecyclerViewAdapter adapter = new MemberPresentationRecyclerViewAdapter(
                                TourDetailActivity.this, members);
                        rvMembers.setLayoutManager(new GridLayoutManager(TourDetailActivity.this, 4));
                        rvMembers.setAdapter(adapter);

                        if (jsonObject.getString("notification_status").equals("pending")) {
                            btnApply.setText(getString(R.string.applied));
                            btnApply.setEnabled(false);
                        }

                        // if user as creator, there is no apply button
                        if (session.getUsername().equals(creator) || isMember)
                            btnApply.setVisibility(View.GONE);

                        refreshComments();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    // refresh tour's comments, data from server
    private void refreshComments() {
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=comment&action=load_all&tour_id=" + tourId;
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