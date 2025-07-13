package com.example.susach.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.R;
import com.example.susach.models.SavePost;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SavePostAdapter extends RecyclerView.Adapter<SavePostAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(SavePost savePost);
    }

    private List<SavePost> savePosts;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public SavePostAdapter(List<SavePost> savePosts, OnItemClickListener listener) {
        this.savePosts = savePosts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_save_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavePost post = savePosts.get(position);
        holder.tvName.setText(post.getNameEvent());
        holder.tvDate.setText(dateFormat.format(post.getSavedAt()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(post));
    }

    @Override
    public int getItemCount() {
        return savePosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_save_post_name);
            tvDate = itemView.findViewById(R.id.tv_save_post_date);
        }
    }
} 