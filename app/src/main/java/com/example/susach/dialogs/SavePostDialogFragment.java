package com.example.susach.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.R;
import com.example.susach.activities.EventDetailActivity;
import com.example.susach.models.SavePost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavePostDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private Button btnClose;
    private SavePostAdapter adapter;
    private List<SavePost> savePostList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_save_post, container, false);
        recyclerView = view.findViewById(R.id.rv_saved_posts);
        btnClose = view.findViewById(R.id.btn_close_saved_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavePostAdapter(savePostList, new SavePostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SavePost savePost) {
                // Mở EventDetailActivity với eventId
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                intent.putExtra("event_id", savePost.getEventId());
                startActivity(intent);
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        btnClose.setOnClickListener(v -> dismiss());
        loadSavedPosts();
        return view;
    }

    private void loadSavedPosts() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("saved_post")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savePostList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        SavePost post = doc.toObject(SavePost.class);
                        savePostList.add(post);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tải danh sách!", Toast.LENGTH_SHORT).show();
                });
    }
} 