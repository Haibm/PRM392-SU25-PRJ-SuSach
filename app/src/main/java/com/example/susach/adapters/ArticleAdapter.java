package com.example.susach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import com.example.susach.models.Event;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    public interface OnActionListener {
        void onUpdate(Event event);
        void onDelete(Event event);
    }
    private List<Event> articleList;
    private OnActionListener listener;

    public ArticleAdapter(List<Event> articleList, OnActionListener listener) {
        this.articleList = articleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event article = articleList.get(position);
        holder.tvName.setText(article.getName());
        holder.tvDesc.setText(article.getDescription());
        holder.btnUpdate.setOnClickListener(v -> listener.onUpdate(article));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(article));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        ImageButton btnUpdate, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_article_name);
            tvDesc = itemView.findViewById(R.id.tv_article_desc);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
} 