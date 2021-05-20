package com.example.holiday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.holiday.helper.Comment;
import com.example.holiday.helper.CommentRecyclerViewAdapter;
import com.example.holiday.helper.Session;
import com.google.android.gms.common.util.Base64Utils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TourDetailActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private Bitmap bitmap;
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
    private EditText txtTourMembers;
    private EditText txtTourNote;
    private Button btnTourImage;
    private ImageView ivTourImage;
    private Button btnSubmit;
    private RecyclerView rvComment;
    private EditText txtComment;
    private ImageButton ibComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        map();
        init();

        session = new Session(TourDetailActivity.this);
        int position = getIntent().getIntExtra("position", -1);
        String keyword = (getIntent().getStringExtra("keyword") != null) ? getIntent().getStringExtra("keyword") : "";

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
                            btnSubmit.setText(getString(R.string.update));
                            btnSubmit.setOnClickListener(v -> {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                String base64Data = Base64Utils.encode(stream.toByteArray());

                                RequestBody body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("creator", session.getUsername())
                                        .addFormDataPart("tour_name", txtTourName.getText().toString())
                                        .addFormDataPart("type", spnTourType.getSelectedItem().toString())
                                        .addFormDataPart("status", spnTourStatus.getSelectedItem().toString())
                                        .addFormDataPart("departure", txtTourDeparture.getText().toString())
                                        .addFormDataPart("destination", txtTourDestination.getText().toString())
                                        .addFormDataPart("during", txtTourDuring.getText().toString())
                                        .addFormDataPart("members", txtTourMembers.getText().toString())
                                        .addFormDataPart("note", txtTourNote.getText().toString())
                                        .addFormDataPart("image", base64Data)
                                        .build();
                                String urlUpdate = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=update";
                                Request requestUpdate = new Request.Builder()
                                        .url(urlUpdate)
                                        .post(body)
                                        .build();
                                client.newCall(requestUpdate).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        try {
                                            JSONObject object = new JSONObject(response.body().string());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            });
                        }
                        else {
                            txtTourName.setEnabled(false);
                            spnTourType.setEnabled(false);
                            spnTourStatus.setEnabled(false);
                            txtTourDeparture.setEnabled(false);
                            txtTourDestination.setEnabled(false);
                            txtTourDuring.setEnabled(false);
                            txtTourNote.setEnabled(false);
                            btnSubmit.setText(getString(R.string.apply));
                            btnTourImage.setVisibility(View.GONE);
                        }
                        submit(creator);
                        refreshComments();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
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

        btnTourImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ivTourImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void map() {
        txtTourName = findViewById(R.id.txt_tour_name);
        spnTourType = findViewById(R.id.spn_tour_type);
        spnTourStatus = findViewById(R.id.spn_tour_status);
        txtTourDeparture = findViewById(R.id.txt_tour_departure);
        txtTourDestination = findViewById(R.id.txt_tour_destination);
        txtTourDuring = findViewById(R.id.txt_tour_during);
        txtTourMembers = findViewById(R.id.txt_tour_members);
        txtTourNote = findViewById(R.id.txt_tour_note);
        btnTourImage = findViewById(R.id.btn_tour_image);
        ivTourImage = findViewById(R.id.iv_tour_image);
        btnSubmit = findViewById(R.id.btn_tour_submit);
        rvComment = findViewById(R.id.rv_comment);
        txtComment = findViewById(R.id.txt_comment);
        ibComment = findViewById(R.id.ib_comment);
    }

    private void init() {
        typeAdapter = ArrayAdapter.createFromResource(TourDetailActivity.this, R.array.tour_type, R.layout.support_simple_spinner_dropdown_item);
        spnTourType.setAdapter(typeAdapter);
        statusAdapter = ArrayAdapter.createFromResource(TourDetailActivity.this, R.array.tour_status, R.layout.support_simple_spinner_dropdown_item);
        spnTourStatus.setAdapter(statusAdapter);
    }

    private void submit(String creator) {
        btnSubmit.setOnClickListener(v -> {
            if (btnSubmit.getText().toString().equals("Apply")) {
                String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=notification&action=apply&tour_id="
                        + Id + "&sender=" + session.getUsername() + "&receiver=" + creator;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) { }
                });
            }
        });
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