package com.example.holiday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.holiday.adapter.MemberAddingRecyclerViewAdapter;
import com.example.holiday.helper.Session;
import com.google.android.gms.common.util.Base64Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateTourActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private int tourId;
    private Bitmap bitmapImage;

    private Session session;
    private ArrayAdapter<CharSequence> typeAdapter;
    private ArrayAdapter<CharSequence> statusAdapter;

    private List<String> usernames;
    private List<String> addedUsernames;
    private MemberAddingRecyclerViewAdapter adapter;

    private EditText txtTourName;
    private Spinner spnTourType;
    private Spinner spnTourStatus;
    private EditText txtTourDeparture;
    private EditText txtTourDestination;
    private EditText txtTourDuring;
    private AutoCompleteTextView actvMember;
    private Button btnAddMember;
    private RecyclerView rvMembers;
    private EditText txtTourNote;
    private Button btnTourImage;
    private ImageView ivTourImage;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        map();
        initAdapter();

        tourId = -1;
        session = new Session(CreateTourActivity.this);
        addedUsernames = new ArrayList<>();
        rvMembers.setLayoutManager(new GridLayoutManager(CreateTourActivity.this, 3));
        adapter = new MemberAddingRecyclerViewAdapter(CreateTourActivity.this, addedUsernames);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");//để nó ở nhiều chế độ, để tận dụng giữa tạo chuyến và sửa chuyến
        if (mode.equals("update")) {
            // id for update later
            tourId = Integer.parseInt(intent.getStringExtra("id"));

            // set value for some basic View
            txtTourName.setText(intent.getStringExtra("name"));
            spnTourType.setSelection(typeAdapter.getPosition(intent.getStringExtra("type")));
            spnTourStatus.setSelection(statusAdapter.getPosition(intent.getStringExtra("status")));
            txtTourDeparture.setText(intent.getStringExtra("from"));
            txtTourDestination.setText(intent.getStringExtra("to"));
            txtTourDuring.setText(intent.getStringExtra("during"));
            txtTourNote.setText(intent.getStringExtra("note"));

            // receive bitmap from server through Picasso, set bitmap to ivTourImage
            Picasso.get().load(intent.getStringExtra("image")).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    bitmapImage = bitmap;
                    ivTourImage.setImageBitmap(bitmapImage);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            });
            // load available username to members
            addedUsernames = intent.getStringArrayListExtra("usernames");
            adapter = new MemberAddingRecyclerViewAdapter(CreateTourActivity.this, addedUsernames);
            rvMembers.setAdapter(adapter);

            btnSubmit.setText(getString(R.string.update));
        }

        // event load image
        btnTourImage.setOnClickListener(v -> {
            Intent imageIntent = new Intent(Intent.ACTION_PICK);
            imageIntent.setType("image/*");
            startActivityForResult(imageIntent, SELECT_PHOTO);
        });

        // event add a member
        btnAddMember.setOnClickListener(v -> {
            if (isValidUsername(actvMember.getText().toString()) != -1) {
                addedUsernames.add(actvMember.getText().toString());
                adapter = new MemberAddingRecyclerViewAdapter(CreateTourActivity.this, addedUsernames);
                rvMembers.setAdapter(adapter);
                actvMember.setText("");

                adapter.setOnItemClickListener(position -> {
                    addedUsernames.remove(position);
                    adapter.notifyItemRemoved(position);
                });
            }
        });

        // event remove a member
        adapter.setOnItemClickListener(position -> {
            addedUsernames.remove(position);
            adapter.notifyItemRemoved(position);
        });

        // event submit (create or update) a tour
        btnSubmit.setOnClickListener(v -> {//điền hết tất cả các thông tin
            if (bitmapImage != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                String base64Data = Base64Utils.encode(stream.toByteArray());

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", String.valueOf(tourId))
                        .addFormDataPart("creator", session.getUsername())
                        .addFormDataPart("tour_name", txtTourName.getText().toString())
                        .addFormDataPart("type", spnTourType.getSelectedItem().toString())
                        .addFormDataPart("status", spnTourStatus.getSelectedItem().toString())
                        .addFormDataPart("departure", txtTourDeparture.getText().toString())
                        .addFormDataPart("destination", txtTourDestination.getText().toString())
                        .addFormDataPart("during", txtTourDuring.getText().toString())
                        .addFormDataPart("members", TextUtils.join(" ", addedUsernames))
                        .addFormDataPart("note", txtTourNote.getText().toString())
                        .addFormDataPart("image", base64Data)
                        .build();
                String url;
                if (mode.equals("create"))//nếu là tạo thì đưa dữ liệu vào tạo
                    url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=create";
                else// ngược lại đưa dữ liệu vào update tour
                    url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=update";
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        CreateTourActivity.this.runOnUiThread(() -> {
                            JSONObject jsonObject;
                            try {//dữ liệu được nhận về
                                jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getBoolean("success"))
                                    finish();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
            else {
                Toast.makeText(this, "Please update your image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // receive image from result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ivTourImage.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // map between java and xml control
    private void map() {
        txtTourName = findViewById(R.id.txt_tour_name);
        spnTourType = findViewById(R.id.spn_tour_type);
        spnTourStatus = findViewById(R.id.spn_tour_status);
        txtTourDeparture = findViewById(R.id.txt_tour_departure);
        txtTourDestination = findViewById(R.id.txt_tour_destination);
        txtTourDuring = findViewById(R.id.txt_tour_during);
        actvMember = findViewById(R.id.actv_member);
        btnAddMember = findViewById(R.id.btn_add_member);
        rvMembers = findViewById(R.id.rv_members);
        txtTourNote = findViewById(R.id.txt_tour_note);
        btnTourImage = findViewById(R.id.btn_tour_image);
        ivTourImage = findViewById(R.id.iv_tour_image);
        btnSubmit = findViewById(R.id.btn_tour_create);
    }

    // initial adapter for type and status adapter/lấy các tên từ string trong xml đưa vào spinner
    private void initAdapter() {
        // initial adapter from string resource
        typeAdapter = ArrayAdapter.createFromResource(
                CreateTourActivity.this, R.array.tour_type, R.layout.support_simple_spinner_dropdown_item);
        spnTourType.setAdapter(typeAdapter);
        statusAdapter = ArrayAdapter.createFromResource(
                CreateTourActivity.this, R.array.tour_status, R.layout.support_simple_spinner_dropdown_item);
        spnTourStatus.setAdapter(statusAdapter);

        // get username adapter from server/lấy hết tin người dùng về rồi đưa vào gợi ý
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=user&action=get_all_usernames";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                CreateTourActivity.this.runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        usernames = new Vector<>();
                        for (int i = 0; i < jsonArray.length(); i++)
                            usernames.add(jsonArray.getJSONObject(i).getString("username"));
                        ArrayAdapter<String> usernameAdapter =
                                new ArrayAdapter<>(CreateTourActivity.this, R.layout.support_simple_spinner_dropdown_item, usernames);
                        actvMember.setAdapter(usernameAdapter);//thực hiện gợi ý tên người dùng
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    // check existed username
    private int isValidUsername(String username) {
        for (int i = 0; i < usernames.size(); i++)
            if (usernames.get(i).equals(username))
                return i;
        return -1;
    }
}