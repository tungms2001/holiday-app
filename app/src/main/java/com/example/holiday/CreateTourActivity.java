package com.example.holiday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.holiday.helper.Session;
import com.google.android.gms.common.util.Base64Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateTourActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private Bitmap bitmap;
    private Session session;

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
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        map();
        initSpinnerAdapter();
        session = new Session(CreateTourActivity.this);

        btnTourImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        });

        btnCreate.setOnClickListener(v -> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            String base64Data = Base64Utils.encode(stream.toByteArray());

            OkHttpClient client = new OkHttpClient();
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
            String url = "http://10.0.2.2:8080/holidayapp/server/index.php?controller=tour&action=create";
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    CreateTourActivity.this.runOnUiThread(() -> {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success"))
                                finish();
                            Toast.makeText(CreateTourActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
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
        btnCreate = findViewById(R.id.btn_tour_create);
    }
    private void initSpinnerAdapter() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                CreateTourActivity.this, R.array.tour_type, R.layout.support_simple_spinner_dropdown_item);
        spnTourType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                CreateTourActivity.this, R.array.tour_status, R.layout.support_simple_spinner_dropdown_item);
        spnTourStatus.setAdapter(statusAdapter);
    }
}