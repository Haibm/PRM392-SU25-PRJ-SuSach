package com.example.susach.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.susach.databinding.ActivityEventDetailBinding;
import com.example.susach.managers.EventManager;
import com.example.susach.models.Event;
import android.view.View;
import com.example.susach.models.SavePost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Date;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

public class EventDetailActivity extends AppCompatActivity {
    private ActivityEventDetailBinding binding;
    private EventManager eventManager;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventManager = new EventManager();
        String eventId = getIntent().getStringExtra("event_id");
        loadEventDetail(eventId);

        binding.btnSaveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEvent == null) {
                    Toast.makeText(EventDetailActivity.this, "Chưa tải xong dữ liệu sự kiện!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("saved_post")
                        .whereEqualTo("eventId", currentEvent.getId())
                        .whereEqualTo("userId", userId)
                        .limit(1);
                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(EventDetailActivity.this, "Bài viết đã được lưu!", Toast.LENGTH_SHORT).show();
                    } else {
                        SavePost savePost = new SavePost(currentEvent.getId(), currentEvent.getName(), new Date(), userId);
                        db.collection("saved_post")
                                .add(savePost.toMap())
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(EventDetailActivity.this, "Đã lưu bài viết!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EventDetailActivity.this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(EventDetailActivity.this, "Lỗi kiểm tra dữ liệu!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadEventDetail(String eventId) {
        eventManager.getEventById(eventId, new EventManager.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                if (event != null) {
                    currentEvent = event;
                    binding.tvName.setText(event.getName());
                    binding.tvDescription.setText(event.getDescription());
                    binding.tvContents.setText(event.getContents());
                    binding.tvStartDate.setText(String.valueOf(event.getStartDate()));
                    binding.tvEndDate.setText(String.valueOf(event.getEndDate()));
                    // binding.tvImageUrl.setText(event.getImageUrl()); // Đã bỏ trường này
                    binding.tvSummary.setText(event.getSummary());
                    binding.tvImageContent.setText(event.getImageContent());
                    binding.tvCreateBy.setText(event.getCreateBy());
                    binding.tvUpdateBy.setText(event.getUpdateBy());

                    // Load ảnh bằng Java thuần
                    loadImageFromUrl(event.getImageUrl(), binding.ivEventImage);
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Lỗi tải chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void loadImageFromUrl(String url, ImageView imageView) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    InputStream in = new URL(params[0]).openStream();
                    return BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }.execute(url);
    }
} 