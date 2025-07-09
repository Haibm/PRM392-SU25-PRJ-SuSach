package com.example.susach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import java.util.List;

public class QuizSetAdapter extends RecyclerView.Adapter<QuizSetAdapter.QuizSetViewHolder> {
    public interface QuizSetListener {
        void onQuizSetClick(String quizSetName);
        void onEditQuizSet(String quizSetName);
        void onDeleteQuizSet(String quizSetName);
    }

    private List<String> quizSetList;
    private QuizSetListener listener;

    public QuizSetAdapter(List<String> quizSetList, QuizSetListener listener) {
        this.quizSetList = quizSetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_set, parent, false);
        QuizSetViewHolder holder = new QuizSetViewHolder(view);
        bindingViewHolder(holder);
        bindingActionHolder(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizSetViewHolder holder, int position) {
        String quizSetName = quizSetList.get(position);
        holder.tvQuizSetName.setText(quizSetName);
        holder.itemView.setTag(quizSetName);
        holder.btnEditQuizSet.setTag(quizSetName);
        holder.btnDeleteQuizSet.setTag(quizSetName);
    }

    @Override
    public int getItemCount() {
        return quizSetList.size();
    }

    private void bindingViewHolder(QuizSetViewHolder holder) {
        holder.tvQuizSetName = holder.itemView.findViewById(R.id.tvQuizSetName);
        holder.btnEditQuizSet = holder.itemView.findViewById(R.id.btnEditQuizSet);
        holder.btnDeleteQuizSet = holder.itemView.findViewById(R.id.btnDeleteQuizSet);
    }

    private void bindingActionHolder(QuizSetViewHolder holder) {
        holder.itemView.setOnClickListener(this::onQuizSetClick);
        holder.btnEditQuizSet.setOnClickListener(this::onEditQuizSetClick);
        holder.btnDeleteQuizSet.setOnClickListener(this::onDeleteQuizSetClick);
    }

    private void onQuizSetClick(View v) {
        if (listener != null) {
            String quizSetName = (String) v.getTag();
            listener.onQuizSetClick(quizSetName);
        }
    }

    private void onEditQuizSetClick(View v) {
        if (listener != null) {
            String quizSetName = (String) v.getTag();
            listener.onEditQuizSet(quizSetName);
        }
    }

    private void onDeleteQuizSetClick(View v) {
        if (listener != null) {
            String quizSetName = (String) v.getTag();
            listener.onDeleteQuizSet(quizSetName);
        }
    }

    static class QuizSetViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizSetName;
        Button btnEditQuizSet, btnDeleteQuizSet;
        public QuizSetViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
} 