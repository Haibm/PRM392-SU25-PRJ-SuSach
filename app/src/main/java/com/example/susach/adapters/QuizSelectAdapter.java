package com.example.susach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.R;
import com.example.susach.models.QuizSetInfo;

import java.util.List;

public class QuizSelectAdapter extends RecyclerView.Adapter<QuizSelectAdapter.QuizSelectViewHolder> {
    public interface QuizSelectListener {
        void onQuizSelectClick(String quizSetName);
    }

    private List<QuizSetInfo> quizSetList;
    private QuizSelectListener listener;

    public QuizSelectAdapter(List<QuizSetInfo> quizSetList, QuizSelectListener listener) {
        this.quizSetList = quizSetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_select, parent, false);
        QuizSelectViewHolder holder = new QuizSelectViewHolder(view);
        bindingViewHolder(holder);
        bindingActionHolder(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizSelectViewHolder holder, int position) {
        QuizSetInfo quizSetInfo = quizSetList.get(position);
        holder.tvQuizSetName.setText(quizSetInfo.name);
        holder.tvQuizSetDesc.setText("Bộ đề gồm " + quizSetInfo.quizCount + " câu hỏi");
        holder.btnStartQuiz.setOnClickListener(v -> {
            if (listener != null) listener.onQuizSelectClick(quizSetInfo.name);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onQuizSelectClick(quizSetInfo.name);
        });
    }

    @Override
    public int getItemCount() {
        return quizSetList.size();
    }

    private void bindingViewHolder(QuizSelectViewHolder holder) {
        holder.tvQuizSetName = holder.itemView.findViewById(R.id.tvQuizSetName);
    }

    private void bindingActionHolder(QuizSelectViewHolder holder) {
        holder.itemView.setOnClickListener(this::onQuizSelectClick);
    }

    private void onQuizSelectClick(View v) {
        if (listener != null) {
            String quizSetName = (String) v.getTag();
            listener.onQuizSelectClick(quizSetName);
        }
    }

    static class QuizSelectViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizSetName, tvQuizSetDesc;
        Button btnStartQuiz;
        public QuizSelectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizSetName = itemView.findViewById(R.id.tvQuizSetName);
            tvQuizSetDesc = itemView.findViewById(R.id.tvQuizSetDesc);
            btnStartQuiz = itemView.findViewById(R.id.btnStartQuiz);
        }
    }
}
